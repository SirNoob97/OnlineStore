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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
  @Column(name = "sub_category_id", updatable = false)
  @Setter(AccessLevel.NONE)
  private Long subCategoryId;

  @Column(name = "sub_category_name", nullable = false, unique = true, length = 50)
  private String subCategoryName;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JoinColumn(name = "category_id")
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  @ManyToOne(fetch = FetchType.LAZY)
  //@OnDelete(action = OnDeleteAction.CASCADE)
  private MainCategory mainCategory;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonIgnoreProperties("subCategories")
  @ManyToMany(mappedBy = "subCategories")
  private Set<Product> products;
}
