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
    private String name; // 仓库名称
    private String address; // 仓库地址
    private String phone; // 联系电话
    private String manager; // 负责人
    private String area; // 面积(平方米)
    private String type; // 仓库类型
    private String remark; // 备注/介绍
}