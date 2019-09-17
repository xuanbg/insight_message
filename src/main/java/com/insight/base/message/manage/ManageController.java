package com.insight.base.message.manage;

import com.insight.base.message.common.entity.ChannelConfig;
import com.insight.base.message.common.entity.Scene;
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
@RequestMapping("/base/message/manage")
public class ManageController {
    private final ManageService service;

    /**
     * 构造方法
     *
     * @param service 注入Service
     */
    public ManageController(ManageService service) {
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
    public Reply getTemplates(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getTemplates(keyword, page, size);
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
        if (dto == null) {
            return ReplyHelper.invalidParam();
        }

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
        if (dto == null) {
            return ReplyHelper.invalidParam();
        }

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
        return service.changeTemplateStatus(loginInfo, id, false);
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
        return service.changeTemplateStatus(loginInfo, id, true);
    }

    /**
     * 获取场景列表
     *
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @GetMapping("/v1.0/scenes")
    public Reply getScenes(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getScenes(keyword, page, size);
    }

    /**
     * 获取场景
     *
     * @param id 场景ID
     * @return Reply
     */
    @GetMapping("/v1.0/scenes/{id}")
    public Reply getScene(@PathVariable String id) {
        return service.getScene(id);
    }

    /**
     * 新增场景
     *
     * @param info 用户关键信息
     * @param dto  场景DTO
     * @return Reply
     */
    @PostMapping("/v1.0/scenes")
    public Reply newScene(@RequestHeader("loginInfo") String info, @Valid @RequestBody Scene dto) {
        if (dto == null) {
            return ReplyHelper.invalidParam();
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.newScene(loginInfo, dto);
    }

    /**
     * 编辑场景
     *
     * @param info 用户关键信息
     * @param dto  场景DTO
     * @return Reply
     */
    @PutMapping("/v1.0/scenes")
    public Reply editScene(@RequestHeader("loginInfo") String info, @Valid @RequestBody Scene dto) {
        if (dto == null) {
            return ReplyHelper.invalidParam();
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.editScene(loginInfo, dto);
    }

    /**
     * 删除场景
     *
     * @param info 用户关键信息
     * @param id   场景ID
     * @return Reply
     */
    @DeleteMapping("/v1.0/scenes")
    public Reply deleteScene(@RequestHeader("loginInfo") String info, @RequestBody String id) {
        if (id == null || id.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.deleteScene(loginInfo, id);
    }

    /**
     * 禁用场景
     *
     * @param info 用户关键信息
     * @param id   场景ID
     * @return Reply
     */
    @PutMapping("/v1.0/scenes/disable")
    public Reply disableScene(@RequestHeader("loginInfo") String info, @RequestBody String id) {
        if (id == null || id.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.changeSceneStatus(loginInfo, id, false);
    }

    /**
     * 启用场景
     *
     * @param info 用户关键信息
     * @param id   场景ID
     * @return Reply
     */
    @PutMapping("/v1.0/scenes/enable")
    public Reply enableScene(@RequestHeader("loginInfo") String info, @RequestBody String id) {
        if (id == null || id.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.changeSceneStatus(loginInfo, id, true);
    }

    /**
     * 获取渠道模板配置列表
     *
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @GetMapping("/v1.0/scenes/channels")
    public Reply getChannelConfigs(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getScenes(keyword, page, size);
    }

    /**
     * 添加渠道模板
     *
     * @param info 用户关键信息
     * @param dto  渠道模板DTO
     * @return Reply
     */
    @PostMapping("/v1.0/scenes/channels")
    public Reply addChannelConfig(@RequestHeader("loginInfo") String info, @Valid @RequestBody ChannelConfig dto) {
        if (dto == null) {
            return ReplyHelper.invalidParam();
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.addChannelConfig(loginInfo, dto);
    }

    /**
     * 移除渠道模板
     *
     * @param info 用户关键信息
     * @param id   渠道模板ID
     * @return Reply
     */
    @DeleteMapping("/v1.0/scenes/channels")
    public Reply removeChannelConfig(@RequestHeader("loginInfo") String info, @RequestBody String id) {
        if (id == null || id.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.removeChannelConfig(loginInfo, id);
    }
}
