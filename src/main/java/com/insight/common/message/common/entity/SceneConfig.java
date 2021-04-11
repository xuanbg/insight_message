package com.insight.common.message.common.entity;

import com.insight.utils.Json;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 渠道消息模板
 */
public class SceneConfig implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * UUID主键
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 消息内容
     */
    @NotEmpty(message = "消息内容不能为空")
    private String content;

    /**
     * 签名
     */
    private String sign;

    /**
     * 消息有效时长(小时)
     */
    private Integer expire;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建人ID
     */
    private String creatorId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Integer getExpire() {
        return expire == null ? 1 : expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
