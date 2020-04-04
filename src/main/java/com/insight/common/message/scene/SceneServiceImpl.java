package com.insight.common.message.scene;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insight.common.message.common.MessageDal;
import com.insight.common.message.common.dto.SceneListDto;
import com.insight.common.message.common.dto.SceneTemplateListDto;
import com.insight.common.message.common.entity.Scene;
import com.insight.common.message.common.entity.SceneTemplate;
import com.insight.common.message.common.mapper.SceneMapper;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.Log;
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
public class SceneServiceImpl implements SceneService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MessageDal dal;
    private final SceneMapper mapper;

    /**
     * 构造方法
     *
     * @param dal    TemplateMapper
     * @param mapper SceneMapper
     */
    public SceneServiceImpl(MessageDal dal, SceneMapper mapper) {
        this.dal = dal;
        this.mapper = mapper;
    }

    /**
     * 获取消息场景列表
     *
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    @Override
    public Reply getScenes(String keyword, int page, int size) {
        PageHelper.startPage(page, size);
        List<SceneListDto> scenes = mapper.getScenes(keyword);
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
            return ReplyHelper.fail("ID不存在,未读取数据");
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
        int count = mapper.getSceneCount(dto.getCode());
        if (count > 0) {
            return ReplyHelper.invalidParam("场景编码已存在");
        }

        String id = uuid();
        dto.setId(id);
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

        int count = mapper.getSceneCount(dto.getCode());
        if (count > 0) {
            return ReplyHelper.invalidParam("场景编码已存在");
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

        int count = mapper.getConfigCount(id);
        if (count > 0){
            return ReplyHelper.fail("该消息场景下配置有模板,请先删除配置");
        }

        mapper.deleteScene(id);
        dal.writeLog(info, OperateType.DELETE, "消息场景管理", id, scene);

        return ReplyHelper.success();
    }

    /**
     * 获取场景模板配置列表
     *
     * @param tenantId 租户ID
     * @param sceneId  场景ID
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    @Override
    public Reply getSceneTemplates(String tenantId, String sceneId, String keyword, int page, int size) {
        PageHelper.startPage(page, size);
        List<SceneTemplateListDto> templates = mapper.getSceneTemplates(tenantId, sceneId, keyword);
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
        dto.setCreator(info.getUserName());
        dto.setCreatorId(info.getUserId());
        dto.setCreatedTime(LocalDateTime.now());

        mapper.addSceneTemplate(dto);
        dal.writeLog(info, OperateType.INSERT, "消息场景管理", id, dto);

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
        SceneTemplate config = mapper.getSceneTemplate(id);
        if (config == null) {
            return ReplyHelper.fail("ID不存在,未删除数据");
        }

        mapper.deleteSceneTemplate(id);
        dal.writeLog(info, OperateType.DELETE, "消息场景管理", id, config);

        return ReplyHelper.success();
    }

    /**
     * 获取日志列表
     *
     * @param tenantId 租户ID
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    @Override
    public Reply getSceneLogs(String tenantId, String keyword, int page, int size) {
        PageHelper.startPage(page, size);
        List<Log> logs = dal.getLogs(tenantId, "消息场景管理", keyword);
        PageInfo<Log> pageInfo = new PageInfo<>(logs);

        return ReplyHelper.success(logs, pageInfo.getTotal());
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @Override
    public Reply getSceneLog(String id) {
        Log log = dal.getLog(id);
        if (log == null) {
            return ReplyHelper.fail("ID不存在,未读取数据");
        }

        return ReplyHelper.success(log);
    }
}
