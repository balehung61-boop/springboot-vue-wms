package com.ikun.wms_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ikun.wms_backend.entity.Record;
import com.ikun.wms_backend.mapper.RecordMapper;
import com.ikun.wms_backend.service.RecordService;
import org.springframework.stereotype.Service;

@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {
}