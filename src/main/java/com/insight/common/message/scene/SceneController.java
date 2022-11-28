package com.insight.common.message.scene;

import com.insight.common.message.common.dto.SceneConfigDto;
import com.insight.common.message.common.entity.Scene;
import com.insight.common.message.common.entity.SceneConfig;
import com.insight.utils.Json;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    public Reply getScenes(Search search) {
        return service.getScenes(search);
    }

    /**
     * 获取场景
     *
     * @param id 场景ID
     * @return Reply
     */
    @GetMapping("/v1.0/scenes/{id}")
    public Scene getScene(@PathVariable Long id) {
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
    public Long newScene(@RequestHeader("loginInfo") String info, @Valid @RequestBody Scene dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        return service.newScene(loginInfo, dto);
    }

    /**
     * 编辑场景
     *
     * @param info 用户关键信息
     * @param id   场景ID
     * @param dto  场景DTO
     */
    @PutMapping("/v1.0/scenes/{id}")
    public void editScene(@RequestHeader("loginInfo") String info, @PathVariable Long id, @Valid @RequestBody Scene dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        dto.setId(id);

        service.editScene(loginInfo, dto);
    }

    /**
     * 删除场景
     *
     * @param info 用户关键信息
     * @param id   场景ID
     */
    @DeleteMapping("/v1.0/scenes/{id}")
    public void deleteScene(@RequestHeader("loginInfo") String info, @RequestBody Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        service.deleteScene(loginInfo, id);
    }

    /**
     * 获取场景配置列表
     *
     * @param info 用户关键信息
     * @param id   场景ID
     * @return Reply
     */
    @GetMapping("/v1.0/scenes/{id}/configs")
    public List<SceneConfigDto> getSceneConfigs(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
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
    public Long newSceneConfig(@RequestHeader("loginInfo") String info, @PathVariable Long id, @Valid @RequestBody SceneConfig dto) {
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
     */
    @PutMapping("/v1.0/scenes/configs/{id}")
    public void editSceneConfig(@RequestHeader("loginInfo") String info, @PathVariable Long id, @Valid @RequestBody SceneConfig dto) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        dto.setId(id);

        service.editSceneConfig(loginInfo, dto);
    }

    /**
     * 删除场景配置
     *
     * @param info 用户关键信息
     * @param id   场景配置ID
     */
    @DeleteMapping("/v1.0/scenes/configs/{id}")
    public void deleteSceneConfig(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        service.deleteSceneConfig(loginInfo, id);
    }

    /**
     * 获取日志列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    @GetMapping("/v1.0/scenes/logs")
    public Reply getSceneLogs(Search search) {
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
