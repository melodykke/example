package com.melody.dubbo.provider;


import com.alibaba.dubbo.config.annotation.Service;
import com.melody.dubbo.api.OrderService;
import com.melody.dubbo.dao.OrderDAO;
import com.melody.dubbo.entity.LapTop;
import com.melody.dubbo.utils.ZKCurator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.Random;


@Slf4j
@Service
public class OrderServiceImpl implements OrderService{


    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private ZKCurator zkCurator;

    @Override
    public void orderCP(Integer num) {
        System.out.println("orderCP called..................");
        LapTop lapTop = orderDAO.findOne(2);
        if(lapTop.getQuntity() < num){
            log.info("库存不足！ 库存量：{} ； 请求数量：{}", lapTop.getQuntity(), num);
            return ;
        }
        lapTop.setQuntity(lapTop.getQuntity() - num);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        orderDAO.save(lapTop);
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
