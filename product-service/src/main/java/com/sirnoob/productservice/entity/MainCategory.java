package com.sirnoob.productservice.entity;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("main_categories")
public class MainCategory {

	@Id
	@Column("category_id")
	private Long categoryId;
	
	@NotNull(message = "The Category Name is required.")
	@Column("category_name")
	private String categoryName;
}
