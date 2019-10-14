package com.insight.base.message.schedule;

import com.insight.util.Json;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.Reply;
import com.insight.util.pojo.Schedule;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 计划任务服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/base/message")
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
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @GetMapping("/v1.0/schedules")
    public Reply getSchedules(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getSchedules(keyword, page, size);
    }

    /**
     * 获取计划任务详情
     *
     * @param id 计划任务ID
     * @return Reply
     */
    @GetMapping("/v1.0/schedules/{id}")
    public Reply getSchedule(@PathVariable String id) {
        return service.getSchedule(id);
    }

    /**
     * 新增计划任务
     *
     * @param dto 计划任务DTO
     * @return Reply
     */
    @PostMapping("/v1.0/schedules")
    public Reply newSchedule(@Valid @RequestBody Schedule dto) {
        return service.newSchedule(dto);
    }

    /**
     * 立即执行计划任务
     *
     * @param info 用户关键信息
     * @param id   计划任务ID
     * @return Reply
     */
    @PutMapping("/v1.0/schedules")
    public Reply executeSchedule(@RequestHeader("loginInfo") String info, @RequestBody String id) {
        if (id == null || id.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.executeSchedule(loginInfo, id);
    }

    /**
     * 删除计划任务
     *
     * @param info 用户关键信息
     * @param id   计划任务ID
     * @return Reply
     */
    @DeleteMapping("/v1.0/schedules")
    public Reply deleteSchedule(@RequestHeader("loginInfo") String info, @RequestBody String id) {
        if (id == null || id.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.deleteSchedule(loginInfo, id);
    }

    /**
     * 禁用计划任务
     *
     * @param info 用户关键信息
     * @param id   计划任务ID
     * @return Reply
     */
    @PutMapping("/v1.0/schedules/disable")
    public Reply disableSchedule(@RequestHeader("loginInfo") String info, @RequestBody String id) {
        if (id == null || id.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.changeScheduleStatus(loginInfo, id, true);
    }

    /**
     * 启用计划任务
     *
     * @param info 用户关键信息
     * @param id   计划任务ID
     * @return Reply
     */
    @PutMapping("/v1.0/schedules/enable")
    public Reply enableSchedule(@RequestHeader("loginInfo") String info, @RequestBody String id) {
        if (id == null || id.isEmpty()) {
            return ReplyHelper.invalidParam();
        }

        LoginInfo loginInfo = Json.toBeanFromBase64(info, LoginInfo.class);
        return service.changeScheduleStatus(loginInfo, id, false);
    }
}
