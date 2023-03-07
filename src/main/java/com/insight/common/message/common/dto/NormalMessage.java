package com.insight.common.message.common.dto;

import com.insight.utils.Util;
import com.insight.utils.pojo.base.BaseXo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author 宣炳刚
 * @date 2019/9/20
 * @remark 标准消息DTO
 */
public class NormalMessage extends BaseXo {

    /**
     * 场景编码
     */
    @NotEmpty(message = "场景编码不能为空")
    private String sceneCode;

    /**
     * 合作伙伴编码
     */
    private String partnerCode;

    /**
     * 接收人,多个接收人使用逗号分隔
     */
    @NotEmpty(message = "接收人不能为空")
    private List<String> receivers;

    /**
     * 自定义参数
     */
    private Map<String, Object> params;

    /**
     * 是否广播消息
     */
    @NotNull(message = "广播设置不能为空")
    private Boolean broadcast;

    public String getSceneCode() {
        return sceneCode;
    }

    public void setSceneCode(String sceneCode) {
        this.sceneCode = sceneCode;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(String receivers) {
        this.receivers = Util.toStringList(receivers);
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Boolean getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(Boolean broadcast) {
        this.broadcast = broadcast;
    }
}
