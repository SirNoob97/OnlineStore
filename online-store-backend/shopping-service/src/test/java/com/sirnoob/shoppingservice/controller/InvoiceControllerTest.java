package com.sirnoob.shoppingservice.controller;

import static com.sirnoob.shoppingservice.util.Provider.createInvoiceRandomValuesItems;
import static com.sirnoob.shoppingservice.util.Provider.createInvoiceRequestRandomValues;
import static com.sirnoob.shoppingservice.util.Provider.createProductRandomPrice;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sirnoob.shoppingservice.client.ProductClient;
import com.sirnoob.shoppingservice.dto.InvoiceRequest;
import com.sirnoob.shoppingservice.entity.Invoice;
import com.sirnoob.shoppingservice.repository.InvoiceRepository;
import com.sirnoob.shoppingservice.service.InvoiceServiceImpl;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import({ InvoiceServiceImpl.class })
class InvoiceControllerTest {
  @MockBean
  private InvoiceRepository invoiceRepository;
  @MockBean
  private ProductClient productClient;

  @Autowired
  private MockMvc mockMvc;

  private static final MediaType JSON = MediaType.APPLICATION_JSON;
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final InvoiceRequest INVOICE_REQUEST = createInvoiceRequestRandomValues();
  private static final Invoice INVOICE = createInvoiceRandomValuesItems();
  private static final PageImpl<Invoice> INVOICE_PAGE = new PageImpl<>(List.of(INVOICE));

  @Test
  public void create_Return201HttpStatus_WhenSuccessful() throws Exception {
    BDDMockito.when(invoiceRepository.existsByInvoiceNumber(anyLong())).thenReturn(false);
    BDDMockito.when(productClient.getInfo(anyLong(), anyString()))
        .thenReturn(ResponseEntity.ok(createProductRandomPrice()));
    BDDMockito.when(productClient.updateStock(anyLong(), anyInt()))
        .thenReturn(ResponseEntity.noContent().build());
    BDDMockito.when(invoiceRepository.save(any())).thenReturn(INVOICE);

    mockMvc.perform(post("/invoices")
                    .contentType(JSON)
                    .content(OBJECT_MAPPER.writeValueAsString(INVOICE_REQUEST))
                    .accept(JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(JSON));
  }

  @Test
  public void create_Return400HttpStatus_WhenInvoicesExistsInTheLog() throws Exception {
    BDDMockito.when(invoiceRepository.existsByInvoiceNumber(anyLong())).thenReturn(true);

    mockMvc.perform(post("/invoices")
                    .contentType(JSON)
                    .content(OBJECT_MAPPER.writeValueAsString(INVOICE_REQUEST))
                    .accept(JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(JSON));
  }

  @Test
  public void create_Return400HttpStatus_WhenInvoiceHasInvalidFields() throws Exception {
    mockMvc.perform(post("/invoices")
                    .contentType(JSON)
                    .content(OBJECT_MAPPER.writeValueAsString(new InvoiceRequest()))
                    .accept(JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(JSON));
  }

  @Test
  public void delete_Return201HttpStatus_WhenSuccessful() throws Exception {
    BDDMockito.when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(INVOICE));
    BDDMockito.doNothing().when(invoiceRepository).delete(any());

    mockMvc.perform(delete("/invoices/1")).andExpect(status().isNoContent());
  }

  @Test
  public void delete_Return404HttpStatus_WhenInvoiceWasNotFound() throws Exception {
    BDDMockito.when(invoiceRepository.findById(anyLong())).thenReturn(Optional.empty());

    mockMvc.perform(delete("/invoices/1")).andExpect(status().isNotFound());
  }

  @Test
  public void getByInvoiceNumber_Return200HttpStatus_WhenSuccessful() throws Exception {
    BDDMockito.when(invoiceRepository.findByInvoiceNumber(anyLong())).thenReturn(Optional.of(INVOICE));

    mockMvc.perform(get("/invoices/1").accept(JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  public void getByInvoiceNumber_Return404HttpStatus_WhenInvoicesWasNotFound() throws Exception {
    BDDMockito.when(invoiceRepository.findByInvoiceNumber(anyLong())).thenReturn(Optional.empty());

    mockMvc.perform(get("/invoices/1").accept(JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }

  @Test
  public void getByUserName_Return200HttpStatus_WhenSuccessful() throws Exception {
    BDDMockito.when(invoiceRepository.findByCustomerUserName(anyString(), any(Pageable.class)))
        .thenReturn(INVOICE_PAGE);

    mockMvc.perform(get("/invoices/users?userName=TEST").accept(JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  public void getByUserName_Return404HttpStatus_WhenInvoicesWasNotFound() throws Exception {
    BDDMockito.when(invoiceRepository.findByCustomerUserName(anyString(), any(Pageable.class))).thenReturn(Page.empty());

    mockMvc.perform(get("/invoices/users?userName=TEST").accept(JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }

  @Test
  public void getByProductBarCode_Return200HttpStatus_WhenSuccessful() throws Exception {
    BDDMockito.when(invoiceRepository.findByItemsProductBarCode(anyLong(), any(Pageable.class)))
        .thenReturn(INVOICE_PAGE);

    mockMvc.perform(get("/invoices/products/1").accept(JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  public void getByProductBarCode_Return404HttpStatus_WhenInvoicesWasNotFound() throws Exception {
    BDDMockito.when(invoiceRepository.findByItemsProductBarCode(anyLong(), any(Pageable.class))).thenReturn(Page.empty());

    mockMvc.perform(get("/invoices/products/1").accept(JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }
}
