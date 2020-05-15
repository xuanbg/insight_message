package com.insight.common.message.template;

import com.insight.common.message.common.entity.Template;
import com.insight.utils.pojo.LoginInfo;
import com.insight.utils.pojo.Reply;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息管理服务接口
 */
public interface TemplateService {

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
     * 获取日志列表
     *
     * @param tenantId 租户ID
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    Reply getTemplateLogs(String tenantId, String keyword, int page, int size);

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    Reply getTemplateLog(String id);
}
