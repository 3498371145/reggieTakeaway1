package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.dto.DishDto;
import com.reggie.mapper.dishMapper;
import com.reggie.pojo.Dish;
import com.reggie.pojo.DishFlavor;
import com.reggie.pojo.Setmeal;
import com.reggie.pojo.SetmealDish;
import com.reggie.service.dishFlavorService;
import com.reggie.service.dishService;
import com.reggie.service.setmealDishService;
import com.reggie.service.setmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class dishServiceImpl extends ServiceImpl<dishMapper, Dish> implements dishService {
    @Autowired
    private dishFlavorService dishFlavorService;
    @Autowired
    private dishService dishService;
    @Autowired
    private setmealDishService setmealDishService;
    @Autowired
    private setmealService setmealService;
    //多表操作开启事务
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        Long categoryId = dishDto.getCategoryId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(categoryId);
        }
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = dishService.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavors = dishFlavorService.list(wrapper);
        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(wrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors=flavors.stream().map((item->{
            item.setDishId(dishDto.getId());
            return item;
        })).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void updateStatus(int status, Long[] ids) {
        List<Dish> dishes1 = new ArrayList<>();
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishes1.add(dish);
        }
        dishService.updateBatchById(dishes1);
        if (status==0){
            LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(SetmealDish::getDishId,ids);
            List<SetmealDish> setmealDishes = setmealDishService.list(wrapper);
            for (SetmealDish setmealDish : setmealDishes) {
                LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Setmeal::getId,setmealDish.getSetmealId());
                Setmeal setmeal = setmealService.getOne(queryWrapper);
                setmeal.setStatus(0);
                setmealService.updateById(setmeal);
            }
        }
        if (status==1){
            LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(SetmealDish::getDishId,ids);
            List<SetmealDish> setmealDishes = setmealDishService.list(wrapper);
            int flag=1;
            for (SetmealDish setmealDish : setmealDishes) {
                LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
                dishLambdaQueryWrapper.eq(Dish::getId,setmealDish.getDishId());
                List<Dish> dishes = dishService.list(dishLambdaQueryWrapper);
                for (Dish dish : dishes) {
                    if (dish.getStatus()==0) flag=0;
                }
                if (flag==1){
                    LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    setmealLambdaQueryWrapper.eq(Setmeal::getId,setmealDish.getSetmealId());
                    Setmeal setmeal = setmealService.getOne(setmealLambdaQueryWrapper);
                    setmeal.setStatus(1);
                    setmealService.updateById(setmeal);
                }
            }
        }
    }
}
