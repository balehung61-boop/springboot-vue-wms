package com.ikun.wms_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ikun.wms_backend.entity.Goods;
import com.ikun.wms_backend.mapper.GoodsMapper;
import com.ikun.wms_backend.service.GoodsService;
import org.springframework.stereotype.Service;
/**
 * @Author ikun
 * @Date 2023/3/26 14:47
 * @Description: TODO
 * @Version 1.0
 */

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {
}