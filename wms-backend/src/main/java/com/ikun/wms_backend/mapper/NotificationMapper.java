package com.ikun.wms_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ikun.wms_backend.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知 Mapper 接口
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
