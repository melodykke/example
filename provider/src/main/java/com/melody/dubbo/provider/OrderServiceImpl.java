package com.melody.dubbo.provider;


import com.alibaba.dubbo.config.annotation.Service;
import com.melody.dubbo.api.OrderService;
import com.melody.dubbo.dao.OrderDAO;
import com.melody.dubbo.entity.LapTop;
import com.melody.dubbo.utils.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Random;


@Slf4j
@Service
public class OrderServiceImpl implements OrderService{


    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private DistributedLock distributedLock;

    @Override
    public Integer orderCP(Integer num) {
        System.out.println("orderCP called..................");
        //执行订单流程之前使得当前业务获得分布式锁
        distributedLock.getLock();
        LapTop lapTop = orderDAO.findOne(2);
        if(lapTop.getQuntity() < num){
            log.info("库存不足！ 库存量：{} ； 请求数量：{}", lapTop.getQuntity(), num);
            distributedLock.releaseLock();
            return -1;
        }
        lapTop.setQuntity(lapTop.getQuntity() - num);
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        orderDAO.save(lapTop);
        distributedLock.releaseLock();
        return 100;
    }

    public Long queryStockNum() {
        Random random = new Random();
        try {
            Thread.sleep(random.nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (long)353;
    }



}
