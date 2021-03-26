package com.sirnoob.shoppingservice.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sirnoob.shoppingservice.model.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "items")
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long itemId;

  @Column(nullable = false)
  private Integer quantity;

  @Column(name = "product_bar_code", nullable = false)
  private Long productBarCode;

  @Embedded
  private Product product;

  @Column(name = "sub_total", nullable = false)
  private Double subTotal;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonIgnoreProperties("items")
  @ManyToMany(mappedBy = "items")
  private Set<Invoice> invoices;
}
