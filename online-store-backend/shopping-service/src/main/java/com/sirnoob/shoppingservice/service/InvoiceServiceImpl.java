package com.sirnoob.shoppingservice.service;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.stream.Collectors;

import com.sirnoob.shoppingservice.client.IProductClient;
import com.sirnoob.shoppingservice.dto.InvoiceRequest;
import com.sirnoob.shoppingservice.dto.ProductDto;
import com.sirnoob.shoppingservice.entity.Invoice;
import com.sirnoob.shoppingservice.entity.Item;
import com.sirnoob.shoppingservice.exception.ResourceNotFoundException;
import com.sirnoob.shoppingservice.model.Product;
import com.sirnoob.shoppingservice.repository.IInvoiceRepository;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class InvoiceServiceImpl implements IInvoiceService{

  private static final String INVOICE_NOT_FOUND = "Invoice Not Found!!";
  private static final String THE_USER_HAS_NO_INVOICES = "The User Has No Invoices!!";
  private static final String NO_INVOICE_ARE_RELATED_WHITH_THATH_PRODUCT = "No Invoice Are Related With That Product!!";

  private static final String INVOICE_NUMBER_MUST_BE_UNIQUE = "Invoice Number Must Be Unique!!";
  private static final String INVOICE_NUMBER_UNIQUE_VIOLATION_TITLE = "Unique index violation: INVOICE.INVOICE_NUMBER";
  private static final String INVOICE_COULD_NOT_EXECUTE_OPERATION = "could not execute operation, " + INVOICE_NUMBER_MUST_BE_UNIQUE;

  private final IInvoiceRepository iInvoiceRepository;
  private final IProductClient iProductClient;

  @Transactional
  @Override
  public Invoice createInvoice(InvoiceRequest invoiceRequest) {
    if(iInvoiceRepository.existsByInvoiceNumber(invoiceRequest.getInvoiceNumber())) getDataIntegrityViolationException();

    Set<Item> items = invoiceRequest.getProducts()
                                    .stream()
                                    .map(productDto -> buildItem(getProductAndUpdateStock(productDto), productDto))
                                    .collect(Collectors.toSet());

    return iInvoiceRepository.save(buildInvoice(invoiceRequest, items));
  }

  @Transactional
  @Override
  public void deleteInvoice(Long invoiceId) {
    Invoice invoice = iInvoiceRepository.findById(invoiceId).orElseThrow(() -> getResourceNotFoundException(INVOICE_NOT_FOUND));
    iInvoiceRepository.delete(invoice);
  }

  @Override
  public Page<Invoice> getInvoiceByUserName(String userName, Pageable pageable) {
    Page<Invoice> invoices = iInvoiceRepository.findByCustomerUserName(userName, pageable);
    return throwExceptionIfPageIsEmpty(invoices, THE_USER_HAS_NO_INVOICES);
  }

  @Override
  public Invoice getInvoiceByInvoiceNumber(Long invoiceNumber) {
    return iInvoiceRepository.findByInvoiceNumber(invoiceNumber).orElseThrow(() -> getResourceNotFoundException(INVOICE_NOT_FOUND));
  }

  @Override
  public Page<Invoice> getInvoiceByProductBarCode(Long productBarCode, Pageable pageable){
    Page<Invoice> invoices = iInvoiceRepository.findByItemsProductBarCode(productBarCode, pageable);
    return throwExceptionIfPageIsEmpty(invoices, NO_INVOICE_ARE_RELATED_WHITH_THATH_PRODUCT);
  }



  private Product getProductAndUpdateStock(ProductDto productDto){
    var productRes = iProductClient.getProductForInvoice(productDto.getProductBarCode(), productDto.getProductName());

    if (productRes.getStatusCode().is2xxSuccessful())
    iProductClient.updateProductStock(productDto.getProductBarCode(), (productDto.getQuantity() * -1));

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
    DecimalFormat dec = new DecimalFormat("#.00");
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
