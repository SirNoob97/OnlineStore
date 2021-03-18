package com.sirnoob.shoppingservice.repository;

import java.util.Optional;

import com.sirnoob.shoppingservice.entity.Invoice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IInvoiceRepository extends JpaRepository<Invoice, Long> {

  public Page<Invoice> findByCustomerUserName(String userName, Pageable pageable);

  public Optional<Invoice> findByInvoiceNumber(Long invoiceNumber);

  public Page<Invoice> findByItemsProductBarCode(Long productBarCode, Pageable pageable);

  public boolean existsByInvoiceNumber(Long invoiceNumber);
}
