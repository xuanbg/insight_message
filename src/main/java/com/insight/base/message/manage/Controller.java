package com.insight.base.message.manage;

import com.insight.util.ReplyHelper;
import com.insight.util.Util;
import com.insight.util.pojo.Reply;
import org.springframework.web.bind.annotation.*;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息管理服务控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/base/message/manage")
public class Controller {
    private final Service service;

    /**
     * 构造方法
     *
     * @param service 注入Service
     */
    public Controller(Service service) {
        this.service = service;
    }

    /**
     * 获取短信模板列表
     *
     * @param keyword     查询关键词
     * @param page        分页页码
     * @param size        每页记录数
     * @return Reply
     */
    @GetMapping("/v1.0/templates")
    public Reply getTemplates(@RequestParam String keyword, @RequestParam int page, @RequestParam int size) {
        return service.getTemplates(keyword, page, size);
    }
}
