package com.melody.dubbo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.melody.dubbo.api.OrderService;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class RunnableCall implements Runnable {

    @Reference
    private OrderService orderService;
    private CountDownLatch countDown;

    public RunnableCall() {
    }
    public RunnableCall(CountDownLatch countDown) {
        this.countDown = countDown;
    }

    @Override
    public void run() {
        countDown.countDown();
        orderService.queryStockNum();
    }
}
