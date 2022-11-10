package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.mapper.addressesBookMapper;
import com.reggie.pojo.AddressBook;
import com.reggie.service.addressesBookService;
import org.springframework.stereotype.Service;

@Service
public class addressesBookServiceImpl extends ServiceImpl<addressesBookMapper, AddressBook> implements addressesBookService {
}
