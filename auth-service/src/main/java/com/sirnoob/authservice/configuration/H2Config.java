package com.sirnoob.authservice.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.connectionfactory.init.CompositeDatabasePopulator;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import io.r2dbc.spi.ConnectionFactory;

@Configuration
@EnableR2dbcRepositories
public class H2Config {

  //@Bean
  //public ConnectionFactory connectionFactory(){
    //ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
      //.option(ConnectionFactoryOptions.DRIVER, "h2")
      //.option(ConnectionFactoryOptions.PROTOCOL, "mem")
      //.option(ConnectionFactoryOptions.DATABASE, "r2dbc:h2:mem:///userTestDB")
      //.option(ConnectionFactoryOptions.USER, "user")
      //.option(ConnectionFactoryOptions.PASSWORD, "")
      //.build();

    //return ConnectionFactories.get(options);
  //}

  @Bean
  public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory){
    ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
    initializer.setConnectionFactory(connectionFactory);

    CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
    populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
    populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("data.sql")));

    initializer.setDatabasePopulator(populator);

    return initializer;
  }
}