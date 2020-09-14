package com.sirnoob.productservice.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "sub_categories")
public class SubCategory{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "sub_category_id")
  private Long subCategoryId;

  @NotNull(message = "The Sub Category Name is required.")
  @NotEmpty(message = "The Sub Category Name is required.")
  @Size(max = 30, message = "The Sub Category must be a maximum of 30 characters.")
  @Column(name = "sub_category_name", nullable = false, unique = true, length = 30)
  private String subCategoryName;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @NotNull(message = "The Sub Category must belong to a Main Category.")
  @JoinColumn(name = "category_id")
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  @ManyToOne(fetch = FetchType.LAZY)
  private MainCategory mainCategory;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonIgnoreProperties("subCategories")
  @ManyToMany(mappedBy = "subCategories")
  private Set<Product> products;
}
