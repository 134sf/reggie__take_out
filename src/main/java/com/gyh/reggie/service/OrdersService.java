package com.gyh.reggie.service;

import com.gyh.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
     public void submit(Orders orders);
}
