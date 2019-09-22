package com.insight.base.message.common.mapper;

import com.insight.base.message.common.dto.MessageListDto;
import com.insight.base.message.common.dto.TemplateDto;
import com.insight.base.message.common.entity.Message;
import com.insight.base.message.common.entity.PushMessage;
import com.insight.base.message.common.entity.SubscribeMessage;
import com.insight.util.common.ArrayTypeHandler;
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
     * @param tenantId    租户ID
     * @param sceneCode   场景编码
     * @param appId       应用ID
     * @param channelCode 渠道编码
     * @return 消息模板
     */
    @Select("select t.tag, t.type, t.title, t.content, t.expire, c.sign from ims_scene_template c " +
            "join ims_template t on t.id = c.template_id and t.tenant_id = #{tenantId} " +
            "join ims_scene s on s.id=c.scene_id and t.tenant_id = #{tenantId} and s.code = #{sceneCode} " +
            "where c.app_id = #{appId} and (c.channel_code is null or c.channel_code = #{channelCode}) " +
            "order by c.channel_code desc limit 1;")
    TemplateDto getTemplate(@Param("tenantId") String tenantId, @Param("sceneCode") String sceneCode, @Param("appId") String appId, @Param("channelCode") String channelCode);

    /**
     * 获取消息列表
     *
     * @param key 查询关键词
     * @return 消息列表
     */
    @Select("<script>select id, tag, title, expire_date, is_broadcast, creator, created_time from imm_message " +
            "<if test = 'key != null'>where tag = #{key} or title like concat('%',#{key},'%') </if>" +
            "order by created_time desc</script>")
    List<MessageListDto> getMessages(@Param("key") String key);

    /**
     * 获取消息详情
     *
     * @param id 消息ID
     * @return 消息详情
     */
    @Results({@Result(property = "receivers", column = "receivers", javaType = String.class, typeHandler = ArrayTypeHandler.class)})
    @Select("select * from imm_message where id = #{id};")
    List<Message> getMessage(String id);

    /**
     * 新增消息
     *
     * @param message 消息DTO
     */
    @Insert("insert imm_message(id, tenant_id, app_id, tag, type, receivers, title, content, expire_date, is_broadcast, dept_id, creator, creator_id, created_time) values " +
            "(#{id}, #{tenantId}, #{appId}, #{tag}, #{type}, #{receivers, typeHandler = com.insight.util.common.ArrayTypeHandler}, " +
            "#{title}, #{content}, #{expireDate}, #{isBroadcast}, #{deptId}, #{creator}, #{creatorId}, #{createdTime});")
    void addMessage(Message message);

    /**
     * 推送消息
     *
     * @param pushMessage 消息推送DTO
     */
    @Insert("insert imm_message_push(id, message_id, user_id) values (#{id}, #{messageId}, #{userId});")
    void pushMessage(PushMessage pushMessage);

    /**
     * 订阅消息
     *
     * @param subscribeMessage 消息订阅DTO
     */
    @Insert("insert imm_message_subscribe(id, message_id, user_id, created_time) values (#{id}, #{messageId}, #{userId}, #{createdTime});")
    void subscribeMessage(SubscribeMessage subscribeMessage);

    /**
     * 编辑消息
     *
     * @param message 消息DTO
     */
    @Update("update imm_message set app_id = #{appId}, tag = #{tag}, type = #{type}, receivers = #{receivers, typeHandler = com.insight.util.common.ArrayTypeHandler}, " +
            "content = #{content}, expire_date = #{expireDate}, is_broadcast = #{isBroadcast} where id = #{id};")
    void editMessage(Message message);

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
}
