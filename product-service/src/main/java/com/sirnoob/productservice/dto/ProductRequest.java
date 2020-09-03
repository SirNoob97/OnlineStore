package com.sirnoob.productservice.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import lombok.Getter;

@Getter
public class ProductRequest {

	@PositiveOrZero(message = "The Bar Code must be positive.")
	@Digits(integer = 13, fraction = 0, message = "The Bar Code must be 9 digits.")
	@NotNull(message = "The Number is required.")
	private Long productBarCode;
	
	@NotNull(message = "The Name is required.")
	@NotEmpty(message = "The Name is required.")
	@Size(min = 5, max = 30, message = "The Name must be between 5 and 30 characters long.")
	private String productName;
	private String productDescription;
	
	@Positive(message = "The Product Stock must be greater than zero.")
	@NotNull(message = "The Product Stock is required.")
	@Digits(integer = 4, fraction = 0, message = "The Stock must be an integer and its maximum is 9999.")
	private Integer productStock;
	
	@PositiveOrZero(message = "The Product Price must be positive.")
	@NotNull(message = "The Product Price is required.")
	@Digits(integer = 6, fraction = 2, message = "The Price can only have 2 decimal places and its maximum is 999999.99")
	private Double productPrice;
	
	@Positive(message = "The Category Id must be positive.")
	@NotNull(message = "The Category Id is required.")
	private Long categoryId;
}
