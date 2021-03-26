package com.sirnoob.shoppingservice.repository;

import static com.sirnoob.shoppingservice.util.Provider.PAGE;
import static com.sirnoob.shoppingservice.util.Provider.TEST;
import static com.sirnoob.shoppingservice.util.Provider.createInvoiceRandomValues;
import static com.sirnoob.shoppingservice.util.Provider.createItemRandomValues;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;

import com.sirnoob.shoppingservice.entity.Invoice;
import com.sirnoob.shoppingservice.entity.Item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

@DataJpaTest
class InvoiceRepositoryTest {

  @Autowired
  private IInvoiceRepository invoiceRepository;
  @Autowired
  private IItemRepository itemRepository;

  @Test
  public void save_ReturnInvoice_WhenSuccessful() {
    Invoice invoice = createInvoiceRandomValues();
    var savedInvoice = invoiceRepository.save(invoice);

    assertThat(savedInvoice.getInvoiceNumber()).isNotNull();
    assertThat(savedInvoice.getInvoiceNumber()).isEqualTo(invoice.getInvoiceNumber());
    assertThat(savedInvoice.getTotal()).isEqualTo(invoice.getTotal());
    assertThat(savedInvoice.getCustomer()).isEqualTo(invoice.getCustomer());
  }

  @Test
  public void save_ReturnInvoiceWithItems_WhenSuccessful() {
    Invoice invoice = createInvoiceRandomValues();

    Item item = createItemRandomValues();
    Item item2 = createItemRandomValues();
    Item item3 = createItemRandomValues();
    invoice.setItems(Set.of(item, item2, item3));

    var invoiceDB = invoiceRepository.save(invoice);

    assertThat(invoiceDB.getInvoiceNumber()).isNotNull();
    assertThat(invoiceDB).isEqualTo(invoice);
    assertTrue(invoiceDB.getItems().contains(item));
  }

  @Test
  public void save_ThrowDataIntegrityViolationException_WhenInvoiceIsEmpty() {
    Invoice invoice = new Invoice();

    assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> invoiceRepository.save(invoice));
  }

  @Test
  public void save_ThrowInvalidDataAccessApiUsageException_WhenInvoiceIsNull() {
    assertThatExceptionOfType(InvalidDataAccessApiUsageException.class).isThrownBy(() -> invoiceRepository.save(null));
  }

  @Test
  public void save_ThrowUnsupportedOperationException_WhenItemsAreAddedToAInvoiceAfterItHasBeenPersisted() {
    Invoice invoiceSaved = invoiceRepository.save(createInvoiceRandomValues());

    Item item = itemRepository.save(createItemRandomValues());
    Item item2 = itemRepository.save(createItemRandomValues());
    Item item3 = itemRepository.save(createItemRandomValues());

    invoiceSaved.setItems(Set.of(item, item2, item3));

    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> invoiceRepository.save(invoiceSaved));
  }

  @Test
  public void save_UpdateProduct_WhenSuccessful() {
    Invoice invoice = invoiceRepository.save(createInvoiceRandomValues());

    Long invoiceNumber = invoice.getInvoiceNumber();
    Double total = invoice.getTotal();

    invoice.setInvoiceNumber(100000L);
    invoice.setTotal(1.000);

    invoiceRepository.save(invoice);

    assertThat(invoice.getInvoiceNumber()).isNotEqualTo(invoiceNumber);
    assertThat(invoice.getTotal()).isNotEqualTo(total);
  }

  @Test
  public void findByInvoiceNumber_ReturnPresentOptional_WhenSuccessful() {
    Invoice invoice = invoiceRepository.save(createInvoiceRandomValues());
    var invoiceDB = invoiceRepository.findByInvoiceNumber(invoice.getInvoiceNumber());

    assertThat(invoiceDB.isPresent()).isTrue();
    assertThat(invoiceDB.get()).isEqualTo(invoice);
  }

  @Test
  public void findByInvoiceNumber_ReturnEmptyOptional_WhenInvoiceNumberNotMatches() {
    invoiceRepository.save(createInvoiceRandomValues());
    var invoice = invoiceRepository.findByInvoiceNumber(-1L);

    assertThat(invoice.isEmpty()).isTrue();
  }

  @Test
  public void findByInvoiceNumber_ReturnEmptyOptional_WhenInvoiceNumberIsNull(){
    var invoiceDB = invoiceRepository.findByInvoiceNumber(null);

    assertThat(invoiceDB.isEmpty()).isTrue();
  }

  @Test
  public void delete_RemoveProduct_WhenSuccessful() {
    Invoice invoice = invoiceRepository.save(createInvoiceRandomValues());
    invoiceRepository.delete(invoice);

    Optional<Invoice> invoiceOptional = invoiceRepository.findByInvoiceNumber(invoice.getInvoiceNumber());

    assertThat(invoice).isNotNull();
    assertThat(invoiceOptional.isEmpty()).isTrue();
  }

  @Test
  public void delete_ThrowInvalidDataAccessApiUsageException_WhenProductIsNull() {
    assertThatExceptionOfType(InvalidDataAccessApiUsageException.class).isThrownBy(() -> invoiceRepository.delete(null));
  }

  @Test
  public void findByCustomerName_ReturnInvoicePage_WhenSuccessful() {
    Invoice inovice = invoiceRepository.save(createInvoiceRandomValues());
    String name = inovice.getCustomer().getUserName();
    var page = invoiceRepository.findByCustomerUserName(name, PAGE);

    assertFalse(page.isEmpty());
    assertThat(page.getNumberOfElements()).isEqualTo(1);
  }

  @Test
  public void findByCustomerName_ReturnEmptyPage_WhenTheNamesNotMatches() {
    Invoice invoice = invoiceRepository.save(createInvoiceRandomValues());
    var page = invoiceRepository.findByCustomerUserName(invoice.getCustomer().getUserName() + TEST, PAGE);

    assertThat(page.isEmpty()).isTrue();
  }

  @Test
  public void findByCustomerName_ReturnEmptyPage_WhenUserNameIsNull(){
    var page = invoiceRepository.findByCustomerUserName(null, PAGE);

    assertThat(page.isEmpty()).isTrue();
  }

  @Test
  public void findByItemsProductBarCode_ReturnPageInvoice_WhenSuccessful() {
    Invoice invoice1 = createInvoiceRandomValues();
    Invoice invoice2 = createInvoiceRandomValues();
    Invoice invoice3 = createInvoiceRandomValues();

    Item item = createItemRandomValues();

    invoice1.setItems(Set.of(item));
    invoice2.setItems(Set.of(item));
    invoice3.setItems(Set.of(item));

    invoiceRepository.save(invoice1);
    invoiceRepository.save(invoice2);
    invoiceRepository.save(invoice3);

    var page = invoiceRepository.findByItemsProductBarCode(item.getProductBarCode(), PAGE);

    assertThat(page.isEmpty()).isFalse();
    assertThat(page.getNumberOfElements()).isEqualTo(3);
  }

  @Test
  public void findByItemsProductBarCode_ReturnEmptyPage_WhenProductBarCodeNotMatches() {
    invoiceRepository.save(createInvoiceRandomValues());
    invoiceRepository.save(createInvoiceRandomValues());
    invoiceRepository.save(createInvoiceRandomValues());

    Item item = itemRepository.save(createItemRandomValues());
    var page = invoiceRepository.findByItemsProductBarCode(item.getProductBarCode(), PAGE);

    assertThat(page.isEmpty()).isTrue();
  }

  @Test
  public void findByItemsProductBarCode_ReturnEmptyPage_WhenProductBarCodeIsNull(){
    var page = invoiceRepository.findByItemsProductBarCode(null, PAGE);

    assertThat(page.isEmpty()).isTrue();
  }

  @Test
  public void existsByInvoiceNumber_ReturnTrue_WhenSuccessful() {
    Invoice invoice = invoiceRepository.save(createInvoiceRandomValues());
    var res = invoiceRepository.existsByInvoiceNumber(invoice.getInvoiceNumber());

    assertTrue(res);
  }

  @Test
  public void existsByInvoiceNumber_ReturnFalse_WhenInvoiceNumberNotMatch() {
    var res = invoiceRepository.existsByInvoiceNumber(-1L);

    assertFalse(res);
  }

  @Test
  public void existsByInvoiceNumber_ReturnFalse_WhenInvoiceNumberIsNull(){
    var res = invoiceRepository.existsByInvoiceNumber(null);

    assertFalse(res);
  }
}
