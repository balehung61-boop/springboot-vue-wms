package com.ikun.wms_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ikun.wms_backend.entity.Notification;
import com.ikun.wms_backend.mapper.NotificationMapper;
import com.ikun.wms_backend.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知服务实现类
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification>
        implements NotificationService {

    @Override
    public int getUnreadCount(Integer userId) {
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        // 查询用户专属通知或系统广播(userId=0)，且未读
        queryWrapper.and(wrapper -> wrapper
                .eq("user_id", userId)
                .or()
                .eq("user_id", 0))
                .eq("is_read", 0);
        return (int) this.count(queryWrapper);
    }

    @Override
    public List<Notification> getNotificationList(Integer userId) {
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        // 查询用户专属通知或系统广播(userId=0)
        queryWrapper.and(wrapper -> wrapper
                .eq("user_id", userId)
                .or()
                .eq("user_id", 0))
                .orderByAsc("is_read") // 未读优先
                .orderByDesc("create_time"); // 最新的在前
        return this.list(queryWrapper);
    }

    @Override
    public boolean markAsRead(Integer id) {
        UpdateWrapper<Notification> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("is_read", 1);
        return this.update(updateWrapper);
    }

    @Override
    public boolean markAllAsRead(Integer userId) {
        UpdateWrapper<Notification> updateWrapper = new UpdateWrapper<>();
        // 标记用户专属通知或系统广播为已读
        updateWrapper.and(wrapper -> wrapper
                .eq("user_id", userId)
                .or()
                .eq("user_id", 0))
                .set("is_read", 1);
        return this.update(updateWrapper);
    }
}
