package com.sirnoob.productservice.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;
import reactor.core.publisher.Flux;

@Data
@Builder
@Table("products")
public class Product implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column("product_id")
	private Long productId;
	
	@Digits(integer = 7, fraction = 0, message = "The Number Id must be 9 digits.")
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
	@Digits(integer = 7, fraction = 0)
	@NotNull(message = "The Product Stock is required.")
	@Column("product_stock")
	private Integer productStock;

	@Positive(message = "The Product Price must be positive.")
	@NotNull(message = "The Product Price is required.")
	@Digits(integer = 5, fraction = 2)
	@Column("product_price")
	private Double productPrice;
	
	@Column("create_at")
	private LocalDate createAt;
	
	private MainCategory mainCategory;
	private Flux<SubCategory> subCategories;
	
}
