package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.pojo.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface addressesBookMapper extends BaseMapper<AddressBook> {
}
