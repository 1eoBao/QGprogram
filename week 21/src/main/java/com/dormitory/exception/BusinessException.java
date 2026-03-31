package com.dormitory.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private Integer code;

/**
 * 自定义业务异常类的构造方法
 * @param message 异常信息描述
 */
    public BusinessException(String message) {
        super(message); // 调用父类（Exception）的构造方法，传递异常信息
        this.code = 400; // 设置异常状态码为400，表示客户端请求错误
    }

/**
 * 自定义业务异常类的构造方法
 * @param code 错误代码，用于标识具体的错误类型
 * @param message 错误信息，用于描述具体的错误内容
 */
    public BusinessException(Integer code, String message) {
        // 调用父类的构造方法，传递错误信息
        super(message);
        // 初始化错误代码属性
        this.code = code;
    }
}