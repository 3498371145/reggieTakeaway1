package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.R;
import com.reggie.pojo.OrderDetail;
import com.reggie.service.orderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/orderDetail")
@RestController
public class orderDetailController {
    @Autowired
    private orderDetailService orderDetailService;

    @GetMapping("/{id}")
    public R<OrderDetail> get(@PathVariable Long id){
        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDetail::getId,id);
        OrderDetail orderDetail = orderDetailService.getOne(wrapper);
        return R.success(orderDetail);
    }
}
