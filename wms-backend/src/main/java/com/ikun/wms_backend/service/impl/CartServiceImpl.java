package com.ikun.wms_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ikun.wms_backend.entity.Cart;
import com.ikun.wms_backend.mapper.CartMapper;
import com.ikun.wms_backend.service.CartService;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {
    // 继承 ServiceImpl 并实现 CartService 接口
    // 自动获得所有业务方法的实现
}