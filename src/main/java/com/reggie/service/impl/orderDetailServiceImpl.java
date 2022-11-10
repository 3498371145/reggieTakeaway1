package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.mapper.orderDetailMapper;
import com.reggie.pojo.OrderDetail;
import com.reggie.service.orderDetailService;
import org.springframework.stereotype.Service;

@Service
public class orderDetailServiceImpl extends ServiceImpl<orderDetailMapper, OrderDetail> implements orderDetailService {
}
