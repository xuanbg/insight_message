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
import com.insight.utils.Util;
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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LogServiceClient client;
    private final SceneMapper mapper;

    /**
     * 构造方法
     *
     * @param client LogServiceClient
     * @param mapper SceneMapper
     */
    public SceneServiceImpl(LogServiceClient client, SceneMapper mapper) {
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
        String id = Util.uuid();
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
        String id = dto.getId();
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
    public Reply deleteScene(LoginInfo info, String id) {
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
     * @param search  查询DTO
     * @param sceneId 场景ID
     * @return Reply
     */
    @Override
    public Reply getSceneConfigs(LoginInfo info, SearchDto search, String sceneId) {
        PageHelper.startPage(search.getPage(), search.getSize());
        List<SceneConfigDto> configs = mapper.getSceneConfigs(info.getTenantId(), sceneId);
        PageInfo<SceneConfigDto> pageInfo = new PageInfo<>(configs);

        return ReplyHelper.success(configs, pageInfo.getTotal());
    }

    /**
     * 新增场景配置
     *
     * @param info 用户关键信息
     * @param dto  场景配置DTO
     * @return Reply
     */
    @Override
    public Reply newSceneConfigs(LoginInfo info, SceneConfig dto) {
        String tenantId = info.getTenantId();
        if (Util.isNotEmpty(tenantId)) {
            dto.setTenantId(tenantId);
        }

        String id = Util.uuid();
        dto.setId(id);
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
    public Reply editSceneConfigs(LoginInfo info, SceneConfig dto) {
        String id = dto.getId();
        SceneConfig config = mapper.getSceneConfig(id);
        if (config == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        String tenantId = info.getTenantId();
        if (Util.isNotEmpty(tenantId) && !tenantId.equals(config.getTenantId())) {
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
    public Reply deleteSceneConfig(LoginInfo info, String id) {
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
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @Override
    public Reply getSceneLogs(String keyword, int page, int size) {
        return client.getLogs(BUSINESS, keyword, page, size);
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @Override
    public Reply getSceneLog(String id) {
        return client.getLog(id);
    }
}
