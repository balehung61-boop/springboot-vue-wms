package com.ikun.wms_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("wms_goods")
public class Goods {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Double price;

    // 驼峰命名自动匹配：storage_id -> storageId
    private Integer storageId;
    private Integer goodstypeId;

    private Integer count; // 库存
    private String unit; // 单位
    private String remark;
    private String image; // 图片URL
}
