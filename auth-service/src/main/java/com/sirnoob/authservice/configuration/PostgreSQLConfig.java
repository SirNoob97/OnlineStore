package com.sirnoob.authservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;

@Profile("postgresql")
@Configuration
@EnableR2dbcRepositories
public class PostgreSQLConfig {

  @Value("${envs.postgres.hostname}")
  private String hostname;

  @Value("${envs.postgres.port}")
  private Integer port;

  @Value("${envs.postgres.database}")
  private String dataBase;

  @Value("${envs.postgres.user}")
  private String user;

  @Value("${envs.postgres.password}")
  private String password;

  @Bean
  public ConnectionFactory connectionFactory() {
    ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
        .option(ConnectionFactoryOptions.DRIVER, "postgresql")
        .option(ConnectionFactoryOptions.HOST, hostname)
        .option(ConnectionFactoryOptions.PORT, port)
        .option(ConnectionFactoryOptions.DATABASE, dataBase)
        .option(ConnectionFactoryOptions.USER, user)
        .option(ConnectionFactoryOptions.PASSWORD, password)
        .build();
    return ConnectionFactories.get(options);
  }
}
