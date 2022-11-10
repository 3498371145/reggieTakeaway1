package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.pojo.Category;


public interface categoryService extends IService<Category> {
    public void remove(Long id);
}
