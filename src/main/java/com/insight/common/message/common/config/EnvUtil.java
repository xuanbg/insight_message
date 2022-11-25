package com.insight.common.message.common.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author 宣炳刚
 * @date 2022/11/25
 * @remark
 */
@Component
public class EnvUtil implements EnvironmentAware {
    private static Environment env;

    /**
     * 设置环境变量
     *
     * @param environment Environment
     */
    @Override
    public void setEnvironment(@NotNull Environment environment) {
        env = environment;
    }

    /**
     * 读取Key对应的配置值
     *
     * @param key 配置Key
     * @return 配置值
     */
    public static String getValue(String key) {
        return env.getProperty(key);
    }
}
