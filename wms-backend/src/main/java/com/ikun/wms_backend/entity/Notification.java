package com.ikun.wms_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统通知实体类
 */
@Data
@TableName("sys_notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId; // 接收用户ID (0表示系统广播)
    private String title; // 通知标题
    private String content; // 通知内容
    private String type; // 通知类型: system/order/stock
    private Integer isRead; // 是否已读: 0-未读, 1-已读
    private LocalDateTime createTime; // 创建时间
}
