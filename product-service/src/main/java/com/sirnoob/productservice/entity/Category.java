package com.sirnoob.productservice.entity;

import org.springframework.data.relational.core.mapping.Table;

@Table(value = "categories")
public class Category {

	private Long categoryId;
	private String categoryName;
}
