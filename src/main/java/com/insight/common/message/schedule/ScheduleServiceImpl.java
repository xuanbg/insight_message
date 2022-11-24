package com.insight.common.message.schedule;

import com.github.pagehelper.PageHelper;
import com.insight.common.message.common.client.LogClient;
import com.insight.common.message.common.client.LogServiceClient;
import com.insight.common.message.common.dto.Schedule;
import com.insight.common.message.common.dto.ScheduleCall;
import com.insight.common.message.common.entity.InsightMessage;
import com.insight.common.message.common.mapper.MessageMapper;
import com.insight.utils.Json;
import com.insight.utils.ReplyHelper;
import com.insight.utils.SnowflakeCreator;
import com.insight.utils.pojo.OperateType;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 计划任务服务
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {
    private static final String BUSINESS = "计划任务管理";
    private static final List<String> MATCH_LIST = new ArrayList<>();

    static {
        MATCH_LIST.add("addMessage");
        MATCH_LIST.add("pushNotice");
        MATCH_LIST.add("sendSms");
        MATCH_LIST.add("sendMail");
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SnowflakeCreator creator;
    private final LogServiceClient client;
    private final MessageMapper mapper;

    /**
     * 构造方法
     *
     * @param creator 雪花算法ID生成器
     * @param client  LogServiceClient
     * @param mapper  MessageMapper
     */
    public ScheduleServiceImpl(SnowflakeCreator creator, LogServiceClient client, MessageMapper mapper) {
        this.creator = creator;
        this.client = client;
        this.mapper = mapper;
    }

    /**
     * 获取计划任务列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    @Override
    public Reply getSchedules(Search search) {
        var page = PageHelper.startPage(search.getPageNum(), search.getPageSize())
                .setOrderBy(search.getOrderBy()).doSelectPage(() -> mapper.getSchedules(search));

        var total = page.getTotal();
        return total > 0 ? ReplyHelper.success(page.getResult(), total) : ReplyHelper.resultIsEmpty();
    }

    /**
     * 获取计划任务详情
     *
     * @param id 计划任务ID
     * @return Reply
     */
    @Override
    public Reply getSchedule(Long id) {
        Schedule schedule = mapper.getSchedule(id);
        if (schedule == null) {
            return ReplyHelper.fail("ID不存在,未读取数据");
        }

        return ReplyHelper.success(schedule);
    }

    /**
     * 新增计划任务
     *
     * @param dto 计划任务DTO
     * @return Reply
     */
    @Override
    public Reply newSchedule(Schedule dto) {
        if (dto.getType() > 0) {
            ScheduleCall call = Json.clone(dto.getContent(), ScheduleCall.class);
            if (call == null || call.getMethod() == null || call.getService() == null || call.getUrl() == null) {
                return ReplyHelper.invalidParam();
            }
        } else {
            boolean match = MATCH_LIST.stream().anyMatch(i -> i.equals(dto.getMethod()));
            if (!match) {
                return ReplyHelper.invalidParam("调用方法错误");
            }

            InsightMessage message = Json.clone(dto.getContent(), InsightMessage.class);
            if (message == null) {
                return ReplyHelper.invalidParam();
            }
        }

        Long id = creator.nextId(3);
        dto.setId(id);
        if (dto.getTaskTime() == null) {
            dto.setTaskTime(LocalDateTime.now().plusSeconds(10));
        }

        dto.setCount(0);
        dto.setInvalid(false);
        dto.setCreatedTime(LocalDateTime.now());

        mapper.addSchedule(dto);
        return ReplyHelper.created(id);
    }

    /**
     * 立即执行计划任务
     *
     * @param info 用户关键信息
     * @param id   计划任务ID
     * @return Reply
     */
    @Override
    public Reply executeSchedule(LoginInfo info, Long id) {
        Schedule schedule = mapper.getSchedule(id);
        if (schedule == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        mapper.editSchedule(id);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, schedule);

        return ReplyHelper.success();
    }

    /**
     * 删除计划任务
     *
     * @param info 用户关键信息
     * @param id   计划任务ID
     * @return Reply
     */
    @Override
    public Reply deleteSchedule(LoginInfo info, Long id) {
        Schedule schedule = mapper.getSchedule(id);
        if (schedule == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        mapper.deleteSchedule(id);
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, schedule);

        return ReplyHelper.success();
    }

    /**
     * 禁用/启用计划任务
     *
     * @param info   用户关键信息
     * @param id     计划任务ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    @Override
    public Reply changeScheduleStatus(LoginInfo info, Long id, boolean status) {
        Schedule schedule = mapper.getSchedule(id);
        if (schedule == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        mapper.changeScheduleStatus(id, status);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, schedule);

        return ReplyHelper.success();
    }

    /**
     * 获取日志列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    @Override
    public Reply getScheduleLogs(Search search) {
        return client.getLogs(BUSINESS, search.getKeyword(), search.getPageNum(), search.getPageSize());
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @Override
    public Reply getScheduleLog(Long id) {
        return client.getLog(id);
    }
}
