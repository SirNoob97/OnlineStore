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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sirnoob.shoppingservice.model.Customer;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "invoices")
public class Invoice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long invoiceId;

  @Column(name = "invoice_number", unique = true, nullable = false)
  private Long invoiceNumber;

  @CreationTimestamp
  @Column(name = "created_date", insertable = false, updatable = false)
  private LocalDate createdDate;

  @Embedded
  private Customer customer;

  @Column(nullable = false)
  private Double total;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonIgnoreProperties("invoices")
  @ManyToMany(cascade = CascadeType.PERSIST)
  @JoinTable(name = "invoices_items",
              joinColumns = { @JoinColumn(name = "fk_invoice") },
              inverseJoinColumns = { @JoinColumn(name = "fk_item") })
  private Set<Item> items;
}
