package com.ikun.wms_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ikun.wms_backend.entity.Record;
import com.ikun.wms_backend.entity.RecordVO;

import java.util.List;

public interface RecordService extends IService<Record> {
    /**
     * 获取所有订单记录（管理员使用）
     */
    List<RecordVO> listAllOrders();
}