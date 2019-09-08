package com.insight.base.message.common;

import com.insight.util.Redis;
import com.insight.util.Util;

/**
 * @author 宣炳刚
 * @date 2019-08-31
 * @remark
 */
public class Core {

    /**
     * 验证短信验证码
     *
     * @param type   验证码类型
     * @param mobile 手机号
     * @param code   验证码
     * @return 是否通过验证
     */
    public Boolean verifySmsCode(int type, String mobile, String code) {
        return verifySmsCode(type, mobile, code, false);
    }

    /**
     * 验证短信验证码
     *
     * @param type    验证码类型
     * @param mobile  手机号
     * @param code    验证码
     * @param isCheck 是否检验模式
     * @return 是否通过验证
     */
    public Boolean verifySmsCode(int type, String mobile, String code, Boolean isCheck) {
        String key = "SMSCode:" + Util.md5(type + mobile + code);
        Boolean isExisted = Redis.hasKey(key);
        if (!isExisted || isCheck) {
            return isExisted;
        }

        Redis.deleteKey(key);
        return true;
    }
}
