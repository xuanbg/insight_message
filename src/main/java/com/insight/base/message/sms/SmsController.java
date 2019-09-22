package com.insight.base.message.sms;

import com.insight.util.Json;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.NormalMessage;
import com.insight.util.pojo.Reply;
import com.insight.util.pojo.SmsCode;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 短信服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/base/message/sms")
public class SmsController {
    private final SmsService service;

    /**
     * 构造方法
     *
     * @param service SmsService
     */
    public SmsController(SmsService service) {
        this.service = service;
    }

    /**
     * 发送短信
     *
     * @param info 用户关键信息
     * @param dto  短信DTO
     * @return Reply
     */
    @PostMapping("/v1.0/messages")
    public Reply sendMessage(@RequestHeader("loginInfo") String info, @Valid @RequestBody NormalMessage dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.sendMessage(loginInfo, dto);
    }

    /**
     * 生成短信验证码
     *
     * @param info 用户关键信息
     * @param dto  验证码:0.验证手机号;1.用户注册;2.重置密码;3.修改支付密码;4.修改手机号
     * @return Reply
     */
    @PostMapping("/v1.0/messages/codes")
    public Reply seedSmsCode(@RequestHeader("loginInfo") String info, @Valid @RequestBody SmsCode dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.seedSmsCode(loginInfo, dto);
    }

    /**
     * 验证短信验证码
     *
     * @param key     验证参数,MD5(type + mobile + code)
     * @param isCheck 是否检验模式:true.检验模式,验证后验证码不失效;false.验证模式,验证后验证码失效
     * @return Reply
     */
    @GetMapping("/v1.0/messages/codes/{key}/status")
    public Reply verifySmsCode(@PathVariable String key, @RequestParam(defaultValue = "true") Boolean isCheck) {
        if (key == null || key.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        return service.verifySmsCode(key, isCheck);
    }
}
