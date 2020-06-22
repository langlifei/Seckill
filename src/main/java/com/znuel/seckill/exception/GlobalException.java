package com.znuel.seckill.exception;

import com.znuel.seckill.result.CodeMsg;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/22 9:49
 * @Describe
 */

public class GlobalException extends RuntimeException{

    private CodeMsg codeMsg;
    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    //用于在异常处理类中获取到codeMsg;
    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
