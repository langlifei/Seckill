package com.znuel.seckill.exception;

import com.znuel.seckill.result.CodeMsg;
import com.znuel.seckill.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/22 9:16
 * @Describe
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler({BindException.class})
     public Result<String> bindExceptionHandler(BindException e){
        List<ObjectError> errors = e.getAllErrors();
        //这里只处理第一个异常
        StringBuilder stringBuilder = new StringBuilder();
        for (ObjectError error:errors) {
            stringBuilder.append(error.getDefaultMessage()+",");
        }
        if(stringBuilder.length()>0)
            //删除最后的逗号
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        CodeMsg codeMsg = CodeMsg.BIND_EXCEPTION.fillArgs(stringBuilder.toString());
        return Result.error(codeMsg);
     }

     //处理自定义异常
    @ExceptionHandler({GlobalException.class})
    public Result<String> globalExceptionHandler(GlobalException ge){
        CodeMsg codeMsg = ge.getCodeMsg();
        return Result.error(codeMsg);
    }
}
