package com.sirnoob.shoppingservice.service;

import com.sirnoob.shoppingservice.dto.InvoiceRequest;
import com.sirnoob.shoppingservice.entity.Invoice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IInvoiceService {

  public Invoice createInvoice(InvoiceRequest invoiceRequest);

  public void deleteInvoice(Long invoiceId);

  public Page<Invoice> getInvoiceByUserName(String userName, Pageable pageable);

  public Invoice getInvoiceByInvoiceNumber(Long invoiceNumber);

  public Page<Invoice> getInvoiceByProductBarCode(Long productBarCode, Pageable pageable);
}
