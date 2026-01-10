package com.ikun.wms_backend.controller;

import com.ikun.wms_backend.common.Result;
import com.ikun.wms_backend.entity.Notification;
import com.ikun.wms_backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 获取用户未读通知数量
     */
    @GetMapping("/unread-count")
    public Result getUnreadCount(@RequestParam Integer userId) {
        int count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 获取用户通知列表
     */
    @GetMapping("/list")
    public Result getNotificationList(@RequestParam Integer userId) {
        List<Notification> list = notificationService.getNotificationList(userId);
        return Result.success(list, (long) list.size());
    }

    /**
     * 标记单条通知为已读
     */
    @PostMapping("/read/{id}")
    public Result markAsRead(@PathVariable Integer id) {
        return notificationService.markAsRead(id) ? Result.success() : Result.fail();
    }

    /**
     * 标记用户所有通知为已读
     */
    @PostMapping("/read-all")
    public Result markAllAsRead(@RequestParam Integer userId) {
        return notificationService.markAllAsRead(userId) ? Result.success() : Result.fail();
    }
}
