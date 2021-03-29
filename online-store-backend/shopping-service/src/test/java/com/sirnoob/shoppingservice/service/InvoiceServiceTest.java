package com.sirnoob.shoppingservice.service;

import static com.sirnoob.shoppingservice.util.Provider.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.sirnoob.shoppingservice.client.IProductClient;
import com.sirnoob.shoppingservice.repository.IInvoiceRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
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

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);

  }

  @Test
  public void create_ReturnInvoice_WhenSuccessful() {
    BDDMockito.when(invoiceRepository.existsByInvoiceNumber(anyLong())).thenReturn(true);
    BDDMockito.when(productClient.getProductForInvoice(anyLong(), anyString()))
        .thenReturn(ResponseEntity.ok(createProductRandomPrice()));
    BDDMockito.when(productClient.updateProductStock(anyLong(), anyInt())).thenReturn(ResponseEntity.noContent().build());
    BDDMockito.when(invoiceRepository.save(any())).thenReturn(createInvoiceRandomValues());

    var invoice = invoiceService.createInvoice(createInvoiceRequestRandomValues());

    assertThat(invoice).isNotNull();
    assertFalse(invoice.getItems().isEmpty());
  }
}
