package com.sirnoob.shoppingservice.service;

import com.sirnoob.shoppingservice.dto.InvoiceRequest;
import com.sirnoob.shoppingservice.entity.Invoice;

public interface IInvoiceService {

  public Invoice createInvoice(InvoiceRequest invoiceRequest);

  public void updateInvoice(InvoiceRequest invoiceRequest);

  public void deleteInvoice(Long invoiceId);

  public Invoice getInvoiceByUserName(String userName);

  public Invoice getInvoiceByInvoiceNumber(Long invoiceNumber);
}
