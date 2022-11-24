package com.insight.common.message.schedule;

import com.insight.common.message.common.dto.Schedule;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 计划任务服务接口
 */
public interface ScheduleService {

    /**
     * 获取计划任务列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    Reply getSchedules(Search search);

    /**
     * 获取计划任务详情
     *
     * @param id 计划任务ID
     * @return Reply
     */
    Reply getSchedule(Long id);

    /**
     * 新增计划任务
     *
     * @param dto 计划任务DTO
     * @return Reply
     */
    Reply newSchedule(Schedule dto);

    /**
     * 立即执行计划任务
     *
     * @param info 用户关键信息
     * @param id   计划任务ID
     * @return Reply
     */
    Reply executeSchedule(LoginInfo info, Long id);

    /**
     * 删除计划任务
     *
     * @param info 用户关键信息
     * @param id   计划任务ID
     * @return Reply
     */
    Reply deleteSchedule(LoginInfo info, Long id);

    /**
     * 禁用/启用计划任务
     *
     * @param info   用户关键信息
     * @param id     计划任务ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    Reply changeScheduleStatus(LoginInfo info, Long id, boolean status);

    /**
     * 获取日志列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    Reply getScheduleLogs(Search search);

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    Reply getScheduleLog(Long id);
}
