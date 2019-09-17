package com.insight.base.message.manage;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insight.base.message.common.dto.ChannelConfigListDto;
import com.insight.base.message.common.dto.SceneListDto;
import com.insight.base.message.common.dto.TemplateListDto;
import com.insight.base.message.common.entity.ChannelConfig;
import com.insight.base.message.common.entity.Scene;
import com.insight.base.message.common.entity.Template;
import com.insight.base.message.common.mapper.SceneMapper;
import com.insight.base.message.common.mapper.TemplateMapper;
import com.insight.util.Generator;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.Log;
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.OperateType;
import com.insight.util.pojo.Reply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息管理服务
 */
@Service
public class ManageServiceImpl implements ManageService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TemplateMapper templateMapper;
    private final SceneMapper sceneMapper;

    /**
     * 构造方法
     *
     * @param templateMapper TemplateMapper
     * @param sceneMapper    SceneMapper
     */
    public ManageServiceImpl(TemplateMapper templateMapper, SceneMapper sceneMapper) {
        this.templateMapper = templateMapper;
        this.sceneMapper = sceneMapper;
    }

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
        PageHelper.startPage(page, size);
        List<TemplateListDto> templates = templateMapper.getTemplates(keyword);
        PageInfo<TemplateListDto> pageInfo = new PageInfo<>(templates);

        return ReplyHelper.success(templates, pageInfo.getTotal());
    }

    /**
     * 获取短信模板
     *
     * @param id 短信模板ID
     * @return Reply
     */
    @Override
    public Reply getTemplate(String id) {
        Template template = templateMapper.getTemplate(id);
        if (template == null) {
            return ReplyHelper.fail("ID不存在,未读取数据");
        }

        return ReplyHelper.success(template);
    }

    /**
     * 新增短信模板
     *
     * @param info 用户关键信息
     * @param dto  短信模板DTO
     * @return Reply
     */
    @Override
    public Reply newTemplate(LoginInfo info, Template dto) {
        String id = Generator.uuid();
        dto.setId(id);
        dto.setDeptId(info.getDeptId());
        dto.setCreator(info.getUserName());
        dto.setCreatorId(info.getUserId());
        dto.setCreatedTime(new Date());

        templateMapper.addTemplate(dto);
        writeLog(info, OperateType.INSERT, "消息模板管理", id, dto);

        return ReplyHelper.created(id);
    }

    /**
     * 编辑短信模板
     *
     * @param info 用户关键信息
     * @param dto  短信模板DTO
     * @return Reply
     */
    @Override
    public Reply editTemplate(LoginInfo info, Template dto) {
        String id = dto.getId();
        Template template = templateMapper.getTemplate(id);
        if (template == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        templateMapper.editTemplate(dto);
        writeLog(info, OperateType.UPDATE, "消息模板管理", id, dto);

        return ReplyHelper.success();
    }

    /**
     * 删除短信模板
     *
     * @param info 用户关键信息
     * @param id   短信模板ID
     * @return Reply
     */
    @Override
    public Reply deleteTemplate(LoginInfo info, String id) {
        Template template = templateMapper.getTemplate(id);
        if (template == null) {
            return ReplyHelper.fail("ID不存在,未删除数据");
        }

        templateMapper.deleteTemplate(id);
        writeLog(info, OperateType.DELETE, "消息模板管理", id, template);

        return ReplyHelper.success();
    }

    /**
     * 改变短信模板禁用/启用状态
     *
     * @param info   用户关键信息
     * @param id     短信模板ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    @Override
    public Reply changeTemplateStatus(LoginInfo info, String id, boolean status) {
        Template template = templateMapper.getTemplate(id);
        if (template == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        templateMapper.changeTemplateStatus(id, status);
        writeLog(info, OperateType.UPDATE, "消息模板管理", id, template);

        return ReplyHelper.success();
    }

    /**
     * 获取消息场景列表
     *
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @Override
    public Reply getScenes(String keyword, int page, int size) {
        PageHelper.startPage(page, size);
        List<SceneListDto> scenes = sceneMapper.getScenes(keyword);
        PageInfo<SceneListDto> pageInfo = new PageInfo<>(scenes);

        return ReplyHelper.success(scenes, pageInfo.getTotal());
    }

    /**
     * 获取消息场景
     *
     * @param id 消息场景ID
     * @return Reply
     */
    @Override
    public Reply getScene(String id) {
        Scene scene = sceneMapper.getScene(id);
        if (scene == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        return ReplyHelper.success(scene);
    }

    /**
     * 新增消息场景
     *
     * @param info 用户关键信息
     * @param dto  消息场景DTO
     * @return Reply
     */
    @Override
    public Reply newScene(LoginInfo info, Scene dto) {
        String id = Generator.uuid();
        dto.setId(id);
        dto.setDeptId(info.getDeptId());
        dto.setCreator(info.getUserName());
        dto.setCreatorId(info.getUserId());
        dto.setCreatedTime(new Date());

        sceneMapper.addScene(dto);
        writeLog(info, OperateType.INSERT, "消息场景管理", id, dto);

        return ReplyHelper.created(id);
    }

    /**
     * 编辑消息场景
     *
     * @param info 用户关键信息
     * @param dto  消息场景DTO
     * @return Reply
     */
    @Override
    public Reply editScene(LoginInfo info, Scene dto) {
        String id = dto.getId();
        Scene scene = sceneMapper.getScene(id);
        if (scene == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        sceneMapper.editScene(dto);
        writeLog(info, OperateType.UPDATE, "消息场景管理", id, dto);

        return ReplyHelper.success();
    }

    /**
     * 删除消息场景
     *
     * @param info 用户关键信息
     * @param id   消息场景ID
     * @return Reply
     */
    @Override
    public Reply deleteScene(LoginInfo info, String id) {
        Scene scene = sceneMapper.getScene(id);
        if (scene == null) {
            return ReplyHelper.fail("ID不存在,未删除数据");
        }

        sceneMapper.deleteScene(id);
        writeLog(info, OperateType.DELETE, "消息场景管理", id, scene);

        return ReplyHelper.success();
    }

    /**
     * 改变消息场景禁用/启用状态
     *
     * @param info   用户关键信息
     * @param id     消息场景ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    @Override
    public Reply changeSceneStatus(LoginInfo info, String id, boolean status) {
        Scene scene = sceneMapper.getScene(id);
        if (scene == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        sceneMapper.changeSceneStatus(id, status);
        writeLog(info, OperateType.UPDATE, "消息场景管理", id, scene);

        return ReplyHelper.success();
    }

    /**
     * 获取渠道模板配置列表
     *
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @Override
    public Reply getChannelConfigs(String keyword, int page, int size) {
        PageHelper.startPage(page, size);
        List<ChannelConfigListDto> templates = sceneMapper.getChannelTemps(keyword);
        PageInfo<ChannelConfigListDto> pageInfo = new PageInfo<>(templates);

        return ReplyHelper.success(templates, pageInfo.getTotal());
    }

    /**
     * 添加渠道模板
     *
     * @param info 用户关键信息
     * @param dto  渠道模板DTO
     * @return Reply
     */
    @Override
    public Reply addChannelConfig(LoginInfo info, ChannelConfig dto) {
        String id = Generator.uuid();
        dto.setId(id);
        dto.setDeptId(info.getDeptId());
        dto.setCreator(info.getUserName());
        dto.setCreatorId(info.getUserId());
        dto.setCreatedTime(new Date());

        sceneMapper.addChannelTemp(dto);
        writeLog(info, OperateType.INSERT, "消息模板配置管理", id, dto);

        return ReplyHelper.created(id);
    }

    /**
     * 移除渠道模板
     *
     * @param info 用户关键信息
     * @param id   渠道模板ID
     * @return Reply
     */
    @Override
    public Reply removeChannelConfig(LoginInfo info, String id) {
        Scene scene = sceneMapper.getScene(id);
        if (scene == null) {
            return ReplyHelper.fail("ID不存在,未删除数据");
        }

        sceneMapper.deleteScene(id);
        writeLog(info, OperateType.DELETE, "消息模板配置管理", id, scene);

        return ReplyHelper.success();
    }


    /**
     * 记录操作日志
     *
     * @param info     用户关键信息
     * @param type     操作类型
     * @param business 业务名称
     * @param id       业务ID
     * @param content  日志内容
     */
    private void writeLog(LoginInfo info, OperateType type, String business, String id, Object content) {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        threadPool.submit(() -> {
            Log log = new Log();
            log.setId(Generator.uuid());
            log.setType(type);
            log.setBusiness(business);
            log.setBusinessId(id);
            log.setContent(content);
            log.setDeptId(info.getDeptId());
            log.setCreator(info.getUserName());
            log.setCreatorId(info.getUserId());
            log.setCreatedTime(new Date());

            templateMapper.addLog(log);
        });
    }
}
