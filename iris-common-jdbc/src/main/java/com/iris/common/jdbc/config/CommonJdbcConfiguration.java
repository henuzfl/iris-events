package com.iris.common.jdbc.config;

import com.iris.common.jdbc.CommonJdbcOperations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @Author: zfl
 * @Date: 2020/8/19 18:06
 * @Version: 1.0.0
 */
@Configuration
public class CommonJdbcConfiguration {

    @Bean
    @ConditionalOnMissingBean(CommonJdbcOperations.class)
    public CommonJdbcOperations commonJdbcOperations(JdbcTemplate jdbcTemplate) {
        return new CommonJdbcOperations(jdbcTemplate);
    }
}
