package com.ikun.wms_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ikun.wms_backend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承 BaseMapper 后，MyBatis-Plus 会自动帮你生成 sql 语句
    // 你不需要写 select * from ... 这种代码了
}