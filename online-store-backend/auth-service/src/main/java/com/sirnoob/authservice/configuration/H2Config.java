package com.sirnoob.authservice.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.connectionfactory.init.CompositeDatabasePopulator;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;

@Profile(value = "test")
@Configuration
@EnableR2dbcRepositories
public class H2Config extends AbstractR2dbcConfiguration{

  @Bean
  @Override
  public ConnectionFactory connectionFactory(){
     return ConnectionFactories.get("r2dbc:h2:mem:///accounts?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE");
  }

  @Bean
  public DatabaseClient databaseClient(ConnectionFactory connectionFactory){
    return DatabaseClient.builder().connectionFactory(connectionFactory).build();
  }

  @Bean
  public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory){
    var initializer = new ConnectionFactoryInitializer();
    initializer.setConnectionFactory(connectionFactory);

    var populator = new CompositeDatabasePopulator();
    populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
    populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("data.sql")));

    initializer.setDatabasePopulator(populator);

    return initializer;
  }
}
