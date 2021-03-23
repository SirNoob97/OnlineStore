package com.sirnoob.shoppingservice.repository;

import static com.sirnoob.shoppingservice.util.Provider.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.sirnoob.shoppingservice.entity.Invoice;
import com.sirnoob.shoppingservice.entity.Item;
import com.sirnoob.shoppingservice.model.Product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;

@DataJpaTest
class InvoiceRepositoryTest {

  @Autowired
  private IInvoiceRepository invoiceRepository;
  @Autowired
  private IItemRepository itemRepository;

  @Test
  public void save_PersistInvoice_WhenSuccessful() {
    Invoice invoice = createInvoiceRandomValues();
    var savedInvoice = invoiceRepository.save(invoice);

    assertThat(savedInvoice.getInvoiceNumber()).isNotNull();
    assertThat(savedInvoice.getInvoiceNumber()).isNotEqualTo(invoice.getInvoiceNumber());
    assertThat(savedInvoice.getTotal()).isEqualTo(invoice.getTotal());
    assertThat(savedInvoice.getCustomer()).isEqualTo(invoice.getCustomer());
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
  public void save_ThrowConstraintViolationException_WhenMainCategoryIsNotSaved() {
    Invoice invoice = createInvoiceRandomValues();
    invoice.setItems(Set.of(createItemRandomValues()));
    
    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> invoiceRepository.save(invoice));
  }

  @Test
  public void save_ThrowUnsupportedOperationException_WhenSubCategoryIsAddedToAProductAfterItHasBeenPersisted() {
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

  //@Test
  //public void findByProductName_ReturnProduct_WhenSuccessful() {
    //Product productSaved = invoiceRepository.save(createProduct());

    //String name = productSaved.getProductName();

    //Optional<Product> productOptional = invoiceRepository.findByProductName(name);

    //assertThat(productOptional.isPresent()).isTrue();
    //assertThat(productOptional.get().getProductName()).isEqualTo(name);
  //}

  //@Test
  //public void findByProductName_ReturnEmptyOptionalProduct_WhenTheNamesNotMatches() {
    //Product productSaved = invoiceRepository.save(createProduct());

    //Optional<Product> productOptional = invoiceRepository.findByProductName(productSaved.getProductName() + "TEST");

    //assertThat(productOptional.isEmpty()).isTrue();
  //}

  //@Test
  //public void findByProductNameOrProductBarCode_ReturnPresentOptionalProduct_WhenSuccessful() {
    //Product productSaved = invoiceRepository.save(createProduct());

    //Optional<Product> productFetchedByName = invoiceRepository.findByProductBarCodeOrProductName(-1L,
        //productSaved.getProductName());
    //Optional<Product> productFetchedByBarCode = invoiceRepository
        //.findByProductBarCodeOrProductName(productSaved.getProductBarCode(), "");

    //assertThat(productFetchedByName.isPresent()).isTrue();
    //assertThat(productFetchedByName.get()).isEqualTo(productSaved);
    //assertThat(productFetchedByBarCode.isPresent()).isTrue();
    //assertThat(productFetchedByBarCode.get()).isEqualTo(productSaved);
  //}

  //@Test
  //public void findByProductNameOrProductBarCode_ReturnEmptyOptionalProduct_WhenNamesOrBarCodeNotMatches() {
    //invoiceRepository.save(createProduct());

    //Optional<Product> productFetchedByNameOrBarCode = invoiceRepository.findByProductBarCodeOrProductName(-1L, " ");

    //assertThat(productFetchedByNameOrBarCode.isEmpty()).isTrue();
  //}

  //@Test
  //public void findByMainCategory_ReturnProductList_WhenSuccessful() {
    //Product product1 = invoiceRepository.save(createProduct());
    //Product product2 = invoiceRepository.save(createProduct());
    //Product product3 = invoiceRepository.save(createProduct());

    //CrudRepository<Invoice, Long> iMainCategoryRepository;
	//product1.setMainCategory(mainCategory);
    //product2.setMainCategory(mainCategory);
    //product3.setMainCategory(mainCategory);

    //invoiceRepository.save(product1);
    //invoiceRepository.save(product2);
    //invoiceRepository.save(product3);

    //List<Product> productsFetchedByMainCategory = invoiceRepository.findByMainCategoryMainCategoryId(mainCategory.getMainCategoryId());

    //int productCount = productsFetchedByMainCategory.size();

    //assertThat(productsFetchedByMainCategory.isEmpty()).isFalse();
    //assertThat(productsFetchedByMainCategory.size()).isEqualTo(productCount);
  //}

  //@Test
  //public void findByMainCategory_ReturnEmptyProductList_WhenMainCategoryIsSavedButDoenstContainsProduct() {
    //invoiceRepository.save(createProduct());
    //invoiceRepository.save(createProduct());
    //invoiceRepository.save(createProduct());

    //MainCategory mainCategorySaved = iMainCategoryRepository.save(createMainCategory());

    //List<Product> productsFetchedByMainCategory = invoiceRepository.findByMainCategoryMainCategoryId(mainCategorySaved.getMainCategoryId());

    //assertThat(productsFetchedByMainCategory.isEmpty()).isTrue();
  //}

  //@Test
  //public void findByMainCategory_ReturnEmptyProductList_WhenMainCategoryIsNotSaved() {
    //List<Product> productsFetchedByMainCategory = invoiceRepository.findByMainCategoryMainCategoryId(createMainCategory().getMainCategoryId());

    //assertThat(productsFetchedByMainCategory.isEmpty()).isTrue();
  //}

  //@Test
  //public void findProductForInvoice_ReturnPresentProductInvoiceResponse_WhenSuccessful() {
    //Product productSaved = invoiceRepository.save(createProduct());

    //Optional<ProductInvoiceResponse> productInvoiceResponse = invoiceRepository
        //.findProductForInvoice(productSaved.getProductBarCode(), productSaved.getProductName());

    //assertThat(productInvoiceResponse.isPresent()).isTrue();
    //assertThat(productInvoiceResponse.get().getClass()).isEqualTo(ProductInvoiceResponse.class);
    //assertThat(productInvoiceResponse.get().getClass().getFields().length)
        //.isEqualTo(ProductInvoiceResponse.class.getFields().length);
  //}

  //@Test
  //public void findProductForInvoice_ReturnEmptyProductInvoiceResponse_WhenNamesAndBarCodeNotMatches() {
    //invoiceRepository.save(createProduct());

    //Optional<ProductInvoiceResponse> productInvoiceResponse = invoiceRepository.findProductForInvoice(0L, "");

    //assertThat(productInvoiceResponse.isEmpty()).isTrue();
  //}

  //@Test
  //public void getAll_ReturnPageOfProductListView_WhenSuccessful() {
    //invoiceRepository.save(createProduct());
    //invoiceRepository.save(createProduct());
    //invoiceRepository.save(createProduct());
    //invoiceRepository.save(createProduct());
    //invoiceRepository.save(createProduct());

    //int size = 3;

    //Page<ProductListView> pageOfProducts = invoiceRepository.getAll(PageRequest.of(0, size));

    //assertThat(pageOfProducts.isEmpty()).isFalse();
    //assertThat(pageOfProducts.getSize()).isEqualTo(size);
    //assertThat(pageOfProducts.getPageable().getPageNumber()).isEqualTo(0);
  //}

  //@Test
  //public void getAll_ReturnEmptyPageOfProductListView_WhenThereAreNoRecordsInTheProductTable() {
    //invoiceRepository.deleteAllInBatch();
    //Page<ProductListView> pageOfProducts = invoiceRepository.getAll(PageRequest.of(0, 10));

    //assertThat(pageOfProducts.isEmpty()).isTrue();
  //}

  //@Test
  //public void listByName_ReturnPageOfProductListView_WhenTheProductNameMatchesTheSearchParameter() {
    //invoiceRepository.save(createProduct());
    //invoiceRepository.save(createProduct());
    //invoiceRepository.save(createProduct());
    //invoiceRepository.save(createProduct());
    //invoiceRepository.save(createProduct());
    //invoiceRepository.save(createProduct());
    //invoiceRepository.save(createProduct());
    //invoiceRepository.save(createProduct());
    //invoiceRepository.save(createProduct());
    //invoiceRepository.save(createProduct());

    //int size = 5;

    //Page<ProductListView> products = invoiceRepository.findByProductNameContainingIgnoreCase("mo", PageRequest.of(0, 5));

    //assertThat(products.isEmpty()).isFalse();
    //assertThat(products.getSize()).isEqualTo(size);
    //assertThat(products.getPageable().getPageNumber()).isEqualTo(0);
  //}

  //@Test
  //public void listByName_ReturnEmptyPageOfProductListView_WhenTheProductNameNotMatchesTheSearchParameter() {
    //Page<ProductListView> products = invoiceRepository.findByProductNameContainingIgnoreCase("asdf", PageRequest.of(0, 5));

    //assertThat(products.isEmpty()).isTrue();
  //}

  //@Test
  //public void findByMainCategory_ReturnPageOfProductListView_WhenSuccessful() {
    //Product product1 = invoiceRepository.save(createProduct());
    //Product product2 = invoiceRepository.save(createProduct());
    //Product product3 = invoiceRepository.save(createProduct());

    //MainCategory mainCategory = iMainCategoryRepository.save(createMainCategory());

    //product1.setMainCategory(mainCategory);
    //product2.setMainCategory(mainCategory);
    //product3.setMainCategory(mainCategory);

    //invoiceRepository.save(product1);
    //invoiceRepository.save(product2);
    //invoiceRepository.save(product3);

    //Page<ProductListView> productsFetchedByMainCategory = invoiceRepository.findByMainCategoryMainCategoryId(mainCategory.getMainCategoryId(),
        //PageRequest.of(0, 10));

    //int productCount = productsFetchedByMainCategory.getSize();

    //assertThat(productsFetchedByMainCategory.isEmpty()).isFalse();
    //assertThat(productsFetchedByMainCategory.getSize()).isEqualTo(productCount);
    //assertThat(productsFetchedByMainCategory.getContent().get(0).getClass()).isEqualTo(ProductListView.class);
  //}

  //@Test
  //public void findByMainCategory_ReturnEmptyProductListView_WhenMainCategoryIsNotSaved() {
    //invoiceRepository.save(createProduct());
    //invoiceRepository.save(createProduct());
    //invoiceRepository.save(createProduct());

    //Page<ProductListView> productsFetchedByMainCategory = invoiceRepository.findByMainCategoryMainCategoryId(createMainCategory().getMainCategoryId(),
        //PageRequest.of(0, 10));

    //assertThat(productsFetchedByMainCategory.isEmpty()).isTrue();
  //}

  //@Test
  //public void findByMainCategory_ReturnEmptyProductListView_WhenMainCategoryIsSavedButDoesNotContainsProduct() {
    //MainCategory mainCategory = iMainCategoryRepository.save(createMainCategory());

    //Page<ProductListView> productsFetchedByMainCategory = invoiceRepository.findByMainCategoryMainCategoryId(mainCategory.getMainCategoryId(),
        //PageRequest.of(0, 10));

    //assertThat(productsFetchedByMainCategory.isEmpty()).isTrue();
  //}

  //@Test
  //public void findBySubCategory_ReturnListOfProductListView_WhenSuccessful() {
    //Product product1 = createProduct();
    //Product product2 = createProduct();
    //Product product3 = createProduct();
    //Product product4 = createProduct();
    //Product product5 = createProduct();

    //SubCategory subCategory = itemRepository.save(createSubCategoryWithPersistedMainCategory());
    //SubCategory subCategory2 = itemRepository.save(createSubCategoryWithPersistedMainCategory());
    //SubCategory subCategory3 = itemRepository.save(createSubCategoryWithPersistedMainCategory());
    //SubCategory subCategory4 = itemRepository.save(createSubCategoryWithPersistedMainCategory());

    //product1.setSubCategories(Set.of(subCategory, subCategory2));
    //product2.setSubCategories(Set.of(subCategory, subCategory3));
    //product3.setSubCategories(Set.of(subCategory, subCategory4));

    //product4.setSubCategories(Set.of(subCategory2, subCategory4));
    //product5.setSubCategories(Set.of(subCategory3, subCategory4));

    //invoiceRepository.save(product1);
    //invoiceRepository.save(product2);
    //invoiceRepository.save(product3);
    //invoiceRepository.save(product4);
    //invoiceRepository.save(product5);

    //List<ProductListView> productsFetchedBySubCategory = invoiceRepository.findBySubCategory(subCategory);

    //assertThat(productsFetchedBySubCategory.isEmpty()).isFalse();
    //assertThat(productsFetchedBySubCategory.size()).isEqualTo(3);
  //}

  //@Test
  //public void findBySubCategory_ReturnEmptyListOfProductListView_WhenNoProductContainsThatSubCategory() {
    //Product product1 = createProduct();
    //Product product2 = createProduct();
    //Product product3 = createProduct();

    //SubCategory subCategory = itemRepository.save(createSubCategoryWithPersistedMainCategory());
    //SubCategory subCategory2 = itemRepository.save(createSubCategoryWithPersistedMainCategory());
    //SubCategory subCategory3 = itemRepository.save(createSubCategoryWithPersistedMainCategory());
    //SubCategory subCategory4 = itemRepository.save(createSubCategoryWithPersistedMainCategory());

    //product1.setSubCategories(Set.of(subCategory, subCategory2));
    //product2.setSubCategories(Set.of(subCategory, subCategory3));
    //product3.setSubCategories(Set.of(subCategory3, subCategory2));

    //invoiceRepository.save(product1);
    //invoiceRepository.save(product2);
    //invoiceRepository.save(product3);

    //List<ProductListView> productsFetchedBySubCategory = invoiceRepository.findBySubCategory(subCategory4);

    //assertThat(productsFetchedBySubCategory.isEmpty()).isTrue();
  //}

  //@Test
  //public void findBySubCategory_ReturnEmptyListOfProductListView_WhenThatSubCategoryIsNotSaved() {
    //Product product1 = createProduct();
    //Product product2 = createProduct();
    //Product product3 = createProduct();

    //SubCategory subCategory = itemRepository.save(createSubCategoryWithPersistedMainCategory());
    //SubCategory subCategory2 = itemRepository.save(createSubCategoryWithPersistedMainCategory());
    //SubCategory subCategory3 = itemRepository.save(createSubCategoryWithPersistedMainCategory());

    //product1.setSubCategories(Set.of(subCategory, subCategory2));
    //product2.setSubCategories(Set.of(subCategory, subCategory3));
    //product3.setSubCategories(Set.of(subCategory3, subCategory2));

    //invoiceRepository.save(product1);
    //invoiceRepository.save(product2);
    //invoiceRepository.save(product3);

    //List<ProductListView> productsFetchedBySubCategory = invoiceRepository.findBySubCategory(createSubCategory());

    //assertThat(productsFetchedBySubCategory.isEmpty()).isTrue();
  //}

  //@Test
  //public void updateProductStockByProductBarCode_ReturnAnIntegerGreaterThanZero_WhenSuccessful() {
    //Product productSaved = invoiceRepository.save(createProduct());

    //Integer returnFromUpdateOperation = invoiceRepository.updateProductStockByProductBarCode(100,
        //productSaved.getProductBarCode());

    //Product productWithUpdatedStock = invoiceRepository.findById(productSaved.getProductId()).get();

    //assertThat(returnFromUpdateOperation).isNotNull();
    //assertThat(returnFromUpdateOperation).isGreaterThan(0);
    //assertThat(productWithUpdatedStock.getProductStock()).isNotEqualTo(productSaved.getProductStock());
  //}

  //@Test
  //public void updateProductStockByProductBarCode_ReturnZero_WhenNoProductHasThatBarCode() {
    //Integer returnFromUpdateOperation = invoiceRepository.updateProductStockByProductBarCode(100, -1L);

    //assertThat(returnFromUpdateOperation).isNotNull();
    //assertThat(returnFromUpdateOperation).isEqualTo(0);
  //}

  //@Test
  //public void updateProductStockByProductBarCode_ReturnZero_WhenProductBarCodeIsNull() {
    //Integer returnFromUpdateOperation = invoiceRepository.updateProductStockByProductBarCode(100, null);

    //assertThat(returnFromUpdateOperation).isNotNull();
    //assertThat(returnFromUpdateOperation).isEqualTo(0);
  //}

  //private SubCategory createSubCategoryWithPersistedMainCategory(){
    //SubCategory subCategory = createSubCategory();

    //MainCategory mainCategory = iMainCategoryRepository.save(createMainCategory());

    //subCategory.setMainCategory(mainCategory);

    //return subCategory;
  //}
}
