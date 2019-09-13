package com.insight.base.message.common.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 宣炳刚
 * @date 2019-09-09
 * @remark
 */
@Component
public class FeignClientConfig {
    @Bean
    public RequestInterceptor headerInterceptor() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null){
            return null;
        }

        HttpServletRequest request = requestAttributes.getRequest();
        String requestId = request.getHeader("requestId");

        return requestTemplate -> requestTemplate.header("requestId", requestId);
    }
}
