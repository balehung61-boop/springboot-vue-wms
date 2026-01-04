package com.ikun.wms_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("wms_storage")
public class Storage {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String remark;
}