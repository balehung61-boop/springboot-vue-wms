package com.ikun.wms_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ikun.wms_backend.entity.Menu;
import com.ikun.wms_backend.mapper.MenuMapper;
import com.ikun.wms_backend.service.MenuService;
import org.springframework.stereotype.Service;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
}