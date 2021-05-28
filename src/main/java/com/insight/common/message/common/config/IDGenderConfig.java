package com.insight.common.message.common.config;

import com.insight.utils.SnowflakeCreator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 宣炳刚
 * @date 2020/12/20
 * @remark
 */
@Configuration
@ConfigurationProperties(prefix = "snowflake")
public class IDGenderConfig {
    private long machineId;

    @Bean
    public SnowflakeCreator getSnowFlakeFactory() {
        return new SnowflakeCreator(machineId);
    }

    public long getMachineId() {
        return machineId;
    }

    public void setMachineId(long machineId) {
        this.machineId = machineId;
    }
}
