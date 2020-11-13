package com.sirnoob.shoppingservice.service;

import java.util.Set;
import java.util.stream.Collectors;

import com.sirnoob.shoppingservice.client.IProductClient;
import com.sirnoob.shoppingservice.dto.InvoiceRequest;
import com.sirnoob.shoppingservice.entity.Invoice;
import com.sirnoob.shoppingservice.entity.Item;
import com.sirnoob.shoppingservice.model.Product;
import com.sirnoob.shoppingservice.repository.IInvoiceRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class InvoiceServiceImpl implements IInvoiceService{

  private final IInvoiceRepository iInvoiceRepository;
  private final IProductClient iProductClient;

  @Override
  public Invoice createInvoice(InvoiceRequest invoiceRequest) {
    Set<Item> items = invoiceRequest.getProducts()
                                    .stream()
                                    .map(dtos -> {
                                      Product product = iProductClient.getProductForInvoice(dtos.getProductBarCode(), dtos.getProductName()).getBody();
                                      iProductClient.updateProductStock(dtos.getProductBarCode(), (dtos.getQuantity() * -1));
                                      return Item.builder()
                                                 .product(product)
                                                 .quantity(dtos.getQuantity())
                                                 .subTotal(dtos.getQuantity() * product.getProductPrice())
                                                 .build();
                                    })
                                    .collect(Collectors.toSet());

    Double total = items.stream().map(Item::getSubTotal).reduce(0.0, (a, b) -> a + b);

    Invoice invoice = Invoice.builder().invoiceNumber(invoiceRequest.getInvoiceNumber())
        .customer(invoiceRequest.getCustomer()).items(items).total(total).build();

    return iInvoiceRepository.save(invoice);
  }

  @Override
  public void updateInvoice(InvoiceRequest invoice) {

  }

  @Override
  public void deleteInvoice(Long invoiceId) {

  }

  @Override
  public Invoice getInvoiceByUserName(String userName) {
    return null;
  }

  @Override
  public Invoice getInvoiceByInvoiceNumber(Long invoiceNumber) {
    return null;
  }
}
