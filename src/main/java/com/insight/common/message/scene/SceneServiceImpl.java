package com.insight.common.message.scene;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insight.common.message.common.client.LogClient;
import com.insight.common.message.common.client.LogServiceClient;
import com.insight.common.message.common.dto.SceneConfigDto;
import com.insight.common.message.common.dto.SceneDto;
import com.insight.common.message.common.entity.Scene;
import com.insight.common.message.common.entity.SceneConfig;
import com.insight.common.message.common.mapper.SceneMapper;
import com.insight.utils.ReplyHelper;
import com.insight.utils.SnowflakeCreator;
import com.insight.utils.pojo.LoginInfo;
import com.insight.utils.pojo.OperateType;
import com.insight.utils.pojo.Reply;
import com.insight.utils.pojo.SearchDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息管理服务
 */
@Service
public class SceneServiceImpl implements SceneService {
    private static final String BUSINESS = "消息场景管理";
    private final SnowflakeCreator creator;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LogServiceClient client;
    private final SceneMapper mapper;

    /**
     * 构造方法
     *
     * @param creator 雪花算法ID生成器
     * @param client  LogServiceClient
     * @param mapper  SceneMapper
     */
    public SceneServiceImpl(SnowflakeCreator creator, LogServiceClient client, SceneMapper mapper) {
        this.creator = creator;
        this.client = client;
        this.mapper = mapper;
    }

    /**
     * 获取消息场景列表
     *
     * @param search 查询DTO
     * @return Reply
     */
    @Override
    public Reply getScenes(SearchDto search) {
        PageHelper.startPage(search.getPage(), search.getSize());
        List<SceneDto> scenes = mapper.getScenes(search.getKeyword());
        PageInfo<SceneDto> pageInfo = new PageInfo<>(scenes);

        return ReplyHelper.success(scenes, pageInfo.getTotal());
    }

    /**
     * 获取消息场景
     *
     * @param id 消息场景ID
     * @return Reply
     */
    @Override
    public Reply getScene(Long id) {
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
        Long id = creator.nextId(1);
        int count = mapper.getSceneCount(id, dto.getCode());
        if (count > 0) {
            return ReplyHelper.invalidParam("场景编码已存在");
        }

        dto.setId(id);
        dto.setCreator(info.getUserName());
        dto.setCreatorId(info.getUserId());

        mapper.addScene(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.INSERT, id, dto);

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
        Long id = dto.getId();
        Scene scene = mapper.getScene(id);
        if (scene == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        int count = mapper.getSceneCount(id, dto.getCode());
        if (count > 0) {
            return ReplyHelper.invalidParam("场景编码已存在");
        }

        mapper.editScene(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, dto);

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
    public Reply deleteScene(LoginInfo info, Long id) {
        Scene scene = mapper.getScene(id);
        if (scene == null) {
            return ReplyHelper.fail("ID不存在,未删除数据");
        }

        mapper.deleteScene(id);
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, scene);

        return ReplyHelper.success();
    }

    /**
     * 获取场景配置列表
     *
     * @param info    用户关键信息
     * @param sceneId 场景ID
     * @return Reply
     */
    @Override
    public Reply getSceneConfigs(LoginInfo info, Long sceneId) {
        List<SceneConfigDto> configs = mapper.getSceneConfigs(info.getTenantId(), sceneId);

        return ReplyHelper.success(configs);
    }

    /**
     * 新增场景配置
     *
     * @param info 用户关键信息
     * @param dto  场景配置DTO
     * @return Reply
     */
    @Override
    public Reply newSceneConfig(LoginInfo info, SceneConfig dto) {
        Long tenantId = info.getTenantId();
        Long id = creator.nextId(2);

        int count = mapper.getConfigCount(dto.getSceneId(), tenantId, dto.getAppId());
        if (count > 0) {
            return ReplyHelper.invalidParam("场景配置已存在,请勿重复添加");
        }

        dto.setId(id);
        dto.setTenantId(tenantId);
        dto.setCreator(info.getUserName());
        dto.setCreatorId(info.getUserId());

        mapper.addSceneConfig(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.INSERT, id, dto);

        return ReplyHelper.created(id);
    }

    /**
     * 编辑场景配置
     *
     * @param info 用户关键信息
     * @param dto  场景配置DTO
     * @return Reply
     */
    @Override
    public Reply editSceneConfig(LoginInfo info, SceneConfig dto) {
        Long id = dto.getId();
        SceneConfig config = mapper.getSceneConfig(id);
        if (config == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        Long tenantId = info.getTenantId();
        if (tenantId != null && !tenantId.equals(config.getTenantId())) {
            return ReplyHelper.fail("您无权修改该数据");
        }

        mapper.updateSceneConfig(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, dto);

        return ReplyHelper.success();
    }

    /**
     * 删除场景配置
     *
     * @param info 用户关键信息
     * @param id   场景配置ID
     * @return Reply
     */
    @Override
    public Reply deleteSceneConfig(LoginInfo info, Long id) {
        SceneConfig config = mapper.getSceneConfig(id);
        if (config == null) {
            return ReplyHelper.fail("ID不存在,未删除数据");
        }

        mapper.deleteSceneConfig(id);
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, config);

        return ReplyHelper.success();
    }

    /**
     * 获取日志列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    @Override
    public Reply getSceneLogs(SearchDto search) {
        return client.getLogs(BUSINESS, search.getKeyword(), search.getPage(), search.getSize());
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @Override
    public Reply getSceneLog(Long id) {
        return client.getLog(id);
    }
}
