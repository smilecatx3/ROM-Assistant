package com.github.romassistant.model.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;


@Configuration
public class DatabaseConfig {
    private static final Log log = LogFactory.getLog(DatabaseConfig.class);


    @Bean
    @ConditionalOnExpression("${db.enabled:true}")
    public BasicDataSource dataSource() throws URISyntaxException {
        log.info("Connecting to database ...");

        URI uri = new URI(System.getenv("DATABASE_URL"));
        String username = uri.getUserInfo().split(":")[0];
        String password = uri.getUserInfo().split(":")[1];
        String url = String.format("jdbc:postgresql://%s:%s%s?sslmode=require",
                uri.getHost(), uri.getPort(), uri.getPath());

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }
}
