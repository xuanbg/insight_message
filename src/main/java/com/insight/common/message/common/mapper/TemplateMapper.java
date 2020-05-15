package com.insight.common.message.common.mapper;

import com.insight.common.message.common.dto.TemplateListDto;
import com.insight.common.message.common.entity.Template;
import com.insight.utils.common.JsonTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author 宣炳刚
 * @date 2019/9/17
 * @remark 场景DAL
 */
@Mapper
public interface TemplateMapper {

    /**
     * 获取消息模板列表
     *
     * @param tenantId 租户ID
     * @param key      查询关键词
     * @return 消息模板列表
     */
    @Select("<script>select * from ims_template where " +
            "<if test = 'tenantId != null'>tenant_id = #{tenantId} </if>" +
            "<if test = 'tenantId == null'>tenant_id is null </if>" +
            "<if test = 'key != null'>and (code = #{key} or title like concat('%',#{key},'%'))</if>" +
            "order by created_time</script>")
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
    @Insert("insert ims_template(id, tenant_id, code, tag, type, title, content, expire, remark_id, creator, creator_id, created_time) values " +
            "(#{id}, #{tenantId}, #{code}, #{tag}, #{type}, #{title}, #{content}, #{expire}, #{remark}, #{creator}, #{creatorId}, #{createdTime});")
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
     * 获取指定模板的消息场景配置数量
     *
     * @param id 消息模板ID
     * @return 配置数量
     */
    @Select("select count(*) from ims_scene_template where template_id = #{id};")
    int getConfigCount(String id);
}
