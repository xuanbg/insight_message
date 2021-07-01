package com.insight.common.message.scene;

import com.insight.common.message.common.entity.Scene;
import com.insight.common.message.common.entity.SceneConfig;
import com.insight.utils.Json;
import com.insight.utils.pojo.LoginInfo;
import com.insight.utils.pojo.Reply;
import com.insight.utils.pojo.SearchDto;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息管理服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/common/message")
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
     * @param search 查询DTO
     * @return Reply
     */
    @GetMapping("/v1.0/scenes")
    public Reply getScenes(SearchDto search) {
        return service.getScenes(search);
    }

    /**
     * 获取场景
     *
     * @param id 场景ID
     * @return Reply
     */
    @GetMapping("/v1.0/scenes/{id}")
    public Reply getScene(@PathVariable Long id) {
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
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.newScene(loginInfo, dto);
    }

    /**
     * 编辑场景
     *
     * @param info 用户关键信息
     * @param id   场景ID
     * @param dto  场景DTO
     * @return Reply
     */
    @PutMapping("/v1.0/scenes/{id}")
    public Reply editScene(@RequestHeader("loginInfo") String info, @PathVariable Long id, @Valid @RequestBody Scene dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        dto.setId(id);

        return service.editScene(loginInfo, dto);
    }

    /**
     * 删除场景
     *
     * @param info 用户关键信息
     * @param id   场景ID
     * @return Reply
     */
    @DeleteMapping("/v1.0/scenes/{id}")
    public Reply deleteScene(@RequestHeader("loginInfo") String info, @RequestBody Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.deleteScene(loginInfo, id);
    }

    /**
     * 获取场景配置列表
     *
     * @param info 用户关键信息
     * @param id   场景ID
     * @return Reply
     */
    @GetMapping("/v1.0/scenes/{id}/configs")
    public Reply getSceneConfigs(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.getSceneConfigs(loginInfo, id);
    }

    /**
     * 新增场景配置
     *
     * @param info 用户关键信息
     * @param id   场景ID
     * @param dto  场景配置DTO
     * @return Reply
     */
    @PostMapping("/v1.0/scenes/{id}/configs")
    public Reply newSceneConfig(@RequestHeader("loginInfo") String info, @PathVariable Long id, @Valid @RequestBody SceneConfig dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        dto.setSceneId(id);

        return service.newSceneConfig(loginInfo, dto);
    }

    /**
     * 编辑场景配置
     *
     * @param info 用户关键信息
     * @param id   场景配置ID
     * @param dto  场景配置DTO
     * @return Reply
     */
    @PutMapping("/v1.0/scenes/configs/{id}")
    public Reply editSceneConfig(@RequestHeader("loginInfo") String info, @PathVariable Long id, @Valid @RequestBody SceneConfig dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        dto.setId(id);

        return service.editSceneConfig(loginInfo, dto);
    }

    /**
     * 删除场景配置
     *
     * @param info 用户关键信息
     * @param id   场景配置ID
     * @return Reply
     */
    @DeleteMapping("/v1.0/scenes/configs/{id}")
    public Reply deleteSceneConfig(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.deleteSceneConfig(loginInfo, id);
    }

    /**
     * 获取日志列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    @GetMapping("/v1.0/scenes/logs")
    public Reply getSceneLogs(SearchDto search) {
        return service.getSceneLogs(search);
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @GetMapping("/v1.0/scenes/logs/{id}")
    Reply getSceneLog(@PathVariable Long id) {
        return service.getSceneLog(id);
    }
}
