package com.insight.base.message.common.mapper;

import com.insight.base.message.common.dto.SceneTemplateListDto;
import com.insight.base.message.common.dto.SceneListDto;
import com.insight.base.message.common.dto.TemplateListDto;
import com.insight.base.message.common.entity.SceneTemplate;
import com.insight.base.message.common.entity.Scene;
import com.insight.base.message.common.entity.Template;
import com.insight.util.common.JsonTypeHandler;
import com.insight.util.pojo.Log;
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
    @Select("<script>select id, type, code, title, creator, created_time from ims_template " +
            "where tenant_id = #{tenantId} " +
            "<if test = 'key != null'>and code = #{key} or title like concat('%',#{key},'%')</if>" +
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
    @Insert("insert ims_template(id, tenant_id, app_id, type, code, title, content, expire, params, remark, dept_id, creator, creator_id, created_time) values " +
            "(#{id}, #{tenantId}, #{appId}, #{type}, #{code}, #{title}, #{content}, #{expire}, #{params, typeHandler = com.insight.util.common.JsonTypeHandler}, " +
            "#{remark}, #{deptId}, #{creator}, #{creatorId}, #{createdTime});")
    void addTemplate(Template template);

    /**
     * 更新消息模板
     *
     * @param template 消息模板DTO
     */
    @Update("update ims_template set app_id = #{appId}, type = #{type}, code = #{code}, title = #{title}, content = #{content}, expire = #{expire}, " +
            "params = #{params, typeHandler = com.insight.util.common.JsonTypeHandler}, remark = #{remark} where id = #{id};")
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
     * 获取操作日志列表
     *
     * @param tenantId 租户ID
     * @param key      查询关键词
     * @return 操作日志列表
     */
    @Select("<script>select id, type, business, business_id, dept_id, creator, creator_id, created_time from iml_operate_log " +
            "where tenant_id = #{tenantId} <if test = 'key!=null'>and type = #{key} or business = #{key} or business_id = #{key} or " +
            "dept_id = #{key} or creator = #{key} or creator_id = #{key}</if>" +
            "order by created_time</script>")
    List<Log> getLogs(@Param("tenantId") String tenantId, @Param("key") String key);

    /**
     * 获取操作日志列表
     *
     * @param id 日志ID
     * @return 操作日志列表
     */
    @Results({@Result(property = "content", column = "content", javaType = Object.class, typeHandler = JsonTypeHandler.class)})
    @Select("select * from iml_operate_log where id = #{id};")
    Log getLog(String id);

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
     * 新增消息场景
     *
     * @param scene 消息场景DTO
     */
    @Insert("insert ims_scene(id, code, name, remark, dept_id, creator, creator_id, created_time) values " +
            "(#{id}, #{code}, #{name}, #{remark}, #{deptId}, #{creator}, #{creatorId}, #{createdTime});")
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
            "c.app_id, c.app_name, c.code, c.channel, c.sign from ims_scene_template c join ims_scene s on s.id = c.scene_id " +
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
