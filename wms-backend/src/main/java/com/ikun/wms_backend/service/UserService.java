package com.ikun.wms_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ikun.wms_backend.entity.User;

// 继承 IService<User>，直接获得通用的业务逻辑方法
public interface UserService extends IService<User> {
    // 以后可以在这里定义复杂的自定义业务方法
    // 比如：boolean login(String username, String password);
}

