package com.melody.dubbo.api;

public interface OrderService {
    void orderCP(Integer num);
    Long queryStockNum();
}
