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
     * 绑定手机号
     *
     * @param mobile 手机号
     */
    @GetMapping("/v1.0/codes")
    public void verifyMobile(@RequestHeader("loginInfo") String loginInfo, @RequestParam String mobile) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);

        var dto = new SmsCode();
        dto.setMobile(mobile);
        dto.setType(4);
        service.seedSmsCode(dto);
    }

    /**
     * 发送短信验证码
     *
     * @param dto 短信DTO
     */
    @PostMapping("/v1.0/codes")
    public void seedCode(@Valid @RequestBody SmsCode dto) {
        if (dto.getType() < 0 || dto.getType() > 3) {
            throw new BusinessException("无效的短信类型");
        }

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
     * @param loginInfo 用户关键信息
     * @param dto       标准信息DTO
     */
    @PostMapping("/v1.0/messages")
    public void sendNormalMessage(@RequestHeader("loginInfo") String loginInfo, @Valid @RequestBody NormalMessage dto) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        service.sendNormalMessage(info, dto);
    }

    /**
     * 发送自定义消息
     *
     * @param loginInfo 用户关键信息
     * @param dto       标准信息DTO
     */
    @PostMapping("/v1.0/customs")
    public void sendCustomMessage(@RequestHeader("loginInfo") String loginInfo, @Valid @RequestBody CustomMessage dto) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        service.sendCustomMessage(info, dto);
    }

    /**
     * 获取用户消息列表
     *
     * @param loginInfo 用户关键信息
     * @param search    查询实体类
     * @return Reply
     */
    @GetMapping("/v1.0/messages")
    public Reply getUserMessages(@RequestHeader("loginInfo") String loginInfo, Search search) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        return service.getUserMessages(info, search);
    }

    /**
     * 获取用户消息详情
     *
     * @param loginInfo 用户关键信息
     * @param id        消息ID
     * @return Reply
     */
    @GetMapping("/v1.0/messages/{id}")
    public UserMessageDto getUserMessage(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        return service.getUserMessage(id, info.getId());
    }

    /**
     * 删除用户消息
     *
     * @param loginInfo 用户关键信息
     * @param id        消息ID
     */
    @DeleteMapping("/v1.0/messages/{id}")
    public void deleteUserMessage(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        service.deleteUserMessage(id, info.getId());
    }
}
