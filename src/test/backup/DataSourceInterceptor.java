package com.osstem.mcs.core.interceptor;

import com.osstem.mcs.core.context.DataSourceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class DataSourceInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getMethod().equalsIgnoreCase("GET")) {
            DataSourceContext.setCurrentDataSource("slave");  // GET 요청은 슬레이브로 설정
        } else {
            DataSourceContext.setCurrentDataSource("master"); // POST 요청은 마스터로 설정
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        DataSourceContext.clear();  // 요청이 끝난 후 데이터 소스 컨텍스트 초기화
    }
}

