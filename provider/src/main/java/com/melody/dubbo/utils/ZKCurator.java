package com.melody.dubbo.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;

@Slf4j
public class ZKCurator {
    private CuratorFramework client = null;

    public ZKCurator(CuratorFramework client) {
        this.client = client;
    }

    /**
     *  初始化操作
     */
    public void init(){
        //使用命名空间
        client = client.usingNamespace("zk-curator-connector");
    }
    /**
     * 判断ZK是否连接
     */
    public CuratorFrameworkState isZKAlive(){
        return client.getState();
    }

}
