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
    public TopicExchange exchange() {
        return new TopicExchange("amq.topic");
    }

    /**
     * 新增消息发送队列
     *
     * @return Queue
     */
    @Bean
    public Queue messageQueue() {
        return new Queue("schedule.message");
    }

    /**
     * 新增本地调用队列
     *
     * @return Queue
     */
    @Bean
    public Queue localQueue() {
        return new Queue("schedule.local");
    }

    /**
     * 新增远程调用队列
     *
     * @return Queue
     */
    @Bean
    public Queue remoteQueue() {
        return new Queue("schedule.remote");
    }

    /**
     * 消息发送绑定
     * @return Binding
     */
    @Bean
    public Binding manageBinding(){
        return BindingBuilder.bind(messageQueue()).to(exchange()).with("schedule.message");
    }

    /**
     * 本地调用绑定
     * @return Binding
     */
    @Bean
    public Binding localBinding(){
        return BindingBuilder.bind(localQueue()).to(exchange()).with("schedule.local");
    }

    /**
     * 远程调用绑定
     * @return Binding
     */
    @Bean
    public Binding remoteBinding(){
        return BindingBuilder.bind(remoteQueue()).to(exchange()).with("schedule.remote");
    }
}
