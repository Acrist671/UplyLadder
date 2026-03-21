package com.otsos.userservice.configs;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    private final DataSource dataSource;

    @Autowired
    FlywayConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public Flyway flyway() {
        return Flyway.configure()
                .dataSource(dataSource)
                .schemas("users")
                .createSchemas(true)
                .table("flyway_schema_history_users")
                .load();
    }


}
