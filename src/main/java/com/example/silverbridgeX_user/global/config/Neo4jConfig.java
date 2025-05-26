package com.example.silverbridgeX_user.global.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neo4jConfig {

    @Value("${org.neo4j.driver.uri}")
    private String uri;

    @Value("${org.neo4j.driver.authentication.username}")
    private String username;

    @Value("${org.neo4j.driver.authentication.password}")
    private String password;

    @Bean
    public Driver neo4jDriver() {
        return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }
}
