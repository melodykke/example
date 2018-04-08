package com.melody.dubbo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.melody.dubbo.api.OrderService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RestController
public class OrderController  {

    public static CountDownLatch countDown;

    @Reference
    private OrderService orderService;

    @GetMapping("/order-taptop/{num}")
    public Integer orderLapTop(@PathVariable("num") Integer num){

        return orderService.orderCP(num);
    }
    @GetMapping("/order-taptop1/{num}")
    public Integer orderLapTop1(@PathVariable("num") Integer num){

        return orderService.orderCP(num);
    }

    @GetMapping("/queryStockNum")
    public Long queryStockNum() throws InterruptedException {
        System.out.println("queryStockNum called...");
        countDown = new CountDownLatch(100);
        Executor executor = Executors.newFixedThreadPool(100);
        for (int i = 1; i <= 100 ; i++) {
            Thread.sleep(new Random().nextInt(20));
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    countDown.countDown();
                    System.out.println(countDown.getCount());
                    orderService.queryStockNum();
                }
            });
        }
        try {

            countDown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ok 线程全部跑完 ");
        return (long)1;
    }


}
