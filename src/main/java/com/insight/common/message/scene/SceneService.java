package com.insight.common.message.scene;

import com.insight.common.message.common.entity.Scene;
import com.insight.common.message.common.entity.SceneConfig;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息管理服务接口
 */
public interface SceneService {

    /**
     * 获取场景列表
     *
     * @param search 查询DTO
     * @return Reply
     */
    Reply getScenes(Search search);

    /**
     * 获取场景
     *
     * @param id 场景ID
     * @return Reply
     */
    Reply getScene(Long id);

    /**
     * 新增场景
     *
     * @param info 用户关键信息
     * @param dto  场景DTO
     * @return Reply
     */
    Reply newScene(LoginInfo info, Scene dto);

    /**
     * 编辑场景
     *
     * @param info 用户关键信息
     * @param dto  场景DTO
     * @return Reply
     */
    Reply editScene(LoginInfo info, Scene dto);

    /**
     * 删除场景
     *
     * @param info 用户关键信息
     * @param id   场景ID
     * @return Reply
     */
    Reply deleteScene(LoginInfo info, Long id);

    /**
     * 获取场景配置列表
     *
     * @param info    用户关键信息
     * @param sceneId 场景ID
     * @return Reply
     */
    Reply getSceneConfigs(LoginInfo info, Long sceneId);

    /**
     * 新增场景配置
     *
     * @param info 用户关键信息
     * @param dto  场景配置DTO
     * @return Reply
     */
    Reply newSceneConfig(LoginInfo info, SceneConfig dto);

    /**
     * 编辑场景配置
     *
     * @param info 用户关键信息
     * @param dto  场景配置DTO
     * @return Reply
     */
    Reply editSceneConfig(LoginInfo info, SceneConfig dto);

    /**
     * 删除场景配置
     *
     * @param info 用户关键信息
     * @param id   场景配置ID
     * @return Reply
     */
    Reply deleteSceneConfig(LoginInfo info, Long id);

    /**
     * 获取日志列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    Reply getSceneLogs(Search search);

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    Reply getSceneLog(Long id);
}
