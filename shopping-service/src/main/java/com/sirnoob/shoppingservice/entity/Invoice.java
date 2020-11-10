package com.sirnoob.shoppingservice.entity;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.sirnoob.shoppingservice.model.Customer;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "invoices")
public class Invoice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long invoiceId;

  @Column(name = "number_invoice", unique = true, nullable = false)
  private Long numberInvoice;

  @CreationTimestamp
  @Column(name = "created_date", insertable = false, updatable = false)
  private LocalDate createdDate;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "fk_invoices")
  private Set<Item> items;

  @Embedded
  private Customer customer;
}
