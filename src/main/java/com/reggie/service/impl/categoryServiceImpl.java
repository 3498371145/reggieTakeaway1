package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.businessException;
import com.reggie.mapper.categoryMapper;
import com.reggie.pojo.Category;
import com.reggie.pojo.Dish;
import com.reggie.pojo.Setmeal;
import com.reggie.service.categoryService;
import com.reggie.service.dishService;
import com.reggie.service.setmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class categoryServiceImpl extends ServiceImpl<categoryMapper, Category> implements categoryService {
   @Autowired
   private dishService dishService;

   @Autowired
   private setmealService setmealService;
   @Override
   public void remove(Long id) {
        //查询当前菜品是否关联菜品或套餐
       LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
       wrapper.eq(Dish::getCategoryId,id);
       int count = dishService.count(wrapper);
       if (count>0) throw new businessException("当前分类关联菜品，删除失败");
       LambdaQueryWrapper<Setmeal> wrapper1 = new LambdaQueryWrapper<>();
       wrapper1.eq(Setmeal::getCategoryId,id);
       int count1 = setmealService.count(wrapper1);
       if (count1>0) throw new businessException("当前分类关联套餐，删除失败");
       super.removeById(id);
   }
}
