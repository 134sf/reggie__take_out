package com.gyh.reggie.controller;


import com.gyh.reggie.service.OrderDetailService;
import com.gyh.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/orderDetail")
@RestController
@Slf4j
public class OrderDetailController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService detailService;
}
