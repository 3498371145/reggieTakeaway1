package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.dto.SetmealDto;
import com.reggie.pojo.Dish;
import com.reggie.pojo.Setmeal;
import com.reggie.pojo.SetmealDish;
import com.reggie.service.categoryService;
import com.reggie.service.dishService;
import com.reggie.service.setmealDishService;
import com.reggie.service.setmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class setmealController {
    @Autowired
    private setmealService setmealService;
    @Autowired
    private setmealDishService setmealDishService;
    @Autowired
    private categoryService categoryService;
    @Autowired
    private dishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name!=null,Setmeal::getName,name);
        setmealService.page(pageInfo,wrapper);
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> dtos=records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            setmealDto.setCategoryName(categoryService.getById(categoryId).getName());
            return setmealDto;
        }).collect(Collectors.toList());
        return R.success(setmealDtoPage.setRecords(dtos));
    }

    @DeleteMapping
    public R<String> deleteByIds(@RequestParam List<Long> ids){
        setmealService.deleteWithDish(ids);
        return R.success("删除成功");
    }

    @PostMapping("/status/{status}")
    @Transactional
    public R<String> updateStatus(@PathVariable int status,Long[] ids){
        for (Long id : ids) {
            LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SetmealDish::getSetmealId,id);
            List<SetmealDish> setmealDishes = setmealDishService.list(wrapper);
            for (SetmealDish setmealDish : setmealDishes) {
                LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Dish::getId,setmealDish.getDishId());
                Dish dish = dishService.getOne(queryWrapper);
                if (dish==null) return R.error("有菜品已被删除，修改状态失败");
                if (dish.getStatus()==0) return R.error("有菜品禁售，修改状态失败");
            }
        }
        List<Setmeal> setmeals = new ArrayList<>();
        for (Long id : ids) {
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(status);
            setmeals.add(setmeal);
        }
        setmealService.updateBatchById(setmeals);
        return R.success("修改状态成功");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getWithDish(id);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(@RequestParam Long categoryId,@RequestParam Integer status){
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Setmeal::getCategoryId,categoryId);
        wrapper.eq(Setmeal::getStatus,status);
        List<Setmeal> setmeals = setmealService.list(wrapper);
        return R.success(setmeals);
    }
}
