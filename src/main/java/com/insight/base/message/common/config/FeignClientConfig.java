package com.insight.base.message.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author 宣炳刚
 * @date 2019-09-09
 * @remark Feign配置类
 */
@Component
public class FeignClientConfig implements RequestInterceptor {

    /**
     * 应用配置
     *
     * @param template RequestTemplate
     */
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }

        HttpServletRequest request = requestAttributes.getRequest();
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String name = headers.nextElement();
            String values = request.getHeader(name);
            template.header(name, values);
        }
    }
}
