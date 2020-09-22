package com.sirnoob.productservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "main_categories")
public class MainCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "category_id", updatable = false)
  @Setter(AccessLevel.NONE)
  private Long mainCategoryId;

  @NotEmpty(message = "The Category Name is required")
  @Size(max = 50, message = "The Name must be a maximum of {max} characters")
  @Column(name = "category_name", nullable = false, unique = true, length = 50)
  private String mainCategoryName;
}
