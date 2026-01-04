package com.ikun.wms_backend.controller.dto;

import lombok.Data;
import java.util.HashMap;

@Data
public class QueryPageParam {
    // 默认第1页
    private int pageNum = 1;
    // 默认每页10条
    private int pageSize = 10;

    // 这是一个万能的参数容器，前端传什么名字，这里就能接什么
    // 比如前端传 name="ikun"，这里 param.get("name") 就能取到
    private HashMap param = new HashMap();
}