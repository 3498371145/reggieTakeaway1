package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.pojo.DishFlavor;
import com.reggie.service.dishFlavorService;
import com.reggie.mapper.dishFlavorMapper;
import org.springframework.stereotype.Service;

@Service
public class dishFlavorServiceImpl extends ServiceImpl<dishFlavorMapper, DishFlavor> implements dishFlavorService {
}
