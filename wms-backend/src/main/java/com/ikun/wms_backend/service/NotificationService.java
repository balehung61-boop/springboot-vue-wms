package com.ikun.wms_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ikun.wms_backend.entity.Notification;

import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService extends IService<Notification> {

    /**
     * 获取用户未读通知数量
     * 
     * @param userId 用户ID
     * @return 未读数量
     */
    int getUnreadCount(Integer userId);

    /**
     * 获取用户通知列表（包含系统广播）
     * 
     * @param userId 用户ID
     * @return 通知列表
     */
    List<Notification> getNotificationList(Integer userId);

    /**
     * 标记通知为已读
     * 
     * @param id 通知ID
     * @return 是否成功
     */
    boolean markAsRead(Integer id);

    /**
     * 标记用户所有通知为已读
     * 
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAllAsRead(Integer userId);
}
