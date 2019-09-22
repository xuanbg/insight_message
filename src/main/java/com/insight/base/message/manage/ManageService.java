package com.insight.base.message.manage;

import com.insight.base.message.common.entity.SceneTemplate;
import com.insight.base.message.common.entity.Scene;
import com.insight.base.message.common.entity.Template;
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.Reply;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息管理服务接口
 */
public interface ManageService {

    /**
     * 获取短信模板列表
     *
     * @param tenantId 租户ID
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    Reply getTemplates(String tenantId, String keyword, int page, int size);

    /**
     * 获取短信模板
     *
     * @param id 短信模板ID
     * @return Reply
     */
    Reply getTemplate(String id);

    /**
     * 新增短信模板
     *
     * @param info 用户关键信息
     * @param dto  短信模板DTO
     * @return Reply
     */
    Reply newTemplate(LoginInfo info, Template dto);

    /**
     * 编辑短信模板
     *
     * @param info 用户关键信息
     * @param dto  短信模板DTO
     * @return Reply
     */
    Reply editTemplate(LoginInfo info, Template dto);

    /**
     * 删除短信模板
     *
     * @param info 用户关键信息
     * @param id   短信模板ID
     * @return Reply
     */
    Reply deleteTemplate(LoginInfo info, String id);

    /**
     * 改变短信模板禁用/启用状态
     *
     * @param info   用户关键信息
     * @param id     短信模板ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    Reply changeTemplateStatus(LoginInfo info, String id, boolean status);

    /**
     * 获取场景列表
     *
     * @param tenantId 租户ID
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    Reply getScenes(String tenantId, String keyword, int page, int size);

    /**
     * 获取场景
     *
     * @param id 场景ID
     * @return Reply
     */
    Reply getScene(String id);

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
    Reply deleteScene(LoginInfo info, String id);

    /**
     * 改变场景禁用/启用状态
     *
     * @param info   用户关键信息
     * @param id     场景ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    Reply changeSceneStatus(LoginInfo info, String id, boolean status);

    /**
     * 获取渠道模板配置列表
     *
     * @param tenantId 租户ID
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    Reply getSceneTemplates(String tenantId, String keyword, int page, int size);

    /**
     * 添加渠道模板
     *
     * @param info 用户关键信息
     * @param dto  渠道模板DTO
     * @return Reply
     */
    Reply addSceneTemplate(LoginInfo info, SceneTemplate dto);

    /**
     * 移除渠道模板
     *
     * @param info 用户关键信息
     * @param id   渠道模板ID
     * @return Reply
     */
    Reply removeSceneTemplate(LoginInfo info, String id);
}
