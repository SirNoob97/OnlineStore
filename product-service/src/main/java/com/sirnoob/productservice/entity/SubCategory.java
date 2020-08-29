package com.sirnoob.productservice.entity;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("sub_categories")
public class SubCategory {

	@Id
	@Column("sub_category_id")
	private Long subCategoryId;
	
	@NotNull(message = "The Category Name is required.")
	@Column("sub_category_name")
	private String subCategoryName;
}
