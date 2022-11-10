package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.SetmealDto;
import com.reggie.pojo.Setmeal;

import java.util.List;

public interface setmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);
    public void deleteWithDish(List<Long> ids);
    public void updateWithDish(SetmealDto setmealDto);
    public SetmealDto getWithDish(Long id);
}
