package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.pojo.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface orderDetailMapper extends BaseMapper<OrderDetail> {
}
