package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.mapper.employeeMapper;
import com.reggie.pojo.Employee;
import com.reggie.service.employeeService;
import org.springframework.stereotype.Service;

@Service
public class employeeServiceImpl extends ServiceImpl<employeeMapper, Employee> implements employeeService {
}
