package com.insight.common.message.scene;

import com.github.pagehelper.PageHelper;
import com.insight.common.message.common.client.LogClient;
import com.insight.common.message.common.dto.SceneConfigDto;
import com.insight.common.message.common.entity.Scene;
import com.insight.common.message.common.entity.SceneConfig;
import com.insight.common.message.common.mapper.SceneMapper;
import com.insight.utils.ReplyHelper;
import com.insight.utils.SnowflakeCreator;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.BusinessException;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;
import com.insight.utils.pojo.message.OperateType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息管理服务
 */
@Service
public class SceneServiceImpl implements SceneService {
    private static final String BUSINESS = "Scene";
    private final SnowflakeCreator creator;
    private final SceneMapper mapper;

    /**
     * 构造方法
     *
     * @param creator 雪花算法ID生成器
     * @param mapper  SceneMapper
     */
    public SceneServiceImpl(SnowflakeCreator creator, SceneMapper mapper) {
        this.creator = creator;
        this.mapper = mapper;
    }

    /**
     * 获取消息场景列表
     *
     * @param search 查询DTO
     * @return Reply
     */
    @Override
    public Reply getScenes(Search search) {
        try (var page = PageHelper.startPage(search.getPageNum(), search.getPageSize()).setOrderBy(search.getOrderBy())
                .doSelectPage(() -> mapper.getScenes(search))) {
            var total = page.getTotal();
            return total > 0 ? ReplyHelper.success(page.getResult(), total) : ReplyHelper.resultIsEmpty();
        }
    }

    /**
     * 获取消息场景
     *
     * @param id 消息场景ID
     * @return Reply
     */
    @Override
    public Scene getScene(Long id) {
        Scene scene = mapper.getScene(id);
        if (scene == null) {
            throw new BusinessException("ID不存在,未读取数据");
        }

        return scene;
    }

    /**
     * 新增消息场景
     *
     * @param info 用户关键信息
     * @param dto  消息场景DTO
     * @return Reply
     */
    @Override
    public Long newScene(LoginInfo info, Scene dto) {
        Long id = creator.nextId(1);
        int count = mapper.getSceneCount(id, dto.getCode());
        if (count > 0) {
            throw new BusinessException("场景编码已存在");
        }

        dto.setId(id);
        dto.setCreator(info.getName());
        dto.setCreatorId(info.getId());

        mapper.addScene(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.NEW, id, dto);

        return id;
    }

    /**
     * 编辑消息场景
     *
     * @param info 用户关键信息
     * @param dto  消息场景DTO
     */
    @Override
    public void editScene(LoginInfo info, Scene dto) {
        Long id = dto.getId();
        Scene scene = mapper.getScene(id);
        if (scene == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        int count = mapper.getSceneCount(id, dto.getCode());
        if (count > 0) {
            throw new BusinessException("场景编码已存在");
        }

        mapper.editScene(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.EDIT, id, dto);
    }

    /**
     * 删除消息场景
     *
     * @param info 用户关键信息
     * @param id   消息场景ID
     */
    @Override
    public void deleteScene(LoginInfo info, Long id) {
        Scene scene = mapper.getScene(id);
        if (scene == null) {
            throw new BusinessException("ID不存在,未删除数据");
        }

        mapper.deleteScene(id);
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, scene);
    }

    /**
     * 获取场景配置列表
     *
     * @param info    用户关键信息
     * @param sceneId 场景ID
     * @return Reply
     */
    @Override
    public List<SceneConfigDto> getSceneConfigs(LoginInfo info, Long sceneId) {
        return mapper.getSceneConfigs(info.getTenantId(), sceneId);
    }

    /**
     * 新增场景配置
     *
     * @param info 用户关键信息
     * @param dto  场景配置DTO
     * @return Reply
     */
    @Override
    public Long newSceneConfig(LoginInfo info, SceneConfig dto) {
        Long tenantId = info.getTenantId();
        Long id = creator.nextId(2);

        int count = mapper.getConfigCount(dto.getSceneId(), tenantId, dto.getAppId());
        if (count > 0) {
            throw new BusinessException("场景配置已存在,请勿重复添加");
        }

        dto.setId(id);
        dto.setTenantId(tenantId);
        dto.setCreator(info.getName());
        dto.setCreatorId(info.getId());

        mapper.addSceneConfig(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.NEW, id, dto);

        return id;
    }

    /**
     * 编辑场景配置
     *
     * @param info 用户关键信息
     * @param dto  场景配置DTO
     */
    @Override
    public void editSceneConfig(LoginInfo info, SceneConfig dto) {
        Long id = dto.getId();
        SceneConfig config = mapper.getSceneConfig(id);
        if (config == null) {
            throw new BusinessException("ID不存在,未更新数据");
        }

        Long tenantId = info.getTenantId();
        if (tenantId != null && !tenantId.equals(config.getTenantId())) {
            throw new BusinessException("您无权修改该数据");
        }

        mapper.updateSceneConfig(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.EDIT, id, dto);
    }

    /**
     * 删除场景配置
     *
     * @param info 用户关键信息
     * @param id   场景配置ID
     */
    @Override
    public void deleteSceneConfig(LoginInfo info, Long id) {
        SceneConfig config = mapper.getSceneConfig(id);
        if (config == null) {
            throw new BusinessException("ID不存在,未删除数据");
        }

        mapper.deleteSceneConfig(id);
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, config);
    }
}
