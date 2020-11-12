package com.sirnoob.shoppingservice.service;

import com.sirnoob.shoppingservice.entity.Invoice;

public interface IInvoiceService {

  public Invoice createInvoice(Invoice invoice);

  public void updateInvoice(Invoice invoice);

  public void deleteInvoice(Long invoiceId);

  public Invoice getInvoiceByUserName(String userName);

  public Invoice getInvoiceByNumberInvoice(Long numberInvoice);
}
