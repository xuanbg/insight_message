package com.insight.base.message.common.mapper;

import com.insight.base.message.common.dto.SceneListDto;
import com.insight.base.message.common.dto.SceneTemplateListDto;
import com.insight.base.message.common.dto.TemplateListDto;
import com.insight.base.message.common.entity.Scene;
import com.insight.base.message.common.entity.SceneTemplate;
import com.insight.base.message.common.entity.Template;
import com.insight.util.common.JsonTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author 宣炳刚
 * @date 2019/9/17
 * @remark 场景DAL
 */
@Mapper
public interface SceneMapper {

    /**
     * 获取消息模板列表
     *
     * @param tenantId 租户ID
     * @param key      查询关键词
     * @return 消息模板列表
     */
    @Select("<script>select id, type, code, title, creator, created_time from ims_template where " +
            "<if test = 'tenantId != null'>tenant_id = #{tenantId} </if>" +
            "<if test = 'tenantId == null'>tenant_id is null </if>" +
            "<if test = 'key != null'>and (code = #{key} or title like concat('%',#{key},'%'))</if>" +
            "order by created_time desc</script>")
    List<TemplateListDto> getTemplates(@Param("tenantId") String tenantId, @Param("key") String key);

    /**
     * 获取消息模板详情
     *
     * @param id 消息模板ID
     * @return 消息模板详情
     */
    @Results({@Result(property = "params", column = "params", javaType = Map.class, typeHandler = JsonTypeHandler.class)})
    @Select("select * from ims_template where id = #{id};")
    Template getTemplate(String id);

    /**
     * 新增消息模板
     *
     * @param template 消息模板DTO
     */
    @Insert("insert ims_template(id, tenant_id, code, tag, type, title, content, expire, remark, dept_id, creator, creator_id, created_time) values " +
            "(#{id}, #{tenantId}, #{code}, #{tag}, #{type}, #{title}, #{content}, #{expire}, #{remark}, #{deptId}, #{creator}, #{creatorId}, #{createdTime});")
    void addTemplate(Template template);

    /**
     * 更新消息模板
     *
     * @param template 消息模板DTO
     */
    @Update("update ims_template set tag = #{tag}, type = #{type}, title = #{title}, content = #{content}, expire = #{expire}, remark = #{remark} where id = #{id};")
    void editTemplate(Template template);

    /**
     * 删除消息模板
     *
     * @param id 消息模板ID
     */
    @Delete("delete from ims_template where id = #{id};")
    void deleteTemplate(String id);

    /**
     * 禁用/启用消息模板
     *
     * @param id     消息模板ID
     * @param status 禁用/启用状态
     */
    @Update("update ims_template set is_invalid = #{status} where id = #{id};")
    void changeTemplateStatus(@Param("id") String id, @Param("status") boolean status);

    /**
     * 获取消息场景列表
     *
     * @param tenantId 租户ID
     * @param key      查询关键词
     * @return 消息场景列表
     */
    @Select("<script>select id, code, name, remark, creator from ims_scene " +
            "<if test = 'key != null'>where " +
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
     * @param key      查询关键词
     * @return 场景模板配置列表
     */
    @Select("<script>select c.id, s.id as scene_id, concat(s.code, '-', s.name) as scene, t.id as template_id, concat(t.code, '-', t.title) as template, " +
            "c.app_id, c.app_name, c.code, c.channel, c.sign from ims_scene_template c join ims_scene s on s.id = c.scene_id " +
            "<if test = 'key != null'>and (s.code = %{key} or s.name like concat('%',#{key},'%')) </if>" +
            "join ims_template t on t.id = c.template_id and " +
            "<if test = 'tenantId != null'>tenant_id = #{tenantId} </if>" +
            "<if test = 'tenantId == null'>tenant_id is null </if>" +
            "<if test = 'key != null'>where c.app_name like concat('%',#{key},'%') or c.partner_code = %{key} or c.partner like concat('%',#{key},'%') </if> " +
            "order by c.created_time desc</script>")
    List<SceneTemplateListDto> getSceneTemplates(@Param("tenantId") String tenantId, @Param("key") String key);

    /**
     * 新增分渠道模板配置
     *
     * @param config 渠道模板配置DTO
     */
    @Insert("INSERT ims_scene_template(id, scene_id, app_id, app_name, code, channel, template_id, sign, dept_id, creator, creator_id, created_time) VALUES " +
            "(#{id}, #{sceneId}, #{appId}, #{appName}, #{code}, #{channel}, #{templateId}, #{sign}, #{deptId}, #{creator}, #{creatorId}, #{createdTime});")
    void addSceneTemplate(SceneTemplate config);

    /**
     * 删除分渠道模板配置
     *
     * @param id 消息场景ID
     */
    @Delete("delete from ims_scene_template where id = #{id};")
    void deleteSceneTemplate(String id);
}
