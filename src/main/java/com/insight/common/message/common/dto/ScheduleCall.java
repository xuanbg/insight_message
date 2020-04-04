package com.insight.common.message.common.dto;

import com.insight.util.Json;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 宣炳刚
 * @date 2019/9/25
 * @remark 计划任务调用DTO
 */
public class ScheduleCall implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 服务名/域名
     */
    private String service;

    /**
     * URL
     */
    private String url;

    /**
     * 请求头数据
     */
    private Map<String, String> headers;

    /**
     * 请求体数据
     */
    private Object body;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
