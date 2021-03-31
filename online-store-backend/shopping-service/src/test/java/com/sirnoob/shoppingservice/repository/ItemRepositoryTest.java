package com.sirnoob.shoppingservice.repository;

import static com.sirnoob.shoppingservice.util.Provider.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import com.sirnoob.shoppingservice.entity.Item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

@DataJpaTest
class ItemRepositoryTest {
  @Autowired
  private IInvoiceRepository invoiceRepository;
  @Autowired
  private IItemRepository itemRepository;

  @Test
  public void save_ReturnAnItem_WhenSuccessfull() {
    var item = createItemRandomValues();
    var itemDB = itemRepository.save(item);

    assertThat(item).isNotNull();
    assertThat(itemDB).isNotNull();
    assertThat(item.getProductBarCode()).isEqualTo(itemDB.getProductBarCode());
  }

  @Test
  public void save_ReturnUpdatedItem_WhenSuccessful() {
    var item = itemRepository.save(createItemRandomValues());
    item.setQuantity(1);
    item.setSubTotal(1.1);
    var itemDB = itemRepository.save(item);

    assertThat(item).isNotNull();
    assertThat(itemDB).isNotNull();
    assertThat(itemDB.getSubTotal()).isEqualTo(1.1);
    assertThat(itemDB.getQuantity()).isEqualTo(1);
  }

  @Test
  public void save_ThrowDataIntegrityViolationException_WhenItemIsEmpty() {
    assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> itemRepository.save(new Item()));
  }

  @Test
  public void save_ThrowInvalidDataAccessApiUsageException_WhenItemIsNull() {
    assertThatExceptionOfType(InvalidDataAccessApiUsageException.class).isThrownBy(() -> itemRepository.save(null));
  }

  @Test
  public void save_ReturnPersistedItemWithoutPersistTheInvoice_WhenSuccessful() {
    var item = createItemRandomValues();
    var invoice = createInvoiceRandomValues();

    item.setInvoices(Set.of(invoice));

    var itemDB = itemRepository.save(item);
    var invoiceDB = invoiceRepository.findByInvoiceNumber(invoice.getInvoiceNumber());

    assertThat(item).isNotNull();
    assertThat(invoice).isNotNull();
    assertThat(itemDB).isNotNull();
    assertTrue(invoiceDB.isEmpty());
  }

  @Test
  public void save_ThrowUnsupportedOperationException_WhenAddAnInvoiceToAPersistedItem() {
    var item = itemRepository.save(createItemRandomValues());
    var invoice = createInvoiceRandomValues();

    item.setInvoices(Set.of(invoice));

    assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> itemRepository.save(item));
  }

  @Test
  public void save_ThrowUnsupportedOperationException_WhenPersistedInvoiceIsAddedToAPersistedItem() {
    var item = itemRepository.save(createItemRandomValues());
    var invoice = invoiceRepository.save(createInvoiceRandomValues());
    item.setInvoices(Set.of(invoice));

    assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> itemRepository.save(item));
  }
}
