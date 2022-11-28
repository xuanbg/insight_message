package com.insight.common.message.common.mapper;

import com.insight.common.message.common.dto.*;
import com.insight.common.message.common.entity.PushMessage;
import com.insight.common.message.common.entity.SubscribeMessage;
import com.insight.utils.common.JsonTypeHandler;
import com.insight.utils.pojo.base.Search;
import com.insight.utils.pojo.message.InsightMessage;
import com.insight.utils.pojo.message.Schedule;
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
     * 获取适用消息模板
     *
     * @param tenantId 租户ID
     * @param appId    应用ID
     * @param code     场景编号
     * @return 消息模板
     */
    @Select("select s.type, s.title, s.tag, c.content, c.sign, c.expire from ims_scene s join ims_scene_config c on c.scene_id = s.id " +
            "and (c.tenant_id is null or c.tenant_id = #{tenantId}) and (c.app_id is null or c.app_id = #{appId}) " +
            "where s.code = #{code} order by c.tenant_id desc, c.app_id desc limit 1;")
    TemplateDto getTemplate(Long tenantId, Long appId, String code);

    /**
     * 获取消息列表
     *
     * @param search 查询实体类
     * @return 消息列表
     */
    @Select("<script>select m.id, m.tag, m.title, case when r.message_id is null then 0 else r.is_read end as is_read, m.creator, m.created_time from imm_message m " +
            "left join (select message_id, is_read, is_invalid from imm_message_push where user_id = #{ownerId} union all " +
            "select message_id, 1 as is_read, is_invalid from imm_message_subscribe where user_id = #{ownerId}) r on r.message_id = m.id " +
            "where m.app_id = #{appId} and date_add(m.created_time, interval m.expire minute) > now() and (r.is_invalid = 0 or r.is_invalid is null) " +
            "<if test = 'tenantId != null'>and m.tenant_id = #{tenantId} </if>" +
            "<if test = 'tenantId == null'>and m.tenant_id is null </if>" +
            "<if test = 'keyword != null'>and (m.tag = #{keyword} or m.title like concat('%',#{keyword},'%')) </if></script>")
    List<MessageListDto> getMessages(Search search);

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
    UserMessageDto getMessage(@Param("messageId") Long messageId, @Param("userId") Long userId);

    /**
     * 新增消息
     *
     * @param message 消息DTO
     */
    @Insert("insert imm_message(id, tenant_id, app_id, tag, title, content, expire, is_broadcast_id, creator, creator_id, created_time) values " +
            "(#{id}, #{tenantId}, #{appId}, #{tag}, #{title}, #{content}, #{expire}, #{isBroadcast}, #{creator}, #{creatorId}, #{createdTime});")
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
    void readMessage(@Param("messageId") Long messageId, @Param("userId") Long userId);

    /**
     * 订阅消息
     *
     * @param subscribeMessage 消息订阅DTO
     */
    @Insert("insert imm_message_subscribe(message_id, user_id, created_time) values (#{messageId}, #{userId}, #{createdTime});")
    void subscribeMessage(SubscribeMessage subscribeMessage);

    /**
     * 删除用户消息
     *
     * @param messageId 消息ID
     * @param userId    用户ID
     */
    @Update("update imm_message_push set is_invalid = 1 where message_id = #{messageId} and user_id = #{userId};")
    void deleteUserMessage(@Param("messageId") Long messageId, @Param("userId") Long userId);

    /**
     * 删除用户消息
     *
     * @param messageId 消息ID
     * @param userId    用户ID
     */
    @Update("update imm_message_subscribe set is_invalid = 1 where message_id = #{messageId} and user_id = #{userId};")
    void unsubscribeMessage(@Param("messageId") Long messageId, @Param("userId") Long userId);

    /**
     * 编辑消息
     *
     * @param message 消息DTO
     */
    @Update("update imm_message set app_id = #{appId}, tag = #{tag}, type = #{type}, receivers = #{receivers, typeHandler = com.insight.utils.common.ArrayTypeHandler}, " +
            "content = #{content}, expire = #{expire}, is_broadcast = #{isBroadcast} where id = #{id};")
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
     * @param search 查询DTO
     * @return 任务列表
     */
    @Select("<script>select id, type, method, task_time, count, is_invalid, created_time from imt_schedule " +
            "<if test = 'keyword != null'>where type = #{keyword} or method = #{keyword} </if></script>")
    List<ScheduleListDto> getSchedules(Search search);

    /**
     * 获取任务详情
     *
     * @param id 计划任务ID
     * @return 计划任务DTO
     */
    @Results({@Result(property = "content", column = "content", javaType = Object.class, typeHandler = JsonTypeHandler.class)})
    @Select("select * from imt_schedule where id = #{id};")
    Schedule getSchedule(Long id);

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
            "(#{id}, #{type}, #{method}, #{taskTime}, #{content, typeHandler = com.insight.utils.common.JsonTypeHandler}, #{count}, #{isInvalid}, #{createdTime});")
    void addSchedule(Schedule schedule);

    /**
     * 更新任务执行时间为当前时间
     *
     * @param id 计划任务ID
     */
    @Update("update imt_schedule set task_time = now(), is_invalid = 0 where id = #{id};")
    void editSchedule(Long id);

    /**
     * 禁用/启用计划任务
     *
     * @param id     计划任务ID
     * @param status 禁用/启用状态
     */
    @Update("update imt_schedule set is_invalid = #{status} where id = #{id};")
    void changeScheduleStatus(Long id, boolean status);

    /**
     * 删除计划任务
     *
     * @param id 计划任务ID
     */
    @Delete("delete from imt_schedule where id = #{id};")
    void deleteSchedule(Long id);
}
