package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        String msg = ex.getMessage();
        //新增用户名重复异常处理
        if(msg.contains("Duplicate entry")){
            String[] split = msg.split(" ");
            String username = split[2];
            String message = username + MessageConstant.ALREADY_EXISTS;
            return Result.error(message);
        }
        else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
        //        log.error("异常信息：{}", ex.getMessage());
//        return Result.error(ex.getMessage());
    }

}
