package com.insight.base.message.common.entity;

import com.insight.util.Json;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息模板
 */
public class Template implements Serializable {
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
     * 模板编号
     */
    @NotEmpty(message = "消息模板编码不能为空")
    private String code;

    /**
     * 消息标签
     */
    @NotEmpty(message = "消息标签不能为空")
    private String tag;

    /**
     * 发送类型:0.未定义;1.仅消息(001);2.仅推送(010);3.消息+推送(011);4.仅短信(100)
     */
    @NotNull(message = "发送类型不能为空")
    @Size(min = 1, max = 4, message = "发送类型为:1.消息;2.推送;3.消息+推送;4.短信")
    private Integer type;

    /**
     * 消息标题
     */
    @NotEmpty(message = "消息标题不能为空")
    private String title;

    /**
     * 消息内容
     */
    @NotEmpty(message = "消息内容不能为空")
    private String content;

    /**
     * 消息有效时长(小时)
     */
    private Integer expire;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否失效:0.正常;1.失效
     */
    private Boolean isInvalid;

    /**
     * 创建人部门ID
     */
    private String deptId;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建人ID
     */
    private String creatorId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getExpire() {
        return expire;
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

    public Boolean getInvalid() {
        return isInvalid;
    }

    public void setInvalid(Boolean invalid) {
        isInvalid = invalid;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
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

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
