package com.sirnoob.shoppingservice.service;

import java.text.DecimalFormat;
import java.util.Set;
import java.util.stream.Collectors;

import com.sirnoob.shoppingservice.client.IProductClient;
import com.sirnoob.shoppingservice.dto.InvoiceRequest;
import com.sirnoob.shoppingservice.dto.ProductDto;
import com.sirnoob.shoppingservice.entity.Invoice;
import com.sirnoob.shoppingservice.entity.Item;
import com.sirnoob.shoppingservice.model.Product;
import com.sirnoob.shoppingservice.repository.IInvoiceRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class InvoiceServiceImpl implements IInvoiceService{

  private static final String INVOICE_NOT_FOUND = "Invoice Not Found!!";
  private static final String THE_USER_HAS_NO_INVOICES = "The User Has No Invoices!!";

  private final IInvoiceRepository iInvoiceRepository;
  private final IProductClient iProductClient;

  @Override
  public Invoice createInvoice(InvoiceRequest invoiceRequest) {
    Set<Item> items = invoiceRequest.getProducts()
                                    .stream()
                                    .map(productDto -> buildItem(getProductAndUpdateStock(productDto), productDto))
                                    .collect(Collectors.toSet());

    return iInvoiceRepository.save(buildInvoice(invoiceRequest, items));
  }

  @Override
  public void deleteInvoice(Long invoiceId) {
    Invoice invoice = iInvoiceRepository.findById(invoiceId).orElseThrow(() -> getResponseStatusException(INVOICE_NOT_FOUND));
    iInvoiceRepository.delete(invoice);
  }

  @Override
  public Page<Invoice> getInvoiceByUserName(String userName, Pageable pageable) {
    Page<Invoice> invoices = iInvoiceRepository.findByCustomerUserName(userName, pageable);
    return throwExceptionIfPageIsEmpty(invoices, THE_USER_HAS_NO_INVOICES);
  }

  @Override
  public Invoice getInvoiceByInvoiceNumber(Long invoiceNumber) {
    return iInvoiceRepository.findByInvoiceNumber(invoiceNumber).orElseThrow(() -> getResponseStatusException(INVOICE_NOT_FOUND));
  }

  @Override
  public Page<Invoice> getInvoiceByProductBarCode(Long productBarCode, Pageable pageable){
    Page<Invoice> invoices = iInvoiceRepository.findByItemsProductBarCode(productBarCode, pageable);
    return throwExceptionIfPageIsEmpty(invoices, THE_USER_HAS_NO_INVOICES);
  }



  private Product getProductAndUpdateStock(ProductDto productDto){
    Product product = iProductClient.getProductForInvoice(productDto.getProductBarCode(), productDto.getProductName()).getBody();
    iProductClient.updateProductStock(productDto.getProductBarCode(), (productDto.getQuantity() * -1));
    return product;
  }



  private Item buildItem(Product product, ProductDto productDto){
    return Item.builder()
                .product(product)
                .quantity(productDto.getQuantity())
                .productBarCode(productDto.getProductBarCode())
                .subTotal(productDto.getQuantity() * product.getProductPrice()).build();
  }

  private Invoice buildInvoice(InvoiceRequest invoiceRequest, Set<Item> items){
    Double total = items.stream()
                        .map(Item::getSubTotal)
                        .reduce(0.0, (a, b) -> a + b);

    DecimalFormat dec = new DecimalFormat("#.00");

    return Invoice.builder()
                  .invoiceId(invoiceRequest.getInvoiceId())
                  .invoiceNumber(invoiceRequest.getInvoiceNumber())
                  .customer(invoiceRequest.getCustomer())
                  .items(items)
                  .total(Double.valueOf(dec.format(total))).build();
  }

  private ResponseStatusException getResponseStatusException(String message){
    return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
  }

  private <T> Page<T> throwExceptionIfPageIsEmpty(Page<T> page, String message) {
    if (!page.isEmpty()) return page;
    throw getResponseStatusException(message);
  }
}
