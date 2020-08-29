package com.sirnoob.productservice.entity;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("products")
public class Product {

//	https://github.com/rxonda/webflux-with-jpa
//	https://github.com/chang-chao/spring-webflux-reactive-jdbc-sample/blob/master/src/main/java/me/changchao/spring/springwebfluxasyncjdbcsample/service/CityServiceImpl.java
	@Id
	@Column("product_id")
	private Long productId;
	
	@Digits(integer = 9, fraction = 0, message = "The Number Id must be 9 digits.")
	@NotNull(message = "The Number is required.")
	@Column("product_number")
	private Integer productNumber;
	
	@NotNull(message = "The Name is required.")
	@NotEmpty(message = "The Name is required.")
	@Column("product_name")
	private String productName;
	
	@Column("product_description")
	private String productDescription;
	
	@Positive(message = "The Product Stock must be positive.")
	@NotNull(message = "The Product Stock is required.")
	@Column("product_stock")
	private Integer productStock;
	
	@Digits(integer = 5, fraction = 2)
	@Column("product_price")
	private Double productPrice;
	
	@Column("create_at")
	private LocalDate createAt;
	
	private Category category;
	
	private List<SubCategory> subCategories;
	
}
