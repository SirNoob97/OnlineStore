package com.sirnoob.productservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.transaction.reactive.TransactionalOperator;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;

@Configuration
public class ProductConfiguration extends AbstractR2dbcConfiguration {

	@Bean
	public ConnectionFactory connectionFactory() {
		ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
				.option(ConnectionFactoryOptions.DRIVER, "postgresql")
				.option(ConnectionFactoryOptions.HOST, "localhost")
				.option(ConnectionFactoryOptions.PORT, 5432)
				.option(ConnectionFactoryOptions.USER, "postgres")
				.option(ConnectionFactoryOptions.PASSWORD, "postgres")
				.option(ConnectionFactoryOptions.DATABASE, "store").build();
		return ConnectionFactories.get(options);
	}

	@Bean
	public DatabaseClient databaseClient(ConnectionFactory connectionFactory) {		
		return DatabaseClient.create(connectionFactory);
	}
	
	@Bean
	public TransactionalOperator transactionalOperator(ConnectionFactory connectionFactory) {
		return TransactionalOperator.create(new R2dbcTransactionManager(connectionFactory));
	}
	
}