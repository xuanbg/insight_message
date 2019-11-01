package com.insight.base.message.common.mapper;

import com.insight.base.message.common.dto.SceneListDto;
import com.insight.base.message.common.dto.SceneTemplateListDto;
import com.insight.base.message.common.entity.Scene;
import com.insight.base.message.common.entity.SceneTemplate;
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
     * @param key      查询关键词
     * @return 消息场景列表
     */
    @Select("<script>select id, code, name, remark, creator from ims_scene " +
            "<if test = 'key != null'>where " +
            "code = #{key} or name like concat('%',#{key},'%')</if> " +
            "order by created_time desc</script>")
    List<SceneListDto> getScenes(@Param("key") String key);

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
     * @param code 消息场景编码
     * @return 场景数量
     */
    @Select("select count(*) from ims_scene where code = #{code};")
    int getSceneCount(String code);

    /**
     * 新增消息场景
     *
     * @param scene 消息场景DTO
     */
    @Insert("insert ims_scene(id, code, name, remark, creator, creator_id, created_time) values " +
            "(#{id}, #{code}, #{name}, #{remark}, #{creator}, #{creatorId}, #{createdTime});")
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
     * 获取场景模板配置列表
     *
     * @param tenantId 租户ID
     * @param sceneId  场景ID
     * @param key      查询关键词
     * @return 场景模板配置列表
     */
    @Select("<script>select c.id, s.id as scene_id, concat(s.code, '-', s.name) as scene, c.app_id, c.app_name, c.partner_code, c.partner, " +
            "t.id as template_id, concat(t.code, '-', t.title) as template, c.sign from ims_scene_template c join ims_scene s on s.id = c.scene_id " +
            "join ims_template t on t.id = c.template_id " +
            "<if test = 'tenantId != null'>and tenant_id = #{tenantId} </if>" +
            "<if test = 'tenantId == null'>and tenant_id is null </if>" +
            "where c.scene_id = #{sceneId} " +
            "<if test = 'key != null'>and (s.code = #{key} or s.name like concat('%',#{key},'%') or c.app_name like concat('%',#{key},'%') " +
            "or c.partner_code = #{key} or c.partner like concat('%',#{key},'%')) </if> " +
            "order by c.created_time desc</script>")
    List<SceneTemplateListDto> getSceneTemplates(@Param("tenantId") String tenantId, @Param("sceneId") String sceneId, @Param("key") String key);

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
    SceneTemplate getSceneTemplate(String id);

    /**
     * 新增分渠道模板配置
     *
     * @param config 渠道模板配置DTO
     */
    @Insert("INSERT ims_scene_template(id, scene_id, app_id, app_name, partner_code, partner, template_id, sign, dept_id, creator, creator_id, created_time) VALUES " +
            "(#{id}, #{sceneId}, #{appId}, #{appName}, #{partnerCode}, #{partner}, #{templateId}, #{sign}, #{deptId}, #{creator}, #{creatorId}, #{createdTime});")
    void addSceneTemplate(SceneTemplate config);

    /**
     * 删除分渠道模板配置
     *
     * @param id 消息场景ID
     */
    @Delete("delete from ims_scene_template where id = #{id};")
    void deleteSceneTemplate(String id);
}
