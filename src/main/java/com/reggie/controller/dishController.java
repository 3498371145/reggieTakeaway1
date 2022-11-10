package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.pojo.*;
import com.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
//@Slf4j
@RequestMapping("/dish")
public class dishController {
    @Autowired
    private setmealService setmealService;
    @Autowired
    private dishService dishService;
    @Autowired
    private categoryService categoryService;
    @Autowired
    private setmealDishService setmealDishService;
    @Autowired
    private dishFlavorService dishFlavorService;

    @PostMapping
    public R<String> save(@RequestBody DishDto DishDto){
        dishService.saveWithFlavor(DishDto);
        return R.success("新增成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name!=null,Dish::getName,name);
        dishService.page(pageInfo,wrapper);
        //没有dish_dto表，需要拷贝Page<dish>中的内容
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        //分页查询的records中封装dish
        List<Dish> dishes = pageInfo.getRecords();
        //将dish的categoryId查询成类别名封装到dishDto中
        List<DishDto> dishDtos = dishes.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dishDtos);
        return R.success(dtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status,Long[] ids){
        dishService.updateStatus(status,ids);
        return R.success("修改状态成功");
    }

    @DeleteMapping
    @Transactional
    public R<String> delete(Long[] ids){
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SetmealDish::getDishId,ids);
        List<SetmealDish> setmealDishes = setmealDishService.list(wrapper);
        for (SetmealDish setmealDish : setmealDishes) {
            LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Setmeal::getId, setmealDish.getSetmealId());
            Setmeal setmeal = setmealService.getOne(queryWrapper);
            setmeal.setStatus(0);
            setmealService.updateById(setmeal);
        }
        dishService.removeByIds(Arrays.asList(ids));
        return R.success("删除成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getCategoryId,dish.getCategoryId());
        wrapper.eq(Dish::getStatus,1);
        List<Dish> dishes = dishService.list(wrapper);
        List<DishDto> dishDtos = dishes.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            dishDto.setCategoryName(category.getName());
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,id);
            List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtos);
    }
}
