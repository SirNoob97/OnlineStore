package com.sirnoob.productservice.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MainCategory{

	@PositiveOrZero(message = "The Id must be positive.")
	@NotNull(message = "The Id is required.")
	private Long categoryId;
	
	@NotNull(message = "The Category Name is required.")
	@NotEmpty(message = "The Category Name is required.")
	@Size(min = 5, max = 30, message = "The Category Name must be between 5 and 30 characters long.")
	private String categoryName;
}
