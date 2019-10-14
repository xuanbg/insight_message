package com.insight.base.message.message;

import com.insight.util.Json;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 短信服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/base/message")
public class MessageController {
    private final MessageService service;

    /**
     * 构造方法
     *
     * @param service MessageService
     */
    public MessageController(MessageService service) {
        this.service = service;
    }

    /**
     * 生成短信验证码
     *
     * @param info 用户关键信息
     * @param dto  验证码:0.验证手机号;1.用户注册;2.重置密码;3.修改支付密码;4.修改手机号
     * @return Reply
     */
    @PostMapping("/v1.0/smscodes")
    public Reply seedSmsCode(@RequestHeader(name = "loginInfo", required = false) String info, @Valid @RequestBody SmsCode dto) {
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
    @GetMapping("/v1.0/smscodes/{key}/status")
    public Reply verifySmsCode(@PathVariable String key, @RequestParam(defaultValue = "true") Boolean isCheck) {
        if (key == null || key.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        return service.verifySmsCode(key, isCheck);
    }

    /**
     * 发送送标准消息
     *
     * @param info 用户关键信息
     * @param dto  标准信息DTO
     * @return Reply
     */
    @PostMapping("/v1.0/messages")
    public Reply sendNormalMessage(@RequestHeader("loginInfo") String info, @Valid @RequestBody NormalMessage dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.sendNormalMessage(loginInfo, dto);
    }

    /**
     * 发送自定义消息
     *
     * @param info 用户关键信息
     * @param dto  标准信息DTO
     * @return Reply
     */
    @PostMapping("/v1.0/messages/custom")
    public Reply sendCustomMessage(@RequestHeader("loginInfo") String info, @Valid @RequestBody CustomMessage dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.sendCustomMessage(loginInfo, dto);
    }
}
