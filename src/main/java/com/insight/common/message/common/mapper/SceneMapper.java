package com.insight.common.message.common.mapper;

import com.insight.common.message.common.dto.SceneDto;
import com.insight.common.message.common.dto.SceneConfigDto;
import com.insight.common.message.common.entity.Scene;
import com.insight.common.message.common.entity.SceneConfig;
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
     * @param key 查询关键词
     * @return 消息场景列表
     */
    @Select("<script>select id, `code`, `name`, title, type, content, remark from ims_scene " +
            "<if test = 'key != null'>where code = #{key} or name like concat('%',#{key},'%')</if> " +
            "order by created_time</script>")
    List<SceneDto> getScenes(@Param("key") String key);

    /**
     * 获取场景详情
     *
     * @param id 消息场景ID
     * @return 消息场景DTO
     */
    @Select("select * from ims_scene where id = #{id};")
    Scene getScene(String id);

    /**
     * 获取指定编码的场景数量
     *
     * @param id   消息场景ID
     * @param code 消息场景编码
     * @return 场景数量
     */
    @Select("select count(*) from ims_scene where id != #{id} and code = #{code};")
    int getSceneCount(@Param("id") String id, @Param("code") String code);

    /**
     * 新增消息场景
     *
     * @param scene 消息场景DTO
     */
    @Insert("insert ims_scene(id, `code`, `name`, title, params, tag, type, content, sign, remark, creator, creator_id, created_time) values " +
            "(#{id}, #{code}, #{name}, #{title}, #{params, typeHandler = com.insight.utils.common.JsonTypeHandler}, " +
            "#{tag}, #{type}, #{content}, #{sign}, #{remark}, #{creator}, #{creatorId}, #{createdTime});")
    void addScene(Scene scene);

    /**
     * 更新消息场景
     *
     * @param scene 消息场景DTO
     */
    @Update("update ims_scene set `code` = #{code}, `name` = #{name}, title = #{title}, params = #{params, typeHandler = com.insight.utils.common.JsonTypeHandler}, " +
            "tag = #{tag}, type = #{type}, content = #{content}, sign = #{sign}, remark = #{remark} where id = #{id};")
    void editScene(Scene scene);

    /**
     * 删除消息场景
     *
     * @param id 消息场景ID
     */
    @Delete("delete from ims_scene where id = #{id};")
    void deleteScene(String id);

    /**
     * 获取场景模板配置列表
     *
     * @param tenantId 租户ID
     * @param sceneId  场景ID
     * @param key      查询关键词
     * @return 场景模板配置列表
     */
    @Select("<script>select id, app_name, concat(partner_code, '-', partner) as partner, type, content, sign, expire " +
            "from ims_scene_config where scene_id = #{sceneId} " +
            "<if test = 'tenantId != null'>and tenant_id = #{tenantId} </if>" +
            "<if test = 'tenantId == null'>and tenant_id is null </if>" +
            "<if test = 'key != null'>and (app_name like concat('%',#{key},'%') or partner_code = #{key} or partner like concat('%',#{key},'%')) </if> " +
            "order by created_time</script>")
    List<SceneConfigDto> getSceneConfigs(@Param("tenantId") String tenantId, @Param("sceneId") String sceneId, @Param("key") String key);

    /**
     * 获取消息场景配置数量
     *
     * @param id 消息场景ID
     * @return 配置数量
     */
    @Select("select count(*) from ims_scene_template where scene_id = #{id};")
    int getConfigCount(String id);

    /**
     * 获取场景模板配置详情
     *
     * @param id 配置ID
     * @return 配置详情
     */
    @Select("select * from ims_scene_template where id = #{id};")
    SceneConfig getSceneConfig(String id);

    /**
     * 新增分渠道模板配置
     *
     * @param config 渠道模板配置DTO
     */
    @Insert("INSERT ims_scene_config(id, tenant_id, scene_id, app_id, app_name, partner_code, partner, type, content, sign, expire, creator, creator_id, created_time) VALUES " +
            "(#{id}, #{tenantId}, #{sceneId}, #{appId}, #{appName}, #{partnerCode}, #{partner}, #{type}, #{content}, #{sign}, #{expire}, #{creator}, #{creatorId}, #{createdTime});")
    void addSceneConfig(SceneConfig config);

    /**
     * 删除分渠道模板配置
     *
     * @param id 消息场景ID
     */
    @Delete("delete from ims_scene_template where id = #{id};")
    void deleteSceneConfig(String id);
}
