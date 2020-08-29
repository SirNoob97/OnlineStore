package com.sirnoob.productservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;

public class R2DBCConfiguration {
	
	@Bean
	public ConnectionFactory connectionFactory() {
		ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
			    .option(ConnectionFactoryOptions.DRIVER, "h2")
			    .option(ConnectionFactoryOptions.PROTOCOL, "mem")  // file, mem
//			    .option(ConnectionFactoryOptions.HOST, "…")
			    .option(ConnectionFactoryOptions.USER, "store")
			    .option(ConnectionFactoryOptions.PASSWORD, "")
			    .option(ConnectionFactoryOptions.DATABASE, "store")
			    .build();
		return ConnectionFactories.get(options);
	}
    
	
}