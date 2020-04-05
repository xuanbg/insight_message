package com.insight.common.message.common.mapper;

import com.insight.common.message.common.dto.*;
import com.insight.common.message.common.entity.InsightMessage;
import com.insight.common.message.common.entity.PushMessage;
import com.insight.common.message.common.entity.SubscribeMessage;
import com.insight.util.common.JsonTypeHandler;
import com.insight.util.pojo.Log;
import com.insight.util.pojo.LoginInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019/9/21
 * @remark 消息DAL
 */
@Mapper
public interface MessageMapper {

    /**
     * 获取指定租户下指定编码的模板数量
     *
     * @param tenantId 租户ID
     * @param code     模板编码
     * @return 模板数量
     */
    @Select("<script>select count(*) from ims_template where code = #{code} and " +
            "<if test = 'tenantId != null'>tenant_id = #{tenantId}</if>" +
            "<if test = 'tenantId == null'>tenant_id is null</if>;</script>")
    int getTemplateCount(@Param("tenantId") String tenantId, @Param("code") String code);

    /**
     * 获取适用消息模板
     *
     * @param info 用户关键信息
     * @param dto  标准信息DTO
     * @return 消息模板
     */
    @Select("select t.tag, t.type, t.title, t.content, t.expire, c.sign from ims_scene_template c " +
            "join ims_template t on t.id = c.template_id and (t.tenant_id is null or t.tenant_id = #{info.tenantId}) " +
            "join ims_scene s on s.id = c.scene_id and s.code = #{dto.sceneCode} " +
            "where (c.app_id is null or c.app_id = #{info.appId}) and (c.partner_code is null or c.partner_code = #{dto.partnerCode}) " +
            "order by t.tenant_id desc, c.app_id desc, c.partner_code desc limit 1;")
    TemplateDto getTemplate(@Param("info") LoginInfo info, @Param("dto") NormalMessage dto);

    /**
     * 获取消息列表
     *
     * @param info 用户关键信息
     * @param key  查询关键词
     * @return 消息列表
     */
    @Select("<script>select m.id, m.tag, m.title, case when r.message_id is null then 0 else 1 end as is_read, m.creator, m.created_time from imm_message m " +
            "left join (select message_id, is_read, is_invalid from imm_message_push where user_id = #{info.userId} union all " +
            "select message_id, 1 as is_read, is_invalid from imm_message_subscribe where user_id = #{info.userId}) r on r.message_id = m.id " +
            "where m.app_id = #{info.appId} and m.expire_date > now() and (r.is_invalid = 0 or r.is_invalid is null) " +
            "<if test = 'info.tenantId != null'>and m.tenant_id = #{info.tenantId} </if>" +
            "<if test = 'info.tenantId == null'>and m.tenant_id is null </if>" +
            "<if test = 'key != null'>and (m.tag = #{key} or m.title like concat('%',#{key},'%')) </if>" +
            "order by m.created_time desc</script>")
    List<MessageListDto> getMessages(@Param("info") LoginInfo info, @Param("key") String key);

    /**
     * 获取消息详情
     *
     * @param messageId 消息ID
     * @param userId    用户ID
     * @return 消息详情
     */
    @Select("select m.id, m.tag, m.title, m.content, case when r.message_id is null then 0 else 1 end as is_read, m.is_broadcast, m.creator, m.creator_id, m.created_time " +
            "from imm_message m left join (select message_id, is_read from imm_message_push where message_id = #{messageId} and user_id = #{userId} union all " +
            "select message_id, 1 as is_read from imm_message_subscribe where message_id = #{messageId} and user_id = #{userId}) r on r.message_id = m.id " +
            "where m.id = #{messageId};")
    UserMessageDto getMessage(@Param("messageId") String messageId, @Param("userId") String userId);

    /**
     * 新增消息
     *
     * @param message 消息DTO
     */
    @Insert("insert imm_message(id, tenant_id, app_id, tag, title, content, expire_date, is_broadcast_id, creator, creator_id, created_time) values " +
            "(#{id}, #{tenantId}, #{appId}, #{tag}, #{title}, #{content}, #{expireDate}, #{isBroadcast}, #{creator}, #{creatorId}, #{createdTime});")
    void addMessage(InsightMessage message);

    /**
     * 推送消息
     *
     * @param list 消息推送DTO集合
     */
    @Insert("<script>insert imm_message_push(id, message_id, user_id) values " +
            "<foreach collection = \"list\" item = \"item\" index = \"index\" separator = \",\">" +
            "(#{item.id},#{item.messageId},#{item.userId})</foreach>;</script>")
    void pushMessage(List<PushMessage> list);

    /**
     * 设置用户消息为已读
     *
     * @param messageId 消息ID
     * @param userId    用户ID
     */
    @Update("update imm_message_push set is_read = 1, read_time = now() where message_id = #{messageId} and user_id = #{userId};")
    void readMessage(@Param("messageId") String messageId, @Param("userId") String userId);

    /**
     * 订阅消息
     *
     * @param subscribeMessage 消息订阅DTO
     */
    @Insert("insert imm_message_subscribe(id, message_id, user_id, created_time) values (#{id}, #{messageId}, #{userId}, #{createdTime});")
    void subscribeMessage(SubscribeMessage subscribeMessage);

    /**
     * 删除用户消息
     *
     * @param messageId 消息ID
     * @param userId    用户ID
     */
    @Update("update imm_message_push set is_invalid = 1 where message_id = #{messageId} and user_id = #{userId};")
    void deleteUserMessage(@Param("messageId") String messageId, @Param("userId") String userId);

    /**
     * 删除用户消息
     *
     * @param messageId 消息ID
     * @param userId    用户ID
     */
    @Update("update imm_message_subscribe set is_invalid = 1 where message_id = #{messageId} and user_id = #{userId};")
    void unsubscribeMessage(@Param("messageId") String messageId, @Param("userId") String userId);

    /**
     * 编辑消息
     *
     * @param message 消息DTO
     */
    @Update("update imm_message set app_id = #{appId}, tag = #{tag}, type = #{type}, receivers = #{receivers, typeHandler = com.insight.util.common.ArrayTypeHandler}, " +
            "content = #{content}, expire_date = #{expireDate}, is_broadcast = #{isBroadcast} where id = #{id};")
    void editMessage(InsightMessage message);

    /**
     * 删除消息
     *
     * @param id 消息ID
     */
    @Delete("delete from imm_message where id = #{id};")
    void deleteMessage(String id);

    /**
     * 取消推送
     *
     * @param id 推送ID
     */
    @Delete("delete from imm_message_push where id = #{id};")
    void cancelPush(String id);

    /**
     * 获取任务列表
     *
     * @param key 查询关键词
     * @return 任务列表
     */
    @Select("<script>select id, type, method, task_time, count, is_invalid, created_time from imt_schedule " +
            "<if test = 'key != null'>where type = #{key} or method = #{key} </if>" +
            "order by task_time</script>")
    List<ScheduleListDto> getSchedules(@Param("key") String key);

    /**
     * 获取任务详情
     *
     * @param id 计划任务ID
     * @return 计划任务DTO
     */
    @Results({@Result(property = "content", column = "content", javaType = Object.class, typeHandler = JsonTypeHandler.class)})
    @Select("select * from imt_schedule where id = #{id};")
    Schedule getSchedule(String id);

    /**
     * 获取当前需要执行的消息类型的计划任务
     *
     * @return DTO集合
     */
    @Results({@Result(property = "content", column = "content", javaType = InsightMessage.class, typeHandler = JsonTypeHandler.class)})
    @Select("select * from imt_schedule where type = 0 and task_time < now() and is_invalid = 0;")
    List<Schedule<InsightMessage>> getMessageSchedule();

    /**
     * 获取当前需要执行的本地调用类型的计划任务
     *
     * @return 计划任务DTO集合
     */
    @Results({@Result(property = "content", column = "content", javaType = ScheduleCall.class, typeHandler = JsonTypeHandler.class)})
    @Select("select * from imt_schedule where type = 1 and task_time < now() and is_invalid = 0;")
    List<Schedule<ScheduleCall>> getLocalSchedule();

    /**
     * 获取当前需要执行的远程调用类型的计划任务
     *
     * @return 计划任务DTO集合
     */
    @Results({@Result(property = "content", column = "content", javaType = ScheduleCall.class, typeHandler = JsonTypeHandler.class)})
    @Select("select * from imt_schedule where type = 2 and task_time < now() and is_invalid = 0;")
    List<Schedule<ScheduleCall>> getRpcSchedule();

    /**
     * 新增计划任务记录
     *
     * @param schedule 计划任务DTO
     */
    @Insert("insert imt_schedule (id, type, method, task_time, content, count, is_invalid, created_time) values " +
            "(#{id}, #{type}, #{method}, #{taskTime}, #{content, typeHandler = com.insight.util.common.JsonTypeHandler}, #{count}, #{isInvalid}, #{createdTime});")
    void addSchedule(Schedule schedule);

    /**
     * 更新任务执行时间为当前时间
     *
     * @param id 计划任务ID
     */
    @Update("update imt_schedule set task_time = now(), is_invalid = 0 where id = #{id};")
    void editSchedule(String id);

    /**
     * 禁用/启用计划任务
     *
     * @param id     计划任务ID
     * @param status 禁用/启用状态
     */
    @Update("update imt_schedule set is_invalid = #{status} where id = #{id};")
    void changeScheduleStatus(String id, boolean status);

    /**
     * 删除计划任务
     *
     * @param id 计划任务ID
     */
    @Delete("delete from imt_schedule where id = #{id};")
    void deleteSchedule(String id);

    /**
     * 记录操作日志
     *
     * @param log 日志DTO
     */
    @Insert("insert iml_operate_log(id, tenant_id, type, business, business_id, content_id, creator, creator_id, created_time) values " +
            "(#{id}, #{tenantId}, #{type}, #{business}, #{businessId}, #{content, typeHandler = com.insight.util.common.JsonTypeHandler}, " +
            "#{creator}, #{creatorId}, #{createdTime});")
    void addLog(Log log);

    /**
     * 获取操作日志列表
     *
     * @param tenantId 租户ID
     * @param business 业务类型
     * @param key      查询关键词
     * @return 操作日志列表
     */
    @Select("<script>select id, type, business, business_id_id, creator, creator_id, created_time from iml_operate_log where business = #{business} " +
            "<if test = 'tenantId != null'>and tenant_id = #{tenantId} </if>" +
            "<if test = 'tenantId == null'>and tenant_id is null </if>" +
            "<if test = 'key!=null'>and (type = #{key} or business_id = #{key} or creator = #{key} or creator_id = #{key}) </if>" +
            "order by created_time</script>")
    List<Log> getLogs(@Param("tenantId") String tenantId, @Param("business") String business, @Param("key") String key);

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
