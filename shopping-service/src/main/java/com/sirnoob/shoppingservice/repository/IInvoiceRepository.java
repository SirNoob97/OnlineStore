package com.sirnoob.shoppingservice.repository;

import java.util.List;
import java.util.Optional;

import com.sirnoob.shoppingservice.entity.Invoice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IInvoiceRepository extends JpaRepository<Invoice, Long> {

  public List<Invoice> findByCustomerUserName(String userName);

  public Optional<Invoice> findByInvoiceNumber(Long invoiceNumber);
}
