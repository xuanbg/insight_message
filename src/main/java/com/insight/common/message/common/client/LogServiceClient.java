package com.insight.common.message.common.client;


import com.insight.common.message.common.config.FeignClientConfig;
import com.insight.utils.pojo.base.Reply;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 宣炳刚
 * @date 2019-08-31
 * @remark 消息中心Feign客户端
 */
@FeignClient(name = "common-basedata", configuration = FeignClientConfig.class)
public interface LogServiceClient {

    /**
     * 获取日志列表
     *
     * @param id      业务ID
     * @param code    业务代码
     * @param keyword 查询关键词
     * @return Reply
     */
    @GetMapping("/common/log/v1.0/logs")
    Reply getLogs(@RequestParam Long id, @RequestParam String code, @RequestParam String keyword);

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @GetMapping("/common/log/v1.0/logs/{id}")
    Reply getLog(@PathVariable Long id);
}
