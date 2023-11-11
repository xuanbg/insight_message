package com.insight.common.message.common.client;

import com.insight.common.message.common.config.FeignClientConfig;
import com.insight.common.message.common.dto.CodeDto;
import com.insight.utils.pojo.base.Reply;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 宣炳刚
 * @date 2019-08-31
 * @remark Feign客户端
 */
@FeignClient(name = "base-auth", configuration = FeignClientConfig.class)
public interface AuthClient {

    /**
     * 获取日志列表
     *
     * @param dto CodeDTO
     * @return Reply
     */
    @PostMapping("/base/auth/v1.0/codes")
    Reply getCode(@RequestBody CodeDto dto);
}
