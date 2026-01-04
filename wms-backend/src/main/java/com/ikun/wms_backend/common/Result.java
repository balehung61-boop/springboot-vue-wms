package com.ikun.wms_backend.common;

import lombok.Data;

@Data
public class Result {

    private int code; // 200成功，400失败
    private String msg; // 提示信息
    private Long total; // 总记录数
    private Object data; // 数据

    // 失败的方法
    public static Result fail(){
        return result(400, "失败", 0L, null);
    }

    // 成功的方法
    public static Result success(){
        return result(200, "成功", 0L, null);
    }

    // 成功的方法(带数据)
    public static Result success(Object data){
        return result(200, "成功", 0L, data);
    }

    // 成功的方法(带数据和总数，用于分页)
    public static Result success(Object data, Long total){
        return result(200, "成功", total, data);
    }


    /**
 * 创建并返回一个Result对象，包含状态码、消息、总数和数据。
 *
 * @param code 状态码
 * @param msg 消息
 * @param total 总数
 * @param data 数据
 * @return 构建好的Result对象
 */
private static Result result(int code, String msg, Long total, Object data){
        Result res = new Result();
        res.setCode(code);
        res.setMsg(msg);
        res.setTotal(total);
        res.setData(data);
        return res;
    }

}