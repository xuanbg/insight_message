package com.insight.base.message.manage;

import com.insight.base.message.common.entity.ChannelTemp;
import com.insight.base.message.common.entity.Scene;
import com.insight.base.message.common.entity.Template;
import com.insight.util.pojo.Reply;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息管理服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/base/message/manage")
public class ManageController {
    private final ManageService service;

    /**
     * 构造方法
     *
     * @param service 注入Service
     */
    public ManageController(ManageService service) {
        this.service = service;
    }

    /**
     * 获取短信模板列表
     *
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @GetMapping("/v1.0/templates")
    public Reply getTemplates(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getTemplates(keyword, page, size);
    }

    /**
     * 获取短信模板
     *
     * @param id 短信模板ID
     * @return Reply
     */
    @GetMapping("/v1.0/templates/{id}")
    public Reply getTemplate(@PathVariable String id) {
        return service.getTemplate(id);
    }

    /**
     * 新增短信模板
     *
     * @param dto 短信模板DTO
     * @return Reply
     */
    @PostMapping("/v1.0/templates")
    public Reply newTemplate(@Valid @RequestBody Template dto) {
        return service.newTemplate(dto);
    }

    /**
     * 编辑短信模板
     *
     * @param dto 短信模板DTO
     * @return Reply
     */
    @PutMapping("/v1.0/templates")
    public Reply editTemplate(@Valid @RequestBody Template dto) {
        return service.editTemplate(dto);
    }

    /**
     * 删除短信模板
     *
     * @param id 短信模板ID
     * @return Reply
     */
    @DeleteMapping("/v1.0/templates")
    public Reply deleteTemplate(String id) {
        return service.deleteTemplate(id);
    }

    /**
     * 改变短信模板禁用/启用状态
     *
     * @param id     短信模板ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    @PutMapping("/v1.0/templates/status")
    public Reply changeTemplateStatus(String id, boolean status) {
        return service.changeTemplateStatus(id, status);
    }

    /**
     * 获取场景列表
     *
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    @GetMapping("/v1.0/scenes")
    public Reply getScenes(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getScenes(keyword, page, size);
    }

    /**
     * 获取场景
     *
     * @param id 场景ID
     * @return Reply
     */
    @GetMapping("/v1.0/scenes/{id}")
    public Reply getScene(@PathVariable String id) {
        return service.getScene(id);
    }

    /**
     * 新增场景
     *
     * @param dto 场景DTO
     * @return Reply
     */
    @PostMapping("/v1.0/scenes")
    public Reply newScene(@Valid @RequestBody Scene dto) {
        return service.newScene(dto);
    }

    /**
     * 编辑场景
     *
     * @param dto 场景DTO
     * @return Reply
     */
    @PutMapping("/v1.0/scenes")
    public Reply editScene(@Valid @RequestBody Scene dto) {
        return service.editScene(dto);
    }

    /**
     * 删除场景
     *
     * @param id 场景ID
     * @return Reply
     */
    @DeleteMapping("/v1.0/scenes")
    public Reply deleteScene(String id) {
        return service.deleteScene(id);
    }

    /**
     * 改变场景禁用/启用状态
     *
     * @param id     场景ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    @PutMapping("/v1.0/scenes/status")
    public Reply changeSceneStatus(String id, boolean status) {
        return service.changeSceneStatus(id, status);
    }

    /**
     * 添加渠道模板
     *
     * @param dto 渠道模板DTO
     * @return Reply
     */
    @PostMapping("/v1.0/scenes/channels")
    public Reply addChannel(ChannelTemp dto) {
        return service.addChannel(dto);
    }

    /**
     * 移除渠道模板
     *
     * @param id 渠道模板ID
     * @return Reply
     */
    @DeleteMapping("/v1.0/scenes/channels")
    public Reply removeChannel(String id) {
        return service.removeChannel(id);
    }
}
