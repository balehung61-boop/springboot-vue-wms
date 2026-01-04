package com.ikun.wms_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("wms_record")
public class Record {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer goodsId;
    private Integer userId;
    private Integer adminId;

    private Integer count;

    // Java 8 推荐的时间类型，对应数据库 datetime
    private LocalDateTime createtime;

    private String remark;
}