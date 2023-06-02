package com.insight.common.message.schedule;

import com.insight.common.message.common.client.LogClient;
import com.insight.common.message.common.client.LogServiceClient;
import com.insight.common.message.common.entity.OperateType;
import com.insight.utils.Json;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;
import com.insight.utils.pojo.message.Schedule;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 计划任务服务控制器
 */
@RestController
@RequestMapping("/common/message")
public class ScheduleController {
    private static final String BUSINESS = "Schedule";
    private final LogServiceClient client;
    private final ScheduleService service;

    /**
     * 构造方法
     *
     * @param client Feign客户端
     * @param service 注入Service
     */
    public ScheduleController(LogServiceClient client, ScheduleService service) {
        this.client = client;
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
     * @param loginInfo 用户关键信息
     * @param id   计划任务ID
     */
    @PutMapping("/v1.0/schedules/{id}")
    public void executeSchedule(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);

        service.executeSchedule(info, id);
        LogClient.writeLog(info, BUSINESS, OperateType.EDIT, id,null);
    }

    /**
     * 删除计划任务
     *
     * @param loginInfo 用户关键信息
     * @param id   计划任务ID
     */
    @DeleteMapping("/v1.0/schedules/{id}")
    public void deleteSchedule(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);

        service.deleteSchedule(info, id);
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, null);
    }

    /**
     * 禁用计划任务
     *
     * @param loginInfo 用户关键信息
     * @param id   计划任务ID
     */
    @PutMapping("/v1.0/schedules/{id}/disable")
    public void disableSchedule(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);

        service.changeScheduleStatus(info, id, true);
        LogClient.writeLog(info, BUSINESS, OperateType.DISABLE, id, null);
    }

    /**
     * 启用计划任务
     *
     * @param loginInfo 用户关键信息
     * @param id   计划任务ID
     */
    @PutMapping("/v1.0/schedules/{id}/enable")
    public void enableSchedule(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id) {
        LoginInfo info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);

        service.changeScheduleStatus(info, id, false);
        LogClient.writeLog(info, BUSINESS, OperateType.ENABLE, id, null);
    }

    /**
     * 查询日志
     *
     * @param loginInfo 用户登录信息
     * @param search    查询条件
     * @return 日志集合
     */
    @GetMapping("/v1.0/schedules/{id}/logs")
    public Reply getAirportLogs(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id, Search search) {
        var info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        return client.getLogs(id, "Schedule", search.getKeyword());
    }

    /**
     * 获取日志
     *
     * @param loginInfo 用户登录信息
     * @param id        日志ID
     * @return 日志VO
     */
    @GetMapping("/v1.0/schedules/logs/{id}")
    public Reply getAirportLog(@RequestHeader("loginInfo") String loginInfo, @PathVariable Long id) {
        var info = Json.toBeanFromBase64(loginInfo, LoginInfo.class);
        return client.getLog(id);
    }
}
