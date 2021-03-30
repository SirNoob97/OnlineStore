package com.sirnoob.shoppingservice.service;

import static com.sirnoob.shoppingservice.util.Provider.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.sirnoob.shoppingservice.client.IProductClient;
import com.sirnoob.shoppingservice.dto.InvoiceRequest;
import com.sirnoob.shoppingservice.entity.Invoice;
import com.sirnoob.shoppingservice.exception.ResourceNotFoundException;
import com.sirnoob.shoppingservice.repository.IInvoiceRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class InvoiceServiceTest {
  @Mock
  private IInvoiceRepository invoiceRepository;
  @Mock
  private IProductClient productClient;

  private IInvoiceService invoiceService;

  private static Invoice staticInvoiceWithItems = createInvoiceRandomValuesItems();
  private static InvoiceRequest staticInvoiceRequest = createInvoiceRequestRandomValues();
  private static PageImpl<Invoice> staticInvoicePage = new PageImpl<>(List.of(staticInvoiceWithItems));

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    invoiceService = new InvoiceServiceImpl(invoiceRepository, productClient);
  }

  @Test
  public void create_ReturnInvoice_WhenSuccessful() {
    BDDMockito.when(invoiceRepository.existsByInvoiceNumber(anyLong())).thenReturn(false);
    BDDMockito.when(productClient.getProductForInvoice(anyLong(), anyString()))
        .thenReturn(ResponseEntity.ok(createProductRandomPrice()));
    BDDMockito.when(productClient.updateProductStock(anyLong(), anyInt()))
        .thenReturn(ResponseEntity.noContent().build());
    BDDMockito.when(invoiceRepository.save(any())).thenReturn(staticInvoiceWithItems);

    var invoice = invoiceService.createInvoice(staticInvoiceRequest);

    assertThat(invoice).isNotNull();
    assertThat(invoice.getCustomer()).isNotNull();
    assertFalse(invoice.getItems().isEmpty());
  }

  @Test
  public void create_ThrowDataIntegrityViolationException_WhenInvoiceAlreadyExistsInTheLog() {
    BDDMockito.when(invoiceRepository.existsByInvoiceNumber(anyLong())).thenReturn(true);

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> invoiceService.createInvoice(staticInvoiceRequest));
  }

  @Test
  public void create_NoExceptionIsTrowed_WhenProductsServiceIsNotAvailable() {
    BDDMockito.when(invoiceRepository.existsByInvoiceNumber(anyLong())).thenReturn(false);
    BDDMockito.when(productClient.getProductForInvoice(anyLong(), anyString()))
        .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(createProductRandomPrice()));
    BDDMockito.when(invoiceRepository.save(any())).thenReturn(staticInvoiceWithItems);

    assertThatCode(() -> invoiceService.createInvoice(createInvoiceRequestRandomValues())).doesNotThrowAnyException();
  }

  @Test
  public void delete_NoExceptionIsThrowed_WhenTheInvoiceIsRemoved() {
    BDDMockito.when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(staticInvoiceWithItems));
    BDDMockito.doNothing().when(invoiceRepository).delete(any());

    assertThatCode(() -> invoiceService.deleteInvoice(getRandomLongNumber())).doesNotThrowAnyException();
  }

  @Test
  public void delete_ThrowResourceNotFoundException_WhenTheInvoiceIsNotFound() {
    BDDMockito.when(invoiceRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThatExceptionOfType(ResourceNotFoundException.class)
        .isThrownBy(() -> invoiceService.deleteInvoice(getRandomLongNumber()));
  }

  @Test
  public void getByUserName_ReturnInvoicePage_WhenSuccessful() {
    BDDMockito.when(invoiceRepository.findByCustomerUserName(anyString(), any(Pageable.class)))
        .thenReturn(staticInvoicePage);

    var invoices = invoiceService.getInvoiceByUserName(TEST, PAGE);

    assertFalse(invoices.isEmpty());
    assertThat(invoices.getContent().contains(staticInvoiceWithItems));
  }

  @Test
  public void getByUserName_ThrowResourceNotFoundException_WhenInvoicePageIsEmpty() {
    BDDMockito.when(invoiceRepository.findByCustomerUserName(anyString(), any(Pageable.class)))
        .thenReturn(Page.empty());

    assertThatExceptionOfType(ResourceNotFoundException.class)
        .isThrownBy(() -> invoiceService.getInvoiceByUserName(TEST, PAGE));
  }

  @Test
  public void getByInvoiceNumber_ReturnInvoice_WhenSuccessful() {
    BDDMockito.when(invoiceRepository.findByInvoiceNumber(anyLong())).thenReturn(Optional.of(staticInvoiceWithItems));

    var invoice = invoiceService.getInvoiceByInvoiceNumber(getRandomLongNumber());

    assertThat(invoice).isNotNull();
    assertThat(invoice.getCustomer()).isNotNull();
    assertFalse(invoice.getItems().isEmpty());
  }

  @Test
  public void getByUserName_ThrowResourceNotFoundException_WhenInvoiceIsNotFound() {
    BDDMockito.when(invoiceRepository.findByInvoiceNumber(anyLong())).thenReturn(Optional.empty());

    assertThatExceptionOfType(ResourceNotFoundException.class)
        .isThrownBy(() -> invoiceService.getInvoiceByInvoiceNumber(getRandomLongNumber()));
  }

  @Test
  public void getByProductBarCode_ReturnInvoicePage_WhenSuccessful() {
    BDDMockito.when(invoiceRepository.findByItemsProductBarCode(anyLong(), any(Pageable.class)))
        .thenReturn(staticInvoicePage);

    var invoices = invoiceService.getInvoiceByProductBarCode(getRandomLongNumber(), PAGE);

    assertFalse(invoices.isEmpty());
    assertThat(invoices.getContent().contains(staticInvoiceWithItems));
  }

  @Test
  public void getByProductBarCode_ThrowResourceNotFoundException_WhenInvoicePageIsEmpty() {
    BDDMockito.when(invoiceRepository.findByItemsProductBarCode(anyLong(), any(Pageable.class)))
        .thenReturn(Page.empty());

    assertThatExceptionOfType(ResourceNotFoundException.class)
        .isThrownBy(() -> invoiceService.getInvoiceByProductBarCode(getRandomLongNumber(), PAGE));
  }
}
