package com.insight.base.message.common.mapper;

import com.insight.base.message.common.dto.SceneTemplateListDto;
import com.insight.base.message.common.dto.SceneListDto;
import com.insight.base.message.common.entity.SceneTemplate;
import com.insight.base.message.common.entity.Scene;
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
     * @param tenantId 租户ID
     * @param key      查询关键词
     * @return 消息场景列表
     */
    @Select("<script>select id, code, name, remark, creator from ims_scene " +
            "where tenant_id = #{tenantId} <if test = 'key != null'>and " +
            "code = #{key} or name like concat('%',#{key},'%')</if> " +
            "order by created_time desc</script>")
    List<SceneListDto> getScenes(@Param("tenantId") String tenantId, @Param("key") String key);

    /**
     * 获取场景详情
     *
     * @param id 消息场景ID
     * @return 消息场景DTO
     */
    @Select("select * from ims_scene where id = #{id};")
    Scene getScene(String id);

    /**
     * 新增消息场景
     *
     * @param scene 消息场景DTO
     */
    @Insert("insert ims_scene(id, tenant_id, code, name, remark, dept_id, creator, creator_id, created_time) values " +
            "(#{id}, #{tenantId}, #{code}, #{name}, #{remark}, #{deptId}, #{creator}, #{creatorId}, #{createdTime});")
    void addScene(Scene scene);

    /**
     * 更新消息场景
     *
     * @param scene 消息场景DTO
     */
    @Update("update ims_scene set code = #{code}, name = #{name}, remark = #{remark} where id = #{id};")
    void editScene(Scene scene);

    /**
     * 删除消息场景
     *
     * @param id 消息场景ID
     */
    @Delete("delete from ims_scene where id = #{id};")
    void deleteScene(String id);

    /**
     * 禁用/启用消息场景
     *
     * @param id     消息场景ID
     * @param status 禁用/启用状态
     */
    @Update("update ims_scene set is_invalid = #{status} where id = #{id};")
    void changeSceneStatus(@Param("id") String id, @Param("status") boolean status);

    /**
     * 获取分渠道模板配置列表
     *
     * @param tenantId 租户ID
     * @param key      查询关键词
     * @return 分渠道模配置板列表
     */
    @Select("<script>select c.id, s.id as scene_id, concat(s.code, '-', s.name) as scene, t.id as template_id, concat(t.code, '-', t.title) as template, " +
            "c.app_id, c.app_name, c.code, c.channel, c.sign from ims_scene_template c " +
            "join ims_scene s on s.id = c.scene_id and s.tenant_id = #{tenantId} " +
            "<if test = 'key != null'>and (s.code = %{key} or s.name like concat('%',#{key},'%')) </if>" +
            "join ims_template t on t.id = c.template_id and t.tenant_id = #{tenantId} " +
            "<if test = 'key != null'>and (t.code = %{key} or t.title like concat('%',#{key},'%')) </if>" +
            "<if test = 'key != null'>where c.code = %{key} or c.channel like concat('%',#{key},'%')" +
            "or c.app_name like concat('%',#{key},'%')</if> " +
            "order by c.created_time desc</script>")
    List<SceneTemplateListDto> getSceneTemplates(@Param("tenantId") String tenantId, @Param("key") String key);

    /**
     * 新增分渠道模板配置
     *
     * @param config 渠道模板配置DTO
     */
    @Insert("INSERT ims_scene_template(id, scene_id, template_id, app_id, app_name, code, channel, sign, dept_id, creator, creator_id, created_time) VALUES " +
            "(#{id}, #{sceneId}, #{templateId}, #{appId}, #{appName}, #{code}, #{channel}, #{sign}, #{deptId}, #{creator}, #{creatorId}, #{createdTime});")
    void addSceneTemplate(SceneTemplate config);

    /**
     * 删除分渠道模板配置
     *
     * @param id 消息场景ID
     */
    @Delete("delete from ims_scene_template where id = #{id};")
    void deleteSceneTemplate(String id);
}
