package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface employeeMapper extends BaseMapper<Employee> {
}
