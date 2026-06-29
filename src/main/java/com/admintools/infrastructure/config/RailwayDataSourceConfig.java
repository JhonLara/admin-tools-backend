package com.admintools.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;

@Slf4j
@Configuration
@Profile("prod")
public class RailwayDataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        log.info("DATABASE_URL from env: {}", databaseUrl != null ? databaseUrl.replaceAll("://.*?:.*?@", "://****:****@") : "null");

        if (databaseUrl == null || databaseUrl.isBlank()) {
            throw new IllegalStateException("DATABASE_URL environment variable is not set. Please verify PostgreSQL is linked to this service in Railway.");
        }

        return createDataSourceFromRailwayUrl(databaseUrl);
    }

    private DataSource createDataSourceFromRailwayUrl(String databaseUrl) {
        try {
            // Railway format: postgres://user:password@host:port/database
            // or: postgresql://user:password@host:port/database
            URI uri = new URI(databaseUrl);
            String host = uri.getHost();
            int port = uri.getPort() == -1 ? 5432 : uri.getPort();
            String database = uri.getPath().replaceFirst("^/", "");

            String userInfo = uri.getUserInfo();
            String username = "";
            String password = "";
            if (userInfo != null) {
                String[] parts = userInfo.split(":");
                username = parts[0];
                if (parts.length > 1) {
                    password = parts[1];
                }
            }

            String jdbcUrl = String.format(
                "jdbc:postgresql://%s:%d/%s?sslmode=require",
                host, port, database
            );

            log.info("Connecting to PostgreSQL at {}:{}/{} with user {}", host, port, database, username);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("org.postgresql.Driver");
            config.setMaximumPoolSize(5);
            return new HikariDataSource(config);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse DATABASE_URL: " + databaseUrl, e);
        }
    }
}
