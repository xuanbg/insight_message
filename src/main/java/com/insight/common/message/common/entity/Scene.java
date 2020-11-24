package com.insight.common.message.common.entity;

import com.insight.utils.Json;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息场景
 */
public class Scene implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * UUID主键
     */
    private String id;

    /**
     * 场景编号
     */
    @NotEmpty(message = "消息场景编码不能为空")
    private String code;

    /**
     * 场景名称
     */
    @NotEmpty(message = "消息场景名称不能为空")
    private String name;

    /**
     * 默认消息标题
     */
    @NotEmpty(message = "消息标题不能为空")
    private String title;

    /**
     * 默认消息参数
     */
    private List<String> params;

    /**
     * 默认消息标签
     */
    private String tag;

    /**
     * 默认发送类型:0.未定义;1.仅消息(0001);2.仅推送(0010);3.推送+消息(0011);4.仅短信(0100);8.仅邮件(1000)
     */
    @NotNull(message = "发送类型不能为空")
    private Integer type;

    /**
     * 默认消息内容
     */
    @NotEmpty(message = "消息内容不能为空")
    private String content;

    /**
     * 默认签名
     */
    private String sign;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
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
