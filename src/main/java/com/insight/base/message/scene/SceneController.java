package com.insight.base.message.scene;

import com.insight.base.message.common.entity.Scene;
import com.insight.base.message.common.entity.SceneTemplate;
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
public class SceneController {
    private final SceneService service;

    /**
     * 构造方法
     *
     * @param service 注入Service
     */
    public SceneController(SceneService service) {
        this.service = service;
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
     * 获取场景模板配置列表
     *
     * @param id      场景ID
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @GetMapping("/v1.0/scenes/{id}/configs")
    public Reply getSceneTemplates(@RequestHeader("loginInfo") String info, @PathVariable("id") String id, @RequestParam(required = false) String keyword,
                                   @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.getSceneTemplates(loginInfo.getTenantId(), id, keyword, page, size);
    }

    /**
     * 添加场景配置
     *
     * @param info 用户关键信息
     * @param dto  渠道模板DTO
     * @return Reply
     */
    @PostMapping("/v1.0/scenes/configs")
    public Reply addSceneTemplate(@RequestHeader("loginInfo") String info, @Valid @RequestBody SceneTemplate dto) {
        if (dto == null) {
            return ReplyHelper.invalidParam();
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.addSceneTemplate(loginInfo, dto);
    }

    /**
     * 移除场景配置
     *
     * @param info 用户关键信息
     * @param id   渠道模板ID
     * @return Reply
     */
    @DeleteMapping("/v1.0/scenes/configs")
    public Reply removeSceneTemplate(@RequestHeader("loginInfo") String info, @RequestBody String id) {
        if (id == null || id.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.removeSceneTemplate(loginInfo, id);
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
    @GetMapping("/v1.0/scenes/logs")
    public Reply getSceneLogs(@RequestHeader("loginInfo") String info, @RequestParam(required = false) String keyword,
                              @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.getSceneLogs(loginInfo.getTenantId(), keyword, page, size);
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @GetMapping("/v1.0/scenes/logs/{id}")
    Reply getSceneLog(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        return service.getSceneLog(id);
    }
}
