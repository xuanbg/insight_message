package com.insight.base.message.schedule;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insight.base.message.common.MessageDal;
import com.insight.base.message.common.dto.ScheduleListDto;
import com.insight.base.message.common.mapper.MessageMapper;
import com.insight.util.Generator;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.OperateType;
import com.insight.util.pojo.Reply;
import com.insight.util.pojo.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        String id = Generator.uuid();
        dto.setId(id);
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
}
