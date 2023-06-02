package com.insight.common.message.scene;

import com.insight.common.message.common.client.LogClient;
import com.insight.common.message.common.client.LogServiceClient;
import com.insight.common.message.common.dto.SceneConfigDto;
import com.insight.common.message.common.entity.OperateType;
import com.insight.common.message.common.entity.Scene;
import com.insight.common.message.common.entity.SceneConfig;
import com.insight.utils.Json;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息管理服务控制器
 */
@RestController
@RequestMapping("/common/message")
public class SceneController {
    private static final String BUSINESS = "Scene";
    private final LogServiceClient client;
    private final SceneService service;

    /**
     * 构造方法
     *
     * @param client  Feign客户端
     * @param service 注入Service
     */
    public SceneController(LogServiceClient client, SceneService service) {
        this.client = client;
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
     * @param loginInfo 用户关键信息
     * @param dto  场景DTO
     * @return Reply
     */
    @PostMapping("/v1.0/scenes")
    public Long newScene(@RequestHeader("loginInfo") String loginInfo, @Valid @RequestBody Scene dto) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);

        var id = service.newScene(info, dto);
        LogClient.writeLog(info, BUSINESS, OperateType.NEW, id, dto);
        return id;
    }

    /**
     * 编辑场景
     *
     * @param loginInfo 用户关键信息
     * @param id   场景ID
     * @param dto  场景DTO
     */
    @PutMapping("/v1.0/scenes/{id}")
    public void editScene(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id, @Valid @RequestBody Scene dto) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        dto.setId(id);

        service.editScene(info, dto);
        LogClient.writeLog(info, BUSINESS, OperateType.EDIT, id, dto);
    }

    /**
     * 删除场景
     *
     * @param loginInfo 用户关键信息
     * @param id   场景ID
     */
    @DeleteMapping("/v1.0/scenes/{id}")
    public void deleteScene(@RequestHeader("loginInfo") String loginInfo, @RequestBody Long id) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);

        service.deleteScene(info, id);
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, null);
    }

    /**
     * 获取场景配置列表
     *
     * @param loginInfo 用户关键信息
     * @param id   场景ID
     * @return Reply
     */
    @GetMapping("/v1.0/scenes/{id}/configs")
    public List<SceneConfigDto> getSceneConfigs(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);

        return service.getSceneConfigs(info, id);
    }

    /**
     * 新增场景配置
     *
     * @param loginInfo 用户关键信息
     * @param id   场景ID
     * @param dto  场景配置DTO
     * @return Reply
     */
    @PostMapping("/v1.0/scenes/{id}/configs")
    public Long newSceneConfig(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id, @Valid @RequestBody SceneConfig dto) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        dto.setSceneId(id);

        var configId = service.newSceneConfig(info, dto);
        LogClient.writeLog(info, BUSINESS, OperateType.NEW, configId, dto);
        return configId;
    }

    /**
     * 编辑场景配置
     *
     * @param loginInfo 用户关键信息
     * @param id   场景配置ID
     * @param dto  场景配置DTO
     */
    @PutMapping("/v1.0/scenes/configs/{id}")
    public void editSceneConfig(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id, @Valid @RequestBody SceneConfig dto) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        dto.setId(id);

        service.editSceneConfig(info, dto);
        LogClient.writeLog(info, BUSINESS, OperateType.EDIT, id, dto);
    }

    /**
     * 删除场景配置
     *
     * @param loginInfo 用户关键信息
     * @param id   场景配置ID
     */
    @DeleteMapping("/v1.0/scenes/configs/{id}")
    public void deleteSceneConfig(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);

        service.deleteSceneConfig(info, id);
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, null);
    }

    /**
     * 查询日志
     *
     * @param loginInfo 用户登录信息
     * @param search    查询条件
     * @return 日志集合
     */
    @GetMapping("/v1.0/scenes/{id}/logs")
    public Reply getAirportLogs(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id, Search search) {
        var info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        return client.getLogs(id, "Scene", search.getKeyword());
    }

    /**
     * 获取日志
     *
     * @param loginInfo 用户登录信息
     * @param id        日志ID
     * @return 日志VO
     */
    @GetMapping("/v1.0/scenes/logs/{id}")
    public Reply getAirportLog(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id) {
        var info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        return client.getLog(id);
    }
}
