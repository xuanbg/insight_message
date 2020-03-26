package com.insight.common.message.schedule;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insight.common.message.common.MessageDal;
import com.insight.common.message.common.dto.ScheduleListDto;
import com.insight.common.message.common.entity.InsightMessage;
import com.insight.common.message.common.mapper.MessageMapper;
import com.insight.util.Generator;
import com.insight.util.Json;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.*;
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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MessageDal dal;
    private final MessageMapper mapper;
    private static final List<String> MATCH_LIST = new ArrayList<>();

    static {
        MATCH_LIST.add("addMessage");
        MATCH_LIST.add("pushNotice");
        MATCH_LIST.add("sendSms");
        MATCH_LIST.add("sendMail");
    }

    /**
     * 构造方法
     *
     * @param dal    MessageDal
     * @param mapper MessageMapper
     */
    public ScheduleServiceImpl(MessageDal dal, MessageMapper mapper) {
        this.dal = dal;
        this.mapper = mapper;
    }

    /**
     * 获取计划任务列表
     *
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @Override
    public Reply getSchedules(String keyword, int page, int size) {
        PageHelper.startPage(page, size);
        List<ScheduleListDto> schedules = mapper.getSchedules(keyword);
        PageInfo<ScheduleListDto> pageInfo = new PageInfo<>(schedules);

        return ReplyHelper.success(schedules, pageInfo.getTotal());
    }

    /**
     * 获取计划任务详情
     *
     * @param id 计划任务ID
     * @return Reply
     */
    @Override
    public Reply getSchedule(String id) {
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
            if (message == null){
                return ReplyHelper.invalidParam();
            }
        }

        String id = Generator.uuid();
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
    public Reply executeSchedule(LoginInfo info, String id) {
        Schedule schedule = mapper.getSchedule(id);
        if (schedule == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        mapper.editSchedule(id);
        dal.writeLog(info, OperateType.UPDATE, "计划任务管理", id, schedule);

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
    public Reply deleteSchedule(LoginInfo info, String id) {
        Schedule schedule = mapper.getSchedule(id);
        if (schedule == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        mapper.deleteSchedule(id);
        dal.writeLog(info, OperateType.DELETE, "计划任务管理", id, schedule);

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
    public Reply changeScheduleStatus(LoginInfo info, String id, boolean status) {
        Schedule schedule = mapper.getSchedule(id);
        if (schedule == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        mapper.changeScheduleStatus(id, status);
        dal.writeLog(info, OperateType.UPDATE, "计划任务管理", id, schedule);

        return ReplyHelper.success();
    }

    /**
     * 获取日志列表
     *
     * @param tenantId 租户ID
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    @Override
    public Reply getScheduleLogs(String tenantId, String keyword, int page, int size) {
        PageHelper.startPage(page, size);
        List<Log> logs = dal.getLogs(tenantId, "计划任务管理", keyword);
        PageInfo<Log> pageInfo = new PageInfo<>(logs);

        return ReplyHelper.success(logs, pageInfo.getTotal());
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @Override
    public Reply getScheduleLog(String id) {
        Log log = dal.getLog(id);
        if (log == null) {
            return ReplyHelper.fail("ID不存在,未读取数据");
        }

        return ReplyHelper.success(log);
    }
}
