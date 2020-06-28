package com.znuel.seckill.result;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/21 10:59
 * @Describe
 */

public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private Result(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public static <T> Result<T> success(T data){
        return new Result<>(0,"success",data);
    }

    public static <T> Result<T> error(CodeMsg codMsg){
        return new Result<>(codMsg.getCode(),codMsg.getMsg());
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }
}
