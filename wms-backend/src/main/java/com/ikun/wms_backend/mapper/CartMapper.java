package com.ikun.wms_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ikun.wms_backend.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
    // 继承 BaseMapper，自动拥有增删改查 SQL 能力
}