package com.sirnoob.shoppingservice.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sirnoob.shoppingservice.model.Product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "items")
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long itemId;

  @Embedded
  private Product product;

  @Column(nullable = false)
  private Integer quantity;

  @Column(name = "sub_total", nullable = false)
  private Double subTotal;
}
