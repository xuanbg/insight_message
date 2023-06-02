package com.insight.common.message.message;

import com.insight.common.message.common.dto.CustomMessage;
import com.insight.common.message.common.dto.NormalMessage;
import com.insight.common.message.common.dto.UserMessageDto;
import com.insight.utils.Json;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.BusinessException;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;
import com.insight.utils.pojo.message.SmsCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 短信服务控制器
 */
@RestController
@RequestMapping("/common/message")
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
     * 发送短信验证码
     *
     * @param mobile 手机号
     */
    @GetMapping("/v1.0/codes")
    public void seedSmsCode(@RequestParam String mobile) {
        var dto = new SmsCode();
        dto.setMobile(mobile);
        service.seedSmsCode(dto);
    }

    /**
     * 发送短信验证码
     *
     * @param dto 短信DTO
     */
    @PostMapping("/v1.0/codes")
    public void seedCode(@Valid @RequestBody SmsCode dto) {
        service.seedSmsCode(dto);
    }

    /**
     * 验证短信验证码
     *
     * @param key     验证参数,MD5(type + mobile + code)
     * @param isCheck 是否检验模式:true.检验模式,验证后验证码不失效;false.验证模式,验证后验证码失效
     * @return Reply
     */
    @GetMapping("/v1.0/codes/{key}/status")
    public String verifySmsCode(@PathVariable String key, @RequestParam(defaultValue = "true") Boolean isCheck) {
        if (key == null || key.isEmpty()) {
            throw new BusinessException("无效的验证参数");
        }

        return service.verifySmsCode(key, isCheck);
    }

    /**
     * 发送送标准消息
     *
     * @param info 用户关键信息
     * @param dto  标准信息DTO
     */
    @PostMapping("/v1.0/messages")
    public void sendNormalMessage(@RequestHeader(value = "loginInfo", required = false) String info, @Valid @RequestBody NormalMessage dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        service.sendNormalMessage(loginInfo, dto);
    }

    /**
     * 发送自定义消息
     *
     * @param info 用户关键信息
     * @param dto  标准信息DTO
     */
    @PostMapping("/v1.0/customs")
    public void sendCustomMessage(@RequestHeader("loginInfo") String info, @Valid @RequestBody CustomMessage dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        service.sendCustomMessage(loginInfo, dto);
    }

    /**
     * 获取用户消息列表
     *
     * @param info   用户关键信息
     * @param search 查询实体类
     * @return Reply
     */
    @GetMapping("/v1.0/messages")
    public Reply getUserMessages(@RequestHeader("loginInfo") String info, Search search) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.getUserMessages(loginInfo, search);
    }

    /**
     * 获取用户消息详情
     *
     * @param info 用户关键信息
     * @param id   消息ID
     * @return Reply
     */
    @GetMapping("/v1.0/messages/{id}")
    public UserMessageDto getUserMessage(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.getUserMessage(id, loginInfo.getId());
    }

    /**
     * 删除用户消息
     *
     * @param info 用户关键信息
     * @param id   消息ID
     */
    @DeleteMapping("/v1.0/messages/{id}")
    public void deleteUserMessage(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        service.deleteUserMessage(id, loginInfo.getId());
    }
}
