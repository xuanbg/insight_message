package com.insight.base.message.sms;

import com.insight.util.ReplyHelper;
import com.insight.util.pojo.Reply;
import com.insight.util.pojo.Sms;
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
     * @param sms 短信DTO
     * @return Reply
     */
    @PostMapping("/v1.0/messages")
    public Reply sendMessage(@Valid @RequestBody Sms sms) {
        return service.sendMessage(sms);
    }

    /**
     * 生成短信验证码
     *
     * @param code 验证码
     * @return Reply
     */
    @PostMapping("/v1.0/messages/codes")
    public Reply seedSmsCode(@Valid @RequestBody SmsCode code) {
        return service.seedSmsCode(code);
    }

    /**
     * 验证短信验证码
     *
     * @param key     验证参数,MD5(type + mobile + code)
     * @param isCheck 是否检验模式:true.检验模式,验证后验证码不失效;false.验证模式,验证后验证码失效
     * @return Reply
     */
    @GetMapping("/v1.0/messages/codes/{key}/status")
    public Reply verifySmsCode(@PathVariable String key, @RequestParam(defaultValue = "false") Boolean isCheck) {
        if (key == null || key.isEmpty()){
            return ReplyHelper.invalidParam();
        }

        return service.verifySmsCode(key, isCheck);
    }
}
