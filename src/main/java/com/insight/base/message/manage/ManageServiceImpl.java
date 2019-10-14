package com.insight.base.message.manage;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insight.base.message.common.MessageDal;
import com.insight.base.message.common.dto.SceneListDto;
import com.insight.base.message.common.dto.SceneTemplateListDto;
import com.insight.base.message.common.dto.TemplateListDto;
import com.insight.base.message.common.entity.Scene;
import com.insight.base.message.common.entity.SceneTemplate;
import com.insight.base.message.common.entity.Template;
import com.insight.base.message.common.mapper.SceneMapper;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.OperateType;
import com.insight.util.pojo.Reply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.insight.util.Generator.uuid;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息管理服务
 */
@Service
public class ManageServiceImpl implements ManageService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MessageDal dal;
    private final SceneMapper mapper;

    /**
     * 构造方法
     *
     * @param dal    TemplateMapper
     * @param mapper SceneMapper
     */
    public ManageServiceImpl(MessageDal dal, SceneMapper mapper) {
        this.dal = dal;
        this.mapper = mapper;
    }

    /**
     * 获取短信模板列表
     *
     * @param tenantId 租户ID
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    @Override
    public Reply getTemplates(String tenantId, String keyword, int page, int size) {
        PageHelper.startPage(page, size);
        List<TemplateListDto> templates = mapper.getTemplates(tenantId, keyword);
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
        Template template = mapper.getTemplate(id);
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
        String id = uuid();
        String tenantId = info.getTenantId();
        dto.setId(id);
        dto.setTenantId(tenantId);
        dto.setCode(dal.getTemplateCode(tenantId));
        dto.setDeptId(info.getDeptId());
        dto.setCreator(info.getUserName());
        dto.setCreatorId(info.getUserId());
        dto.setCreatedTime(LocalDateTime.now());

        mapper.addTemplate(dto);
        dal.writeLog(info, OperateType.INSERT, "消息模板管理", id, dto);

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
        Template template = mapper.getTemplate(id);
        if (template == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        mapper.editTemplate(dto);
        dal.writeLog(info, OperateType.UPDATE, "消息模板管理", id, dto);

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
        Template template = mapper.getTemplate(id);
        if (template == null) {
            return ReplyHelper.fail("ID不存在,未删除数据");
        }

        mapper.deleteTemplate(id);
        dal.writeLog(info, OperateType.DELETE, "消息模板管理", id, template);

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
        Template template = mapper.getTemplate(id);
        if (template == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        mapper.changeTemplateStatus(id, status);
        dal.writeLog(info, OperateType.UPDATE, "消息模板管理", id, template);

        return ReplyHelper.success();
    }

    /**
     * 获取消息场景列表
     *
     * @param tenantId 租户ID
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    @Override
    public Reply getScenes(String tenantId, String keyword, int page, int size) {
        PageHelper.startPage(page, size);
        List<SceneListDto> scenes = mapper.getScenes(tenantId, keyword);
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
        Scene scene = mapper.getScene(id);
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
        String id = uuid();
        dto.setId(id);
        dto.setDeptId(info.getDeptId());
        dto.setCreator(info.getUserName());
        dto.setCreatorId(info.getUserId());
        dto.setCreatedTime(LocalDateTime.now());

        mapper.addScene(dto);
        dal.writeLog(info, OperateType.INSERT, "消息场景管理", id, dto);

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
        Scene scene = mapper.getScene(id);
        if (scene == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        mapper.editScene(dto);
        dal.writeLog(info, OperateType.UPDATE, "消息场景管理", id, dto);

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
        Scene scene = mapper.getScene(id);
        if (scene == null) {
            return ReplyHelper.fail("ID不存在,未删除数据");
        }

        mapper.deleteScene(id);
        dal.writeLog(info, OperateType.DELETE, "消息场景管理", id, scene);

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
        Scene scene = mapper.getScene(id);
        if (scene == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        mapper.changeSceneStatus(id, status);
        dal.writeLog(info, OperateType.UPDATE, "消息场景管理", id, scene);

        return ReplyHelper.success();
    }

    /**
     * 获取渠道模板配置列表
     *
     * @param tenantId 租户ID
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    @Override
    public Reply getSceneTemplates(String tenantId, String keyword, int page, int size) {
        PageHelper.startPage(page, size);
        List<SceneTemplateListDto> templates = mapper.getSceneTemplates(tenantId, keyword);
        PageInfo<SceneTemplateListDto> pageInfo = new PageInfo<>(templates);

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
    public Reply addSceneTemplate(LoginInfo info, SceneTemplate dto) {
        String id = uuid();
        dto.setId(id);
        dto.setDeptId(info.getDeptId());
        dto.setCreator(info.getUserName());
        dto.setCreatorId(info.getUserId());
        dto.setCreatedTime(LocalDateTime.now());

        mapper.addSceneTemplate(dto);
        dal.writeLog(info, OperateType.INSERT, "消息模板配置管理", id, dto);

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
    public Reply removeSceneTemplate(LoginInfo info, String id) {
        Scene scene = mapper.getScene(id);
        if (scene == null) {
            return ReplyHelper.fail("ID不存在,未删除数据");
        }

        mapper.deleteSceneTemplate(id);
        dal.writeLog(info, OperateType.DELETE, "消息模板配置管理", id, scene);

        return ReplyHelper.success();
    }
}
