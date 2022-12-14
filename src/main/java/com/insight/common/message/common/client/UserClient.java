package com.insight.common.message.common.client;

import com.insight.common.message.common.config.FeignClientConfig;
import com.insight.utils.pojo.base.Reply;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 宣炳刚
 * @date 2019-08-31
 * @remark Feign客户端
 */
@FeignClient(name = "base-user", configuration = FeignClientConfig.class)
public interface UserClient {

    /**
     * 查询用户
     *
     * @param keyword 用户手机号
     * @return Reply
     */
    @GetMapping("/base/user/manage/v1.0/users")
    Reply getUser(@RequestParam String keyword, Boolean invalid);
}
