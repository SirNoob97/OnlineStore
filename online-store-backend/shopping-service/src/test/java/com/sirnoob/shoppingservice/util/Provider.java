package com.sirnoob.shoppingservice.util;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.sirnoob.shoppingservice.dto.InvoiceRequest;
import com.sirnoob.shoppingservice.dto.ProductDto;
import com.sirnoob.shoppingservice.entity.Invoice;
import com.sirnoob.shoppingservice.entity.Item;
import com.sirnoob.shoppingservice.model.Customer;
import com.sirnoob.shoppingservice.model.Product;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class Provider {

  public static final String TEST = "TEST";
  public static final String TESTEMAIL = "TEST@TEST.TEST";
  public static final Pageable PAGE = PageRequest.of(0, 10);


  public static Invoice createInvoiceRandomValues() {
    return Invoice.builder()
                  .invoiceNumber(getRandomLongNumber())
                  .customer(createTestCustomer())
                  .total(getRandomDoubleNumber())
                  .build();
  }

  public static Invoice createInvoiceRandomValuesItems() {
    var invoice = createInvoiceRandomValues();
    invoice.setItems(Set.of(createItemRandomValues()));
    return invoice;
  }

  public static Item createItemRandomValues() {
    return Item.builder()
                .quantity(getRandomIntegerNumber())
                .productBarCode(getRandomLongNumber())
                .product(createProductRandomPrice())
                .subTotal(getRandomDoubleNumber())
                .build();
  }

  public static InvoiceRequest createInvoiceRequestRandomValues(){
    return InvoiceRequest.builder()
                          .invoiceNumber(getRandomLongNumber())
                          .customer(createTestCustomer())
                          .products(Set.of(createProductDtoRandomValues()))
                          .build();
  }

  public static Product createProductRandomPrice() {
    return new Product(TEST, getRandomDoubleNumber());
  }

  public static ProductDto createProductDtoRandomValues(){
    return new ProductDto(getRandomLongNumber(), TEST, getRandomIntegerNumber());
  }

  public static Customer createTestCustomer(){
    return new Customer(TEST, TESTEMAIL);
  }

  public static Long getRandomLongNumber() {
    Long rn = new Random().nextLong();
    return rn > 0 ? rn : rn * -1;
  }

  public static Integer getRandomIntegerNumber() {
    Integer rn = new Random().nextInt();
    return rn > 0 ? rn : rn * -1;
  }

  public static String getRandomString() {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }

  private static Double getRandomDoubleNumber() {
    return new Random().nextDouble() * (new Random().nextInt(100) + 1);
  }
}
