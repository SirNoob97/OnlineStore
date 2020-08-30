package com.sirnoob.productservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;

@Configuration
public class ProductConfiguration extends AbstractR2dbcConfiguration {

//  @Bean
//  public R2dbcRepositoryFactory factory(DatabaseClient client) {
//    RelationalMappingContext context = new RelationalMappingContext();
//    context.initialize();
//
//    return new R2dbcRepositoryFactory(client, (ReactiveDataAccessStrategy) context);
//  }


  @Bean
  public DatabaseClient databaseClient(ConnectionFactory factory) {
    return DatabaseClient.builder().connectionFactory(factory).build();
  }

  @Bean
  public ConnectionFactory connectionFactory() {
    ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
            .option(ConnectionFactoryOptions.DRIVER, "postgresql")
            .option(ConnectionFactoryOptions.HOST, "localhost")
						.option(ConnectionFactoryOptions.PORT, 5432)
						.option(ConnectionFactoryOptions.USER, "postgres")
						.option(ConnectionFactoryOptions.PASSWORD, "postgres")
						.option(ConnectionFactoryOptions.DATABASE, "store")
            .build();
    return ConnectionFactories.get(options);
  }


}