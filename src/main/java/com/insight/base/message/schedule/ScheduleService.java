package com.insight.base.message.schedule;

import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.Reply;
import com.insight.util.pojo.Schedule;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 计划任务服务接口
 */
public interface ScheduleService {

    /**
     * 获取计划任务列表
     *
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    Reply getSchedules(String keyword, int page, int size);

    /**
     * 获取计划任务详情
     *
     * @param id 计划任务ID
     * @return Reply
     */
    Reply getSchedule(String id);

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
    Reply executeSchedule(LoginInfo info, String id);

    /**
     * 删除计划任务
     *
     * @param info 用户关键信息
     * @param id   计划任务ID
     * @return Reply
     */
    Reply deleteSchedule(LoginInfo info, String id);

    /**
     * 禁用/启用计划任务
     *
     * @param info   用户关键信息
     * @param id     计划任务ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    Reply changeScheduleStatus(LoginInfo info, String id, boolean status);
}
