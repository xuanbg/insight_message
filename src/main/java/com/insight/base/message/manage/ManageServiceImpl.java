package com.insight.base.message.manage;

import com.insight.base.message.common.entity.ChannelTemp;
import com.insight.base.message.common.entity.Scene;
import com.insight.base.message.common.entity.Template;
import com.insight.util.pojo.Reply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息管理服务
 */
@Service
public class ManageServiceImpl implements ManageService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取短信模板列表
     *
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @Override
    public Reply getTemplates(String keyword, int page, int size) {
        return null;
    }

    /**
     * 获取短信模板
     *
     * @param id 短信模板ID
     * @return Reply
     */
    @Override
    public Reply getTemplate(String id) {
        return null;
    }

    /**
     * 新增短信模板
     *
     * @param dto 短信模板DTO
     * @return Reply
     */
    @Override
    public Reply newTemplate(Template dto) {
        return null;
    }

    /**
     * 编辑短信模板
     *
     * @param dto 短信模板DTO
     * @return Reply
     */
    @Override
    public Reply editTemplate(Template dto) {
        return null;
    }

    /**
     * 删除短信模板
     *
     * @param id 短信模板ID
     * @return Reply
     */
    @Override
    public Reply deleteTemplate(String id) {
        return null;
    }

    /**
     * 改变短信模板禁用/启用状态
     *
     * @param id     短信模板ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    @Override
    public Reply changeTemplateStatus(String id, boolean status) {
        return null;
    }

    /**
     * 获取场景列表
     *
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @Override
    public Reply getScenes(String keyword, int page, int size) {
        return null;
    }

    /**
     * 获取场景
     *
     * @param id 场景ID
     * @return Reply
     */
    @Override
    public Reply getScene(String id) {
        return null;
    }

    /**
     * 新增场景
     *
     * @param dto 场景DTO
     * @return Reply
     */
    @Override
    public Reply newScene(Scene dto) {
        return null;
    }

    /**
     * 编辑场景
     *
     * @param dto 场景DTO
     * @return Reply
     */
    @Override
    public Reply editScene(Scene dto) {
        return null;
    }

    /**
     * 删除场景
     *
     * @param id 场景ID
     * @return Reply
     */
    @Override
    public Reply deleteScene(String id) {
        return null;
    }

    /**
     * 改变场景禁用/启用状态
     *
     * @param id     场景ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    @Override
    public Reply changeSceneStatus(String id, boolean status) {
        return null;
    }

    /**
     * 添加渠道模板
     *
     * @param dto 渠道模板DTO
     * @return Reply
     */
    @Override
    public Reply addChannel(ChannelTemp dto) {
        return null;
    }

    /**
     * 移除渠道模板
     *
     * @param id 渠道模板ID
     * @return Reply
     */
    @Override
    public Reply removeChannel(String id) {
        return null;
    }
}
