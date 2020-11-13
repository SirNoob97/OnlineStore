package com.sirnoob.shoppingservice.controller;

import javax.validation.Valid;

import com.sirnoob.shoppingservice.dto.InvoiceRequest;
import com.sirnoob.shoppingservice.entity.Invoice;
import com.sirnoob.shoppingservice.service.IInvoiceService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/invoices")
public class InvoiceController {

  private static final MediaType JSON = MediaType.APPLICATION_JSON;

  private final IInvoiceService iInvoiceService;

  @PostMapping
  public ResponseEntity<Invoice> createInvoice(@Valid @RequestBody InvoiceRequest invoiceRequest){
    return ResponseEntity.status(HttpStatus.CREATED).contentType(JSON).body(iInvoiceService.persistInvoice(invoiceRequest));
  }
}
