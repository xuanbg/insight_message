package com.insight.common.message.common.client;

import com.insight.utils.Util;
import com.insight.utils.common.ApplicationContextHolder;
import com.insight.utils.pojo.Log;
import com.insight.utils.pojo.LoginInfo;
import com.insight.utils.pojo.OperateType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;

/**
 * @author 宣炳刚
 * @date 2019-09-03
 * @remark RabbitMQ客户端
 */
public class LogClient {
    private static final RabbitTemplate TEMPLATE = ApplicationContextHolder.getContext().getBean(RabbitTemplate.class);

    /**
     * 记录操作日志
     *
     * @param info     用户关键信息
     * @param business 业务类型
     * @param type     操作类型
     * @param id       业务ID
     * @param content  日志内容
     */
    public static void writeLog(LoginInfo info, String business, OperateType type, String id, Object content) {
        Log log = new Log();
        log.setId(Util.uuid());
        log.setTenantId(info.getTenantId());
        log.setType(type);
        log.setBusiness(business);
        log.setBusinessId(id);
        log.setContent(content);
        log.setCreator(info.getUserName());
        log.setCreatorId(info.getUserId());
        log.setCreatedTime(LocalDateTime.now());

        TEMPLATE.convertAndSend("amq.topic", "insight.log", log);
    }
}
