package com.insight.common.message.common.mapper;

import com.insight.common.message.common.dto.SceneConfigDto;
import com.insight.common.message.common.dto.SceneDto;
import com.insight.common.message.common.entity.Scene;
import com.insight.common.message.common.entity.SceneConfig;
import com.insight.utils.pojo.base.ArrayTypeHandler;
import com.insight.utils.pojo.base.Search;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019/9/17
 * @remark 场景DAL
 */
@Mapper
public interface SceneMapper {

    /**
     * 获取消息场景列表
     *
     * @param search 查询DTO
     * @return 消息场景列表
     */
    @Results({@Result(property = "param", column = "param", javaType = String.class, typeHandler = ArrayTypeHandler.class)})
    @Select("<script>select * from ims_scene " +
            "<if test = 'keyword != null'>where code = #{keyword} or name like concat('%',#{keyword},'%')</if></script>")
    List<SceneDto> getScenes(Search search);

    /**
     * 获取场景详情
     *
     * @param id 消息场景ID
     * @return 消息场景DTO
     */
    @Results({@Result(property = "params", column = "params", javaType = String.class, typeHandler = ArrayTypeHandler.class)})
    @Select("select * from ims_scene where id = #{id};")
    Scene getScene(Long id);

    /**
     * 获取指定编码的场景数量
     *
     * @param id   消息场景ID
     * @param code 消息场景编码
     * @return 场景数量
     */
    @Select("select count(*) from ims_scene where id != #{id} and code = #{code};")
    int getSceneCount(@Param("id") Long id, @Param("code") String code);

    /**
     * 新增消息场景
     *
     * @param scene 消息场景DTO
     */
    @Insert("insert ims_scene(id, `code`, type, `name`, title, tag, `param`, remark, creator, creator_id, created_time) values " +
            "(#{id}, #{code}, #{type}, #{name}, #{title}, #{tag}, #{param, typeHandler = com.insight.utils.pojo.base.ArrayTypeHandler}, " +
            "#{remark}, #{creator}, #{creatorId}, now());")
    void addScene(Scene scene);

    /**
     * 更新消息场景
     *
     * @param scene 消息场景DTO
     */
    @Update("update ims_scene set `code` = #{code}, type = #{type}, `name` = #{name}, title = #{title}, tag = #{tag}, " +
            "`param` = #{param, typeHandler = com.insight.utils.pojo.base.ArrayTypeHandler}, remark = #{remark} where id = #{id};")
    void editScene(Scene scene);

    /**
     * 删除消息场景
     *
     * @param id 消息场景ID
     */
    @Delete("delete s, c from ims_scene s left join ims_scene_config c on c.scene_id = s.id where s.id = #{id};")
    void deleteScene(Long id);

    /**
     * 获取场景配置列表
     *
     * @param tenantId 租户ID
     * @param sceneId  场景ID
     * @return 场景模板配置列表
     */
    @Select("select * from ims_scene_config where scene_id = #{sceneId} and (tenant_id is null or tenant_id = #{tenantId}) order by created_time;")
    List<SceneConfigDto> getSceneConfigs(@Param("tenantId") Long tenantId, @Param("sceneId") Long sceneId);

    /**
     * 获取场景配置详情
     *
     * @param id 配置ID
     * @return 配置详情
     */
    @Select("select * from ims_scene_config where id = #{id};")
    SceneConfig getSceneConfig(Long id);

    /**
     * 获取已存在配置数量
     *
     * @param id       场景ID
     * @param tenantId 租户ID
     * @param appId    应用ID
     * @return 消息模板
     */
    @Select("<script>select count(*) from ims_scene_config where scene_id = #{id} " +
            "<if test = 'tenantId != null'>and tenant_id = #{tenantId} </if>" +
            "<if test = 'tenantId == null'>and tenant_id is null </if>" +
            "<if test = 'appId != null'>and app_id = #{appId} </if>" +
            "<if test = 'appId == null'>and app_id is null </if>" +
            ";</script>")
    int getConfigCount(Long id, Long tenantId, Long appId);

    /**
     * 新增场景配置
     *
     * @param config 场景配置DTO
     */
    @Insert("insert ims_scene_config(id, tenant_id, scene_id, app_id, app_name, content, sign, expire, creator, creator_id, created_time) VALUES " +
            "(#{id}, #{tenantId}, #{sceneId}, #{appId}, #{appName}, #{content}, #{sign}, #{expire}, #{creator}, #{creatorId}, now());")
    void addSceneConfig(SceneConfig config);

    /**
     * 编辑场景配置
     *
     * @param config 场景配置DTO
     */
    @Update("update ims_scene_config set app_id = #{appId}, app_name = #{appName}, content = #{content}, sign = #{sign}, expire = #{expire} where id = #{id};")
    void updateSceneConfig(SceneConfig config);

    /**
     * 删除场景配置
     *
     * @param id 场景配置ID
     */
    @Delete("delete from ims_scene_config where id = #{id};")
    void deleteSceneConfig(Long id);
}
