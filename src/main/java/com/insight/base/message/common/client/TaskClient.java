package com.insight.base.message.common.client;

import com.insight.util.pojo.Reply;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;

import java.net.URI;

/**
 * @author 宣炳刚
 * @date 2019-08-31
 * @remark 消息中心Feign客户端
 */
@FeignClient()
public interface TaskClient {

    /**
     * POST方法
     *
     * @param uri URI
     * @return Reply
     */
    @RequestLine("GET")
    Reply get(URI uri);

    /**
     * POST方法
     *
     * @param uri URI
     * @param dto DTO
     * @return Reply
     */
    @RequestLine("POST")
    Reply post(URI uri, Object dto);

    /**
     * PUT方法
     *
     * @param uri URI
     * @param dto DTO
     * @return Reply
     */
    @RequestLine("PUT")
    Reply put(URI uri, Object dto);

    /**
     * DELETE方法
     *
     * @param uri URI
     * @param dto DTO
     * @return Reply
     */
    @RequestLine("DELETE")
    Reply delete(URI uri, Object dto);
}
