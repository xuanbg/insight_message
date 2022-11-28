package com.insight.common.message.common.client;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.insight.utils.EnvUtil;
import com.insight.utils.Json;
import com.insight.utils.pojo.base.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 宣炳刚
 * @date 2022/11/25
 * @remark
 */
public class AliyunClient {
    private static final Logger logger = LoggerFactory.getLogger(AliyunClient.class);

    /**
     * 初始化Client
     *
     * @return Client
     */
    public static Client createClient() throws Exception {
        String accessKeyId = EnvUtil.getValue("insight.sms.aliyun.accessKeyId");
        String accessKeySecret = EnvUtil.getValue("insight.sms.aliyun.accessKeySecret");
        Config config = new Config().setAccessKeyId(accessKeyId).setAccessKeySecret(accessKeySecret);
        config.endpoint = "dysmsapi.aliyuncs.com";

        return new Client(config);
    }

    /**
     * 发送短信
     *
     * @param phone 手机号
     * @param code  模版代码
     * @param param 模版参数
     * @param sign  签名
     */
    public static void sendTemplateMessage(String phone, String code, Object param, String sign) throws Exception {
        Client client = createClient();
        SendSmsRequest request = new SendSmsRequest()
                .setPhoneNumbers(phone)
                .setTemplateCode(code)
                .setTemplateParam(Json.toJson(param))
                .setSignName(sign);

        var response = client.sendSmsWithOptions(request, new RuntimeOptions());
        if (!response.statusCode.equals(200) || !response.body.code.equals("OK")) {
            throw new BusinessException(response.body.message);
        }
    }
}
