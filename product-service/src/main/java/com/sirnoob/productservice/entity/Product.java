package com.sirnoob.productservice.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Table(value = "products")
public class Product {

//	https://github.com/rxonda/webflux-with-jpa
//	https://github.com/chang-chao/spring-webflux-reactive-jdbc-sample/blob/master/src/main/java/me/changchao/spring/springwebfluxasyncjdbcsample/service/CityServiceImpl.java
	@Id
	private Long productId;
	private String productNumberId;
	private String productName;
	private String productDescription;
	private LocalDate createAt;
	
	private Category category;
	
}
