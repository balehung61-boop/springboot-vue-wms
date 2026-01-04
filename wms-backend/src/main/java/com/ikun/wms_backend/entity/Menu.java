package com.ikun.wms_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_menu")
public class Menu {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String icon;
    private String component;
    private String path;
    private Integer menuLevel;
    private Integer parentId;
}