package com.ikun.wms_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单详情VO - 包含订单信息和关联的商品信息
 */
@Data
public class RecordVO {
    // Record 字段
    private Integer id;
    private Integer goodsId;
    private Integer userId;
    private Integer adminId;
    private Integer count;
    private LocalDateTime createtime;
    private String remark;

    // Goods 关联字段
    private String goodsName;
    private Double goodsPrice;
    private String goodsImage;
    private String goodsUnit;

    // 计算字段
    private Double totalPrice;

    // 解析自remark的字段
    private String recipient; // 收货人
    private String address; // 地址
    private String payMethod; // 支付方式
    private String status; // 订单状态 (已完成/待签收等)

    // 用户信息（可选）
    private String userName;
}
