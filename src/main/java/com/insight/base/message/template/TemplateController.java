package com.insight.base.message.template;

import com.insight.base.message.common.entity.Template;
import com.insight.util.Json;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.Reply;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息管理服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/base/message")
public class TemplateController {
    private final TemplateService service;

    /**
     * 构造方法
     *
     * @param service 注入Service
     */
    public TemplateController(TemplateService service) {
        this.service = service;
    }

    /**
     * 获取短信模板列表
     *
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @GetMapping("/v1.0/templates")
    public Reply getTemplates(@RequestHeader("loginInfo") String info, @RequestParam(required = false) String keyword,
                              @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.getTemplates(loginInfo.getTenantId(), keyword, page, size);
    }

    /**
     * 获取短信模板
     *
     * @param id 短信模板ID
     * @return Reply
     */
    @GetMapping("/v1.0/templates/{id}")
    public Reply getTemplate(@PathVariable String id) {
        return service.getTemplate(id);
    }

    /**
     * 新增短信模板
     *
     * @param info 用户关键信息
     * @param dto  短信模板DTO
     * @return Reply
     */
    @PostMapping("/v1.0/templates")
    public Reply newTemplate(@RequestHeader("loginInfo") String info, @Valid @RequestBody Template dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.newTemplate(loginInfo, dto);
    }

    /**
     * 编辑短信模板
     *
     * @param info 用户关键信息
     * @param dto  短信模板DTO
     * @return Reply
     */
    @PutMapping("/v1.0/templates")
    public Reply editTemplate(@RequestHeader("loginInfo") String info, @Valid @RequestBody Template dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.editTemplate(loginInfo, dto);
    }

    /**
     * 删除短信模板
     *
     * @param info 用户关键信息
     * @param id   短信模板ID
     * @return Reply
     */
    @DeleteMapping("/v1.0/templates")
    public Reply deleteTemplate(@RequestHeader("loginInfo") String info, @RequestBody String id) {
        if (id == null || id.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.deleteTemplate(loginInfo, id);
    }

    /**
     * 禁用短信模板
     *
     * @param info 用户关键信息
     * @param id   短信模板ID
     * @return Reply
     */
    @PutMapping("/v1.0/templates/disable")
    public Reply disableTemplate(@RequestHeader("loginInfo") String info, @RequestBody String id) {
        if (id == null || id.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.changeTemplateStatus(loginInfo, id, true);
    }

    /**
     * 启用短信模板
     *
     * @param info 用户关键信息
     * @param id   短信模板ID
     * @return Reply
     */
    @PutMapping("/v1.0/templates/enable")
    public Reply enableTemplate(@RequestHeader("loginInfo") String info, @RequestBody String id) {
        if (id == null || id.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.changeTemplateStatus(loginInfo, id, false);
    }

    /**
     * 获取日志列表
     *
     * @param info    用户关键信息
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @GetMapping("/v1.0/templates/logs")
    public Reply getTemplateLogs(@RequestHeader("loginInfo") String info, @RequestParam(required = false) String keyword,
                         @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.getTemplateLogs(loginInfo.getTenantId(), keyword, page, size);
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @GetMapping("/v1.0/templates/logs/{id}")
    Reply getTemplateLog(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        return service.getTemplateLog(id);
    }
}
