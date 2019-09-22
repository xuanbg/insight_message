package com.insight.base.message.common.mapper;

import com.insight.base.message.common.dto.TemplateListDto;
import com.insight.base.message.common.entity.Template;
import com.insight.util.common.JsonTypeHandler;
import com.insight.util.pojo.Log;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author 宣炳刚
 * @date 2019/9/17
 * @remark 模板DAL
 */
@Mapper
public interface TemplateMapper{

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
     * 记录操作日志
     *
     * @param log 日志DTO
     */
    @Insert("insert iml_operate_log(id, tenant_id, type, business, business_id, content, dept_id, creator, creator_id, created_time) values " +
            "(#{id}, #{tenantId}, #{type}, #{business}, #{businessId}, #{content, typeHandler = com.insight.util.common.JsonTypeHandler}, " +
            "#{deptId}, #{creator}, #{creatorId}, #{createdTime});")
    void addLog(Log log);

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
}
