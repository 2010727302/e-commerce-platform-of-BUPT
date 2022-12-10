package com.cy.store.aop;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * @title  记录日志注解类
 * @data 2019/12/16
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})//用于标识方法
@Retention(RetentionPolicy.RUNTIME)//表示运行时保留
@Documented
public @interface OperationLog {
    /**
     * @param
     * @return
     * @title 自定义注解 操作模块
     * @date 2019/12/16
     */
    enum TYPE {
        RESULT, PARAMS
    }
    TYPE type();
    String fieldName();
}