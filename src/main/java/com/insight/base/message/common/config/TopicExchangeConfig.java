package com.insight.base.message.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 宣炳刚
 * @date 2019-09-03
 * @remark Topic交换机配置
 */
@Configuration
public class TopicExchangeConfig {

    /**
     * Topic交换机
     *
     * @return TopicExchange
     */
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange("amq.topic");
    }

    /**
     * 新增用户队列
     *
     * @return Queue
     */
    @Bean
    public Queue userQueue() {
        return new Queue("auth.user");
    }

    /**
     * 默认绑定
     * @return Binding
     */
    @Bean
    public Binding binding(){
        return BindingBuilder.bind(userQueue()).to(userExchange()).with("auth.addUser");
    }
}
