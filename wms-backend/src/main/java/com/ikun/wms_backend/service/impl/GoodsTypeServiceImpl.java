package com.ikun.wms_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ikun.wms_backend.entity.GoodsType;
import com.ikun.wms_backend.mapper.GoodsTypeMapper;
import com.ikun.wms_backend.service.GoodsTypeService;
import org.springframework.stereotype.Service;

@Service
public class GoodsTypeServiceImpl extends ServiceImpl<GoodsTypeMapper, GoodsType> implements GoodsTypeService {
}