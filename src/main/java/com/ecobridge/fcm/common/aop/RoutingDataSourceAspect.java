/*
 * Copyright 2023 jinyoonoh@gmail.com (postgresql.co.kr, ecobridge.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
