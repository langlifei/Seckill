package com.znuel.seckill.result;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/21 13:16
 * @Describe
 */

public class CodeMsg {
    private Integer code;
    private String msg;

    private CodeMsg(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    //通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0,"success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100,"服务端异常");
    public static CodeMsg BIND_EXCEPTION = new CodeMsg(500101,"参数绑定异常:%s");
    public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102,"非法请求");
    public static CodeMsg REQUEST_OVER_FREQUENCE = new CodeMsg(500103,"访问过于频繁");
    //登录模块 5002xx
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210,"Session不存在或者已经失效");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211,"登录密码不能为空");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212,"手机号不能为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500213,"手机号格式错误");
    public static CodeMsg MOBILE_NOT_EXITS = new CodeMsg(500214,"手机号不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215,"用户密码错误");
    public static CodeMsg USER_NOT_LOGIN = new CodeMsg(500216,"用户未登录");
    //订单模块 5004XX
    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400, "订单不存在");
    //服务端异常
    //秒杀模块异常 5005xx
    public static CodeMsg MIAO_SHA_OVER = new CodeMsg(500500,"商品已经秒杀完毕");
    public static CodeMsg REPEAT_MIAO_SHA = new CodeMsg(500501,"不能重复秒杀");
    public static CodeMsg MIAOSHA_FAIL = new CodeMsg(500502,"秒杀失败");
    public static CodeMsg VERIFYCODE_ERROR = new CodeMsg(500502,"验证码错误");



    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    //在消息中添加信息
    public CodeMsg fillArgs(Object... args){
        int code = this.code;
        String msg = String.format(this.msg,args);
        return new CodeMsg(code,msg);
    }

    @Override
    public String toString() {
        return "CodeMsg{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
