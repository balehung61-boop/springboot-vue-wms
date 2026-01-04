package com.ikun.wms_backend.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("wms_address")
public class Address {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String linkman;
    private String phone;
    private String address;
    private String isDefault; // Y或N
}