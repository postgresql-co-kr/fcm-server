package com.ecobridge.fcm.common.aop;

import com.ecobridge.fcm.common.annotation.RoutingDataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RoutingDataSourceAspect {
    @Around("@annotation(routingDataSource)") // 어노테이션 적용
    public Object proceed(ProceedingJoinPoint proceedingJoinPoint, RoutingDataSource routingDataSource) throws Throwable {
        String dataSourceKey = routingDataSource.value(); // 어노테이션의 value 값을 가져옴
        RoutingDataSourceContextHolder.setDataSourceKey(dataSourceKey); // 데이터 소스 선택
        try {
            return proceedingJoinPoint.proceed();
        } finally {
            RoutingDataSourceContextHolder.clearDataSourceKey(); // 데이터 소스 선택 해제
        }
    }
}
