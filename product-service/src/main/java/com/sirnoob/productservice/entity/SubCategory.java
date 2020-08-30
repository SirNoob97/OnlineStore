package com.sirnoob.productservice.entity;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@ToString
@Getter
@Setter
@Builder
@Table("sub_categories")
public class SubCategory implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column("sub_category_id")
	private Long subCategoryId;
	
	@NotNull(message = "The Category Name is required.")
	@Column("sub_category_name")
	private String subCategoryName;
}
