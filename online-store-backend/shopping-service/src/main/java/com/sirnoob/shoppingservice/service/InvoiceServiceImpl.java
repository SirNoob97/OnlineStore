package com.sirnoob.shoppingservice.service;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.stream.Collectors;

import com.sirnoob.shoppingservice.client.ProductClient;
import com.sirnoob.shoppingservice.dto.InvoiceRequest;
import com.sirnoob.shoppingservice.dto.ProductDto;
import com.sirnoob.shoppingservice.entity.Invoice;
import com.sirnoob.shoppingservice.entity.Item;
import com.sirnoob.shoppingservice.exception.ResourceNotFoundException;
import com.sirnoob.shoppingservice.model.Product;
import com.sirnoob.shoppingservice.repository.InvoiceRepository;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class InvoiceServiceImpl implements InvoiceService{

  private static final String INVOICE_NOT_FOUND = "Invoice Not Found!!";
  private static final String THE_USER_HAS_NO_INVOICES = "The User Has No Invoices!!";
  private static final String NO_INVOICE_ARE_RELATED_WHITH_THATH_PRODUCT = "No Invoice Are Related With That Product!!";

  private static final String INVOICE_NUMBER_MUST_BE_UNIQUE = "Invoice Number Must Be Unique!!";
  private static final String INVOICE_NUMBER_UNIQUE_VIOLATION_TITLE = "Unique index violation: INVOICE.INVOICE_NUMBER";
  private static final String INVOICE_COULD_NOT_EXECUTE_OPERATION = "could not execute operation, " + INVOICE_NUMBER_MUST_BE_UNIQUE;

  private final InvoiceRepository invoiceRepository;
  private final ProductClient productClient;

  @Transactional
  @Override
  public Invoice create(InvoiceRequest invoiceRequest) {
    if(invoiceRepository.existsByInvoiceNumber(invoiceRequest.getInvoiceNumber())) getDataIntegrityViolationException();

    var items = invoiceRequest.getProducts()
                                    .stream()
                                    .map(productDto -> buildItem(getProductAndUpdateStock(productDto), productDto))
                                    .collect(Collectors.toSet());

    return invoiceRepository.save(buildInvoice(invoiceRequest, items));
  }

  @Transactional
  @Override
  public void deleteById(Long invoiceId) {
    Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> getResourceNotFoundException(INVOICE_NOT_FOUND));
    invoiceRepository.delete(invoice);
  }

  @Override
  public Page<Invoice> getByUserName(String userName, Pageable pageable) {
    var invoices = invoiceRepository.findByCustomerUserName(userName, pageable);
    return throwExceptionIfPageIsEmpty(invoices, THE_USER_HAS_NO_INVOICES);
  }

  @Override
  public Invoice getByInvoiceNumber(Long invoiceNumber) {
    return invoiceRepository.findByInvoiceNumber(invoiceNumber).orElseThrow(() -> getResourceNotFoundException(INVOICE_NOT_FOUND));
  }

  @Override
  public Page<Invoice> getByProductBarCode(Long productBarCode, Pageable pageable){
    var invoices = invoiceRepository.findByItemsProductBarCode(productBarCode, pageable);
    return throwExceptionIfPageIsEmpty(invoices, NO_INVOICE_ARE_RELATED_WHITH_THATH_PRODUCT);
  }



  private Product getProductAndUpdateStock(ProductDto productDto){
    var productRes = productClient.getInfo(productDto.getProductBarCode(), productDto.getProductName());

    if (productRes.getStatusCode().is2xxSuccessful())
    productClient.updateStock(productDto.getProductBarCode(), (productDto.getQuantity() * -1));

    return productRes.getBody();
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

    return Invoice.builder()
                  .invoiceId(invoiceRequest.getInvoiceId())
                  .invoiceNumber(invoiceRequest.getInvoiceNumber())
                  .customer(invoiceRequest.getCustomer())
                  .items(items)
                  .total(getTotal(total)).build();
  }

  private Double getTotal(Double total) {
    var dec = new DecimalFormat("#.00");
    return Double.valueOf(dec.format(total));
  }

  private ResourceNotFoundException getResourceNotFoundException(String message){
    return new ResourceNotFoundException(message);
  }

  private <T> Page<T> throwExceptionIfPageIsEmpty(Page<T> page, String message) {
    if (!page.isEmpty()) return page;
    throw getResourceNotFoundException(message);
  }

  private void getDataIntegrityViolationException(){
    throw new DataIntegrityViolationException(INVOICE_COULD_NOT_EXECUTE_OPERATION,
                new ConstraintViolationException(INVOICE_NUMBER_MUST_BE_UNIQUE,
                    new SQLException(INVOICE_NUMBER_UNIQUE_VIOLATION_TITLE), INVOICE_NUMBER_MUST_BE_UNIQUE));
  }
}
