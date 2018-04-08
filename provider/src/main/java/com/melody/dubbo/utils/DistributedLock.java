package com.melody.dubbo.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

import java.util.concurrent.CountDownLatch;

/**
 * 分布式锁的实现工具类
 */
@Slf4j
public class DistributedLock {
    private CuratorFramework client = null;
    //用于挂起当前请求，并且等待上一个分布式锁释放
    private static CountDownLatch zkLockLatch = new CountDownLatch(1);
    //分布式锁 总结点名
    private static final String ZK_LOCK_PROJECT = "imooc-locks";
    //分布式锁节点
    private static final String DISTRIBUTED_LOCK = "distributed-lock";
    //构造函数
    public DistributedLock(CuratorFramework client){
        this.client = client;
    }

    /**
     * 初始化锁
     */
    public void init(){
        //使用命名空间
        client = client.usingNamespace("ZKLocks-Namespace");

        /**
         * 创建zk锁的总结点，相当于eclipse的工作空间下的项目
         * ZKLocks-Namespace
         *             |
         *             imooc-locks
         *                      |
         *                      distributed-lcok
         */
        try{
            if(client.checkExists().forPath("/" + ZK_LOCK_PROJECT) == null){
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath("/" + ZK_LOCK_PROJECT);
            }
            //针对zk分布式锁节点， 创建相应的锁watcher事件监听
            addWatcherToLock("/" + ZK_LOCK_PROJECT);
        } catch (Exception e){
            log.error("客户端连接zk服务器错误， 请重试！");
        }
    }

    /**
     * 获得分布式锁
     */
    public void getLock(){
        //使用死循环， 当且仅当上一个锁释放 并且当前请求获得锁成功后才会跳出
        while (true){
            try {
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath("/" + ZK_LOCK_PROJECT + "/" + DISTRIBUTED_LOCK);
                log.info("获得分布式锁成功！");
                return;
            }catch (Exception e){
                log.info("获取分布式锁失败！");
                try{
                    //如果没有获取到锁，需要重新设置同步资源值。
                    if(zkLockLatch.getCount() <= 0){
                        zkLockLatch = new CountDownLatch(1);
                    }
                    zkLockLatch.await();
                }catch (InterruptedException ie){
                    ie.printStackTrace();
                }

            }
        }
    }

    /**
     * 创建watcher监听
     */
    public void addWatcherToLock(String path) throws Exception{
        final PathChildrenCache cache = new PathChildrenCache(client, path, true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                if(pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)){
                    String path = pathChildrenCacheEvent.getData().getPath();
                    log.info("上一个会话已经释放锁 或该会话已经断开， 节点路径： " + path);
                    if(path.contains(DISTRIBUTED_LOCK)){
                        log.info("释放计数器， 让当前请求来获得分布式锁");
                        zkLockLatch.countDown();
                    }
                }
            }
        });
    }

    /**
     * 释放分布式锁
     */
    public boolean releaseLock(){
        try{
            if (client.checkExists().forPath("/" + ZK_LOCK_PROJECT + "/" + DISTRIBUTED_LOCK) != null){
                client.delete().forPath("/" + ZK_LOCK_PROJECT + "/" + DISTRIBUTED_LOCK);
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        log.info("分布式锁释放完毕！");
        return true;
    }
}
