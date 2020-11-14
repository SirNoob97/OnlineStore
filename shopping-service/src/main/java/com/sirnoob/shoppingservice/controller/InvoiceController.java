package com.sirnoob.shoppingservice.controller;

import javax.validation.Valid;

import com.sirnoob.shoppingservice.dto.InvoiceRequest;
import com.sirnoob.shoppingservice.entity.Invoice;
import com.sirnoob.shoppingservice.service.IInvoiceService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    return ResponseEntity.status(HttpStatus.CREATED).contentType(JSON).body(iInvoiceService.createInvoice(invoiceRequest));
  }

  @DeleteMapping("/{invoiceId}")
  public ResponseEntity<Void> deleteInvoice(@PathVariable Long invoiceId){
    iInvoiceService.deleteInvoice(invoiceId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{invoiceNumber}")
  public ResponseEntity<Invoice> getInvoiceByInvoiceNumber(@PathVariable Long invoiceNumber){
    return getBodyBuilder().body(iInvoiceService.getInvoiceByInvoiceNumber(invoiceNumber));
  }

  @GetMapping("/users")
  public ResponseEntity<Page<Invoice>> getInvoicesByUserName(@RequestParam(required = true) String userName, Pageable pageable){
    return getBodyBuilder().body(iInvoiceService.getInvoiceByUserName(userName, pageable));
  }

  @GetMapping("/products/{productBarCode}")
  public ResponseEntity<Page<Invoice>> getInvoiceByProductBarCode(@PathVariable Long productBarCode, Pageable pageable){
    return getBodyBuilder().body(iInvoiceService.getInvoiceByProductBarCode(productBarCode, pageable));
  }

  private BodyBuilder getBodyBuilder(){
    return ResponseEntity.ok().contentType(JSON);
  }
}
