package com.sirnoob.productservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "main_categories")
public class MainCategory{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "category_id")
  private Long mainCategoryId;

  @NotNull(message = "The Category Name is required.")
  @NotEmpty(message = "The Category Name is required.")
  @Column(name = "category_name", nullable = false, unique = true, length = 30)
  private String mainCategoryName;
}
