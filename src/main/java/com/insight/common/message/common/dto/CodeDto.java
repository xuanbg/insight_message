package com.insight.common.message.common.dto;

import com.insight.utils.pojo.base.BaseXo;

/**
 * @author 宣炳刚
 * @date 2023/3/15
 * @remark CodeDTO
 */
public class CodeDto extends BaseXo {

    /**
     * 用户登录账号
     */
    private String account;

    public CodeDto(String account) {
        this.account = account;
    }

    public Integer getType() {
        return 0;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
