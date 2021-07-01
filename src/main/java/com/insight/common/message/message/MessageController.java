package com.insight.common.message.message;

import com.insight.common.message.common.dto.CustomMessage;
import com.insight.common.message.common.dto.NormalMessage;
import com.insight.utils.Json;
import com.insight.utils.ReplyHelper;
import com.insight.utils.pojo.LoginInfo;
import com.insight.utils.pojo.Reply;
import com.insight.utils.pojo.SearchDto;
import com.insight.utils.pojo.SmsCode;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 短信服务控制器
 */
@CrossOrigin
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
     * 生成短信验证码
     *
     * @param info 用户关键信息
     * @param dto  验证码:0.验证手机号;1.用户注册;2.重置密码;3.修改支付密码;4.修改手机号
     * @return Reply
     */
    @PostMapping("/v1.0/codes")
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
    @GetMapping("/v1.0/codes/{key}/status")
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
    public Reply sendNormalMessage(@RequestHeader(value = "loginInfo", required = false) String info, @Valid @RequestBody NormalMessage dto) {
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
    @PostMapping("/v1.0/customs")
    public Reply sendCustomMessage(@RequestHeader("loginInfo") String info, @Valid @RequestBody CustomMessage dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.sendCustomMessage(loginInfo, dto);
    }

    /**
     * 获取用户消息列表
     *
     * @param info   用户关键信息
     * @param search 查询实体类
     * @return Reply
     */
    @GetMapping("/v1.0/messages")
    public Reply getUserMessages(@RequestHeader("loginInfo") String info, SearchDto search) {
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
    public Reply getUserMessage(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.getUserMessage(id, loginInfo.getUserId());
    }

    /**
     * 删除用户消息
     *
     * @param info 用户关键信息
     * @param id   消息ID
     * @return Reply
     */
    @DeleteMapping("/v1.0/messages/{id}")
    public Reply deleteUserMessage(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.deleteUserMessage(id, loginInfo.getUserId());
    }
}
