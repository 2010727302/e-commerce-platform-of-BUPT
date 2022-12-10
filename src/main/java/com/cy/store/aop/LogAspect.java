package com.cy.store.aop;


import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cy.store.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
/***
 * @title  操作日志记录处理
 * @data 2019/12/13
 */
@Component
@Aspect
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
    private static final Marker marker = MarkerFactory.getMarker("user_operations");

    // 配置织入点  注解拦截
    @Pointcut("@annotation(com.cy.store.aop.OperationLog)")
    public void logPointCut() {
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "result")
    public void AfterReturning(JoinPoint joinPoint, Object result) throws Exception {
        OperationLog log = ((MethodSignature) joinPoint.getSignature())
                .getMethod().getAnnotation(OperationLog.class);
        //assert (log.fieldName() != null && log.type() != null);
        String jsonLog = getJsonLog(joinPoint, result, log.type(), log.fieldName());
        writeLog(jsonLog);
    }
    private void writeLog(String log) {
        logger.info(marker, log);
    }

    private String getJsonLog(JoinPoint joinPoint, Object result, OperationLog.TYPE type, String dataType) throws JsonProcessingException {
        Map<String, Object> log = new HashMap<>();
        log.put("data_type", dataType);
        log.put("time_stamp", System.currentTimeMillis());
        if(type == OperationLog.TYPE.PARAMS) {
            log.put("data", getParams(joinPoint));
        }
        if(type == OperationLog.TYPE.RESULT) {
            log.put("data", result);
        }
        return JsonUtils.ObjectToJson(log);
    }

    private Map<String, Object> getParams(JoinPoint joinPoint) {
        Map<String, Object> params = new HashMap<>();
        // 下面两个数组中，参数值和参数名的个数和位置是一一对应的。
        Object[] args = joinPoint.getArgs(); // 参数值
        String[] argNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames(); // 参数名
        for(int i = 0; i < args.length; i ++) {
            params.put(argNames[i], args[i]);
        }
        return params;
    }


}