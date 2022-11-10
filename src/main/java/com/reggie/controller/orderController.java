package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.common.baseContext;
import com.reggie.pojo.AddressBook;
import com.reggie.pojo.OrderDetail;
import com.reggie.pojo.Orders;
import com.reggie.service.addressesBookService;
import com.reggie.service.orderDetailService;
import com.reggie.service.orderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RequestMapping("/order")
@RestController
public class orderController {
    @Autowired
    private orderService orderService;
    @Autowired
    private orderDetailService orderDetailService;
    @Autowired
    private addressesBookService addressesBookService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize){
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        long id = baseContext.getCurrentId();
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId,id);
        orderService.page(pageInfo,wrapper);
        return R.success(pageInfo);
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime){
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(number!=null,Orders::getNumber,number);
        wrapper.ge(beginTime!=null,Orders::getOrderTime,beginTime);
        wrapper.le(endTime!=null,Orders::getCheckoutTime,endTime);
        orderService.page(pageInfo,wrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(@RequestBody Orders orders){
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getId,orders.getId());
        Orders orderServiceOne = orderService.getOne(wrapper);
        if (orders.getStatus()==3) orderServiceOne.setStatus(3);
        if (orders.getStatus()==4) orderServiceOne.setStatus(4);
        orderService.updateById(orders);
        return R.success("更改成功");
    }

    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getId,orders.getId());
        Orders orderServiceOne = orderService.getOne(wrapper);
        long orderId = IdWorker.getId();
        orderServiceOne.setId(orderId);
        orderServiceOne.setNumber(String.valueOf(orderId));
        orderServiceOne.setStatus(2);
        orderServiceOne.setOrderTime(LocalDateTime.now());
        orderServiceOne.setCheckoutTime(LocalDateTime.now());
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,orders.getId());
        List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper);
        for (OrderDetail orderDetail : orderDetails) {
            orderDetail.setOrderId(orderId);
            orderDetail.setId(null);
            orderDetailService.save(orderDetail);
        }
        orderService.save(orderServiceOne);
        return R.success("下单成功");
    }

}
