package com.insight.base.message.common.dto;

import com.insight.util.Json;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 宣炳刚
 * @date 2019/9/20
 * @remark
 */
public class MessageDto implements Serializable {
    private static final long serialVersionUID = -1L;

    private String receivers;

    /**
     * 场景编码
     */
    private String sceneCode;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 自定义参数
     */
    private Map params;

    public String getReceivers() {
        return receivers;
    }

    public void setReceivers(String receivers) {
        this.receivers = receivers;
    }

    public String getSceneCode() {
        return sceneCode;
    }

    public void setSceneCode(String sceneCode) {
        this.sceneCode = sceneCode;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
