package com.ikun.wms_backend.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("wms_cart")
public class Cart {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer goodsId;
    private Integer userId;
    private Integer count;
    private java.time.LocalDateTime createtime;

    // ▼▼▼ 新增：非数据库字段，用于展示 ▼▼▼
    @TableField(exist = false)
    private String goodsName;

    @TableField(exist = false)
    private String storageName;

    @TableField(exist = false)
    private Double price; // 单价
    // ▲▲▲ 新增结束 ▲▲▲

}