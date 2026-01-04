package com.ikun.wms_backend.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    // ... 原有字段 ...
    private String avatar;      // 头像
    private String email;       // 邮箱
    private String description; // 简介

    private String no;       // 账号
    private String name;     // 名字
    private String password; // 密码
    private Integer age;
    private Integer sex;     // 1:男 0:女
    private String phone;
    private Integer roleId;  // 角色ID

    // 刚才新增的字段
    private Integer userLevel;

    // 逻辑删除标志 (Y/N)
    private String isValid;
}
