package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.pojo.ShoppingCart;
import com.reggie.service.shoppingCartService;
import com.reggie.mapper.shoppingCartMapper;
import org.springframework.stereotype.Service;

@Service
public class shoppingCartServiceImpl extends ServiceImpl<shoppingCartMapper, ShoppingCart> implements shoppingCartService {
}
