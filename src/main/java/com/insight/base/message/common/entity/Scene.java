package com.insight.base.message.common.entity;

import java.io.Serializable;
import java.util.Date;

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
    private String code;

    /**
     * 场景名称
     */
    private String name;

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
    private Date createdTime;

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

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
