package com.insight.common.message.schedule;

import com.insight.utils.Json;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;
import com.insight.utils.pojo.message.Schedule;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 计划任务服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/common/message")
public class ScheduleController {
    private final ScheduleService service;

    /**
     * 构造方法
     *
     * @param service 注入Service
     */
    public ScheduleController(ScheduleService service) {
        this.service = service;
    }

    /**
     * 获取计划任务列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    @GetMapping("/v1.0/schedules")
    public Reply getSchedules(Search search) {
        return service.getSchedules(search);
    }

    /**
     * 获取计划任务详情
     *
     * @param id 计划任务ID
     * @return Reply
     */
    @GetMapping("/v1.0/schedules/{id}")
    public Schedule getSchedule(@PathVariable Long id) {
        return service.getSchedule(id);
    }

    /**
     * 新增计划任务
     *
     * @param dto 计划任务DTO
     * @return Reply
     */
    @PostMapping("/v1.0/schedules")
    public Long newSchedule(@Valid @RequestBody Schedule dto) {
        return service.newSchedule(dto);
    }

    /**
     * 立即执行计划任务
     *
     * @param info 用户关键信息
     * @param id   计划任务ID
     */
    @PutMapping("/v1.0/schedules/{id}")
    public void executeSchedule(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        service.executeSchedule(loginInfo, id);
    }

    /**
     * 删除计划任务
     *
     * @param info 用户关键信息
     * @param id   计划任务ID
     */
    @DeleteMapping("/v1.0/schedules/{id}")
    public void deleteSchedule(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        service.deleteSchedule(loginInfo, id);
    }

    /**
     * 禁用计划任务
     *
     * @param info 用户关键信息
     * @param id   计划任务ID
     */
    @PutMapping("/v1.0/schedules/{id}/disable")
    public void disableSchedule(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        service.changeScheduleStatus(loginInfo, id, true);
    }

    /**
     * 启用计划任务
     *
     * @param info 用户关键信息
     * @param id   计划任务ID
     */
    @PutMapping("/v1.0/schedules/{id}/enable")
    public void enableSchedule(@RequestHeader("loginInfo") String info, @PathVariable Long id) {
        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);

        service.changeScheduleStatus(loginInfo, id, false);
    }

    /**
     * 获取日志列表
     *
     * @param search 查询实体类
     * @return Reply
     */
    @GetMapping("/v1.0/schedules/logs")
    public Reply getScheduleLogs(Search search) {
        return service.getScheduleLogs(search);
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @GetMapping("/v1.0/schedules/logs/{id}")
    Reply getScheduleLog(@PathVariable Long id) {
        return service.getScheduleLog(id);
    }
}
