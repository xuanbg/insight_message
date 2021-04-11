package com.insight.common.message.common.dto;

import com.insight.utils.Json;

import java.io.Serializable;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息场景
 */
public class SceneDto implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * UUID主键
     */
    private String id;

    /**
     * 场景编号
     */
    private String code;

    /**
     * 默认发送类型:0.未定义;1.仅消息(0001);2.仅推送(0010);3.推送+消息(0011);4.仅短信(0100);8.仅邮件(1000)
     */
    private Integer type;

    /**
     * 场景名称
     */
    private String name;

    /**
     * 默认消息标题
     */
    private String title;

    /**
     * 默认消息内容
     */
    private String content;

    /**
     * 默认消息标签
     */
    private String tag;

    /**
     * 默认消息参数
     */
    private List<String> param;

    /**
     * 备注
     */
    private String remark;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<String> getParam() {
        return param;
    }

    public void setParam(List<String> param) {
        this.param = param;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
