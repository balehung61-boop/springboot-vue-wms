package com.ikun.wms_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ikun.wms_backend.entity.Address;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressMapper extends BaseMapper<Address> {
    // 继承 BaseMapper，自动拥有增删改查 SQL 能力
}