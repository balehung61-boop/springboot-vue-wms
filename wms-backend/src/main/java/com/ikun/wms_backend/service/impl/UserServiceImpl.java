package com.ikun.wms_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ikun.wms_backend.entity.User;
import com.ikun.wms_backend.mapper.UserMapper;
import com.ikun.wms_backend.service.UserService;
import org.springframework.stereotype.Service;

@Service // 1. 必须交给 Spring 管理
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    // 2. 继承 ServiceImpl<Mapper, Entity>
    // 3. 实现 UserService 接口

    // 这里的代码虽然也是空的，但通过继承，你已经拥有了 save, getById, list, page 等几十个方法
}