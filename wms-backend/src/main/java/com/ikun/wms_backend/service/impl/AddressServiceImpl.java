package com.ikun.wms_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ikun.wms_backend.entity.Address;
import com.ikun.wms_backend.mapper.AddressMapper;
import com.ikun.wms_backend.service.AddressService;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {
    // 继承 ServiceImpl 并实现 AddressService 接口
    // 自动获得所有业务方法的实现
}