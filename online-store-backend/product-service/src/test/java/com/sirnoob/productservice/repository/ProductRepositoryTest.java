package com.sirnoob.productservice.repository;

import static com.sirnoob.productservice.util.RandomEntityGenerator.createMainCategory;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProduct;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createSubCategory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;

@DataJpaTest
class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private MainCategoryRepository mainCategoryRepository;
  @Autowired
  private SubCategoryRepository subCategoryRepository;

  @Test
  public void save_PersistProduct_WhenSuccessful() {
    Product product = createProduct();
    Product savedProduct = productRepository.save(product);

    assertThat(savedProduct.getProductId()).isNotNull();
    assertThat(savedProduct.getProductId()).isNotEqualTo(product.getProductId());
    assertThat(savedProduct.getProductName()).isEqualTo(product.getProductName());
    assertThat(savedProduct.getProductStatus()).isEqualTo(product.getProductStatus());
  }

  @Test
  public void save_ThrowDataIntegrityViolationException_WhenProductIsEmpty() {
    Product product = new Product();

    assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> productRepository.save(product));
  }

  @Test
  public void save_ThrowInvalidDataAccessApiUsageException_WhenProductIsNull() {
    assertThatExceptionOfType(InvalidDataAccessApiUsageException.class).isThrownBy(() -> productRepository.save(null));
  }

  @Test
  public void save_ThrowConstraintViolationException_WhenMainCategoryIsNotSaved() {
    Product productSaved = createProduct();

    MainCategory mainCategory = createMainCategory();

    productSaved.setMainCategory(mainCategory);

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> productRepository.save(productSaved));
  }

  @Test
  public void save_ThrowUnsupportedOperationException_WhenSubCategoryIsAddedToAProductAfterItHasBeenPersisted() {
    Product productSaved = productRepository.save(createProduct());

    SubCategory subCategory = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory2 = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory3 = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());

    productSaved.setSubCategories(Set.of(subCategory, subCategory2, subCategory3));

    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> productRepository.save(productSaved));
  }

  @Test
  public void save_UpdateProduct_WhenSuccessful() {
    Product productToUpdate = productRepository.save(createProduct());

    String name = productToUpdate.getProductName();
    String status = productToUpdate.getProductStatus();

    productToUpdate.setProductName("Test");
    productToUpdate.setProductStatus("TEST");

    productRepository.save(productToUpdate);

    assertThat(productToUpdate.getProductId()).isNotNull();
    assertThat(productToUpdate.getProductName()).isNotEqualToIgnoringCase(name);
    assertThat(productToUpdate.getProductName()).isEqualTo(productToUpdate.getProductName());
    assertThat(productToUpdate.getProductStatus()).isNotEqualToIgnoringCase(status);
    assertThat(productToUpdate.getProductStatus()).isEqualTo(productToUpdate.getProductStatus());
  }

  @Test
  public void delete_RemoveProduct_WhenSuccessful() {
    Product productToDelete = productRepository.save(createProduct());

    productRepository.delete(productToDelete);

    var productOptional = productRepository.findById(productToDelete.getProductId());

    assertThat(productToDelete.getProductId()).isNotNull();
    assertThat(productOptional.isEmpty()).isTrue();
  }

  @Test
  public void delete_ThrowInvalidDataAccessApiUsageException_WhenProductIsNull() {
    assertThatExceptionOfType(InvalidDataAccessApiUsageException.class).isThrownBy(() -> productRepository.delete(null));
  }

  @Test
  public void findByProductName_ReturnProduct_WhenSuccessful() {
    Product productSaved = productRepository.save(createProduct());

    String name = productSaved.getProductName();

    var productOptional = productRepository.findByProductName(name);

    assertThat(productOptional.isPresent()).isTrue();
    assertThat(productOptional.get().getProductName()).isEqualTo(name);
  }

  @Test
  public void findByProductName_ReturnEmptyOptionalProduct_WhenTheNamesNotMatches() {
    Product productSaved = productRepository.save(createProduct());

    var productOptional = productRepository.findByProductName(productSaved.getProductName() + "TEST");

    assertThat(productOptional.isEmpty()).isTrue();
  }

  @Test
  public void findByProductNameOrProductBarCode_ReturnPresentOptionalProduct_WhenSuccessful() {
    Product productSaved = productRepository.save(createProduct());

    var productFetchedByName = productRepository.findByProductBarCodeOrProductName(-1L,
        productSaved.getProductName());
    var productFetchedByBarCode = productRepository
        .findByProductBarCodeOrProductName(productSaved.getProductBarCode(), "");

    assertThat(productFetchedByName.isPresent()).isTrue();
    assertThat(productFetchedByName.get()).isEqualTo(productSaved);
    assertThat(productFetchedByBarCode.isPresent()).isTrue();
    assertThat(productFetchedByBarCode.get()).isEqualTo(productSaved);
  }

  @Test
  public void findByProductNameOrProductBarCode_ReturnEmptyOptionalProduct_WhenNamesOrBarCodeNotMatches() {
    productRepository.save(createProduct());

    var productFetchedByNameOrBarCode = productRepository.findByProductBarCodeOrProductName(-1L, " ");

    assertThat(productFetchedByNameOrBarCode.isEmpty()).isTrue();
  }

  @Test
  public void findByMainCategory_ReturnProductList_WhenSuccessful() {
    Product product1 = productRepository.save(createProduct());
    Product product2 = productRepository.save(createProduct());
    Product product3 = productRepository.save(createProduct());

    MainCategory mainCategory = mainCategoryRepository.save(createMainCategory());

    product1.setMainCategory(mainCategory);
    product2.setMainCategory(mainCategory);
    product3.setMainCategory(mainCategory);

    productRepository.save(product1);
    productRepository.save(product2);
    productRepository.save(product3);

    var productsFetchedByMainCategory = productRepository.findByMainCategoryMainCategoryId(mainCategory.getMainCategoryId());

    int productCount = productsFetchedByMainCategory.size();

    assertThat(productsFetchedByMainCategory.isEmpty()).isFalse();
    assertThat(productsFetchedByMainCategory.size()).isEqualTo(productCount);
  }

  @Test
  public void findByMainCategory_ReturnEmptyProductList_WhenMainCategoryIsSavedButDoenstContainsProduct() {
    productRepository.save(createProduct());
    productRepository.save(createProduct());
    productRepository.save(createProduct());

    MainCategory mainCategorySaved = mainCategoryRepository.save(createMainCategory());

    var productsFetchedByMainCategory = productRepository.findByMainCategoryMainCategoryId(mainCategorySaved.getMainCategoryId());

    assertThat(productsFetchedByMainCategory.isEmpty()).isTrue();
  }

  @Test
  public void findByMainCategory_ReturnEmptyProductList_WhenMainCategoryIsNotSaved() {
    var productsFetchedByMainCategory = productRepository.findByMainCategoryMainCategoryId(createMainCategory().getMainCategoryId());

    assertThat(productsFetchedByMainCategory.isEmpty()).isTrue();
  }

  @Test
  public void findProductForInvoice_ReturnPresentProductInvoiceResponse_WhenSuccessful() {
    Product productSaved = productRepository.save(createProduct());

    var productInvoiceResponse = productRepository
        .findForInvoice(productSaved.getProductBarCode(), productSaved.getProductName());

    assertThat(productInvoiceResponse.isPresent()).isTrue();
    assertThat(productInvoiceResponse.get().getClass()).isEqualTo(ProductInvoiceResponse.class);
    assertThat(productInvoiceResponse.get().getClass().getFields().length)
        .isEqualTo(ProductInvoiceResponse.class.getFields().length);
  }

  @Test
  public void findProductForInvoice_ReturnEmptyProductInvoiceResponse_WhenNamesAndBarCodeNotMatches() {
    productRepository.save(createProduct());

    var productInvoiceResponse = productRepository.findForInvoice(0L, "");

    assertThat(productInvoiceResponse.isEmpty()).isTrue();
  }

  @Test
  public void getAll_ReturnPageOfProductListView_WhenSuccessful() {
    productRepository.save(createProduct());
    productRepository.save(createProduct());
    productRepository.save(createProduct());
    productRepository.save(createProduct());
    productRepository.save(createProduct());

    int size = 3;

    var pageOfProducts = productRepository.getAll(PageRequest.of(0, size));

    assertThat(pageOfProducts.isEmpty()).isFalse();
    assertThat(pageOfProducts.getSize()).isEqualTo(size);
    assertThat(pageOfProducts.getPageable().getPageNumber()).isEqualTo(0);
  }

  @Test
  public void getAll_ReturnEmptyPageOfProductListView_WhenThereAreNoRecordsInTheProductTable() {
    productRepository.deleteAllInBatch();
    var pageOfProducts = productRepository.getAll(PageRequest.of(0, 10));

    assertThat(pageOfProducts.isEmpty()).isTrue();
  }

  @Test
  public void listByName_ReturnPageOfProductListView_WhenTheProductNameMatchesTheSearchParameter() {
    productRepository.save(createProduct());
    productRepository.save(createProduct());
    productRepository.save(createProduct());
    productRepository.save(createProduct());
    productRepository.save(createProduct());
    productRepository.save(createProduct());
    productRepository.save(createProduct());
    productRepository.save(createProduct());
    productRepository.save(createProduct());
    productRepository.save(createProduct());

    int size = 5;

    var products = productRepository.findByProductNameContainingIgnoreCase("mo", PageRequest.of(0, 5));

    assertThat(products.isEmpty()).isFalse();
    assertThat(products.getSize()).isEqualTo(size);
    assertThat(products.getPageable().getPageNumber()).isEqualTo(0);
  }

  @Test
  public void listByName_ReturnEmptyPageOfProductListView_WhenTheProductNameNotMatchesTheSearchParameter() {
    var products = productRepository.findByProductNameContainingIgnoreCase("asdf", PageRequest.of(0, 5));

    assertThat(products.isEmpty()).isTrue();
  }

  @Test
  public void findByMainCategory_ReturnPageOfProductListView_WhenSuccessful() {
    Product product1 = productRepository.save(createProduct());
    Product product2 = productRepository.save(createProduct());
    Product product3 = productRepository.save(createProduct());

    MainCategory mainCategory = mainCategoryRepository.save(createMainCategory());

    product1.setMainCategory(mainCategory);
    product2.setMainCategory(mainCategory);
    product3.setMainCategory(mainCategory);

    productRepository.save(product1);
    productRepository.save(product2);
    productRepository.save(product3);

    var productsFetchedByMainCategory = productRepository.findByMainCategoryMainCategoryId(mainCategory.getMainCategoryId(),
        PageRequest.of(0, 10));

    int productCount = productsFetchedByMainCategory.getSize();

    assertThat(productsFetchedByMainCategory.isEmpty()).isFalse();
    assertThat(productsFetchedByMainCategory.getSize()).isEqualTo(productCount);
    assertThat(productsFetchedByMainCategory.getContent().get(0).getClass()).isEqualTo(ProductListView.class);
  }

  @Test
  public void findByMainCategory_ReturnEmptyProductListView_WhenMainCategoryIsNotSaved() {
    productRepository.save(createProduct());
    productRepository.save(createProduct());
    productRepository.save(createProduct());

    var productsFetchedByMainCategory = productRepository.findByMainCategoryMainCategoryId(createMainCategory().getMainCategoryId(),
        PageRequest.of(0, 10));

    assertThat(productsFetchedByMainCategory.isEmpty()).isTrue();
  }

  @Test
  public void findByMainCategory_ReturnEmptyProductListView_WhenMainCategoryIsSavedButDoesNotContainsProduct() {
    MainCategory mainCategory = mainCategoryRepository.save(createMainCategory());

    var productsFetchedByMainCategory = productRepository.findByMainCategoryMainCategoryId(mainCategory.getMainCategoryId(),
        PageRequest.of(0, 10));

    assertThat(productsFetchedByMainCategory.isEmpty()).isTrue();
  }

  @Test
  public void findBySubCategory_ReturnListOfProductListView_WhenSuccessful() {
    Product product1 = createProduct();
    Product product2 = createProduct();
    Product product3 = createProduct();
    Product product4 = createProduct();
    Product product5 = createProduct();

    SubCategory subCategory = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory2 = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory3 = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory4 = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());

    product1.setSubCategories(Set.of(subCategory, subCategory2));
    product2.setSubCategories(Set.of(subCategory, subCategory3));
    product3.setSubCategories(Set.of(subCategory, subCategory4));

    product4.setSubCategories(Set.of(subCategory2, subCategory4));
    product5.setSubCategories(Set.of(subCategory3, subCategory4));

    productRepository.save(product1);
    productRepository.save(product2);
    productRepository.save(product3);
    productRepository.save(product4);
    productRepository.save(product5);

    var productsFetchedBySubCategory = productRepository.findBySubCategory(subCategory);

    assertThat(productsFetchedBySubCategory.isEmpty()).isFalse();
    assertThat(productsFetchedBySubCategory.size()).isEqualTo(3);
  }

  @Test
  public void findBySubCategory_ReturnEmptyListOfProductListView_WhenNoProductContainsThatSubCategory() {
    Product product1 = createProduct();
    Product product2 = createProduct();
    Product product3 = createProduct();

    SubCategory subCategory = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory2 = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory3 = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory4 = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());

    product1.setSubCategories(Set.of(subCategory, subCategory2));
    product2.setSubCategories(Set.of(subCategory, subCategory3));
    product3.setSubCategories(Set.of(subCategory3, subCategory2));

    productRepository.save(product1);
    productRepository.save(product2);
    productRepository.save(product3);

    var productsFetchedBySubCategory = productRepository.findBySubCategory(subCategory4);

    assertThat(productsFetchedBySubCategory.isEmpty()).isTrue();
  }

  @Test
  public void findBySubCategory_ReturnEmptyListOfProductListView_WhenThatSubCategoryIsNotSaved() {
    Product product1 = createProduct();
    Product product2 = createProduct();
    Product product3 = createProduct();

    SubCategory subCategory = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory2 = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory3 = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());

    product1.setSubCategories(Set.of(subCategory, subCategory2));
    product2.setSubCategories(Set.of(subCategory, subCategory3));
    product3.setSubCategories(Set.of(subCategory3, subCategory2));

    productRepository.save(product1);
    productRepository.save(product2);
    productRepository.save(product3);

    var productsFetchedBySubCategory = productRepository.findBySubCategory(createSubCategory());

    assertThat(productsFetchedBySubCategory.isEmpty()).isTrue();
  }

  @Test
  public void updateProductStockByProductBarCode_ReturnAnIntegerGreaterThanZero_WhenSuccessful() {
    Product productSaved = productRepository.save(createProduct());

    Integer returnFromUpdateOperation = productRepository.updateStockByBarCode(100,
        productSaved.getProductBarCode());

    Product productWithUpdatedStock = productRepository.findById(productSaved.getProductId()).get();

    assertThat(returnFromUpdateOperation).isNotNull();
    assertThat(returnFromUpdateOperation).isGreaterThan(0);
    assertThat(productWithUpdatedStock.getProductStock()).isNotEqualTo(productSaved.getProductStock());
  }

  @Test
  public void updateProductStockByProductBarCode_ReturnZero_WhenNoProductHasThatBarCode() {
    Integer returnFromUpdateOperation = productRepository.updateStockByBarCode(100, -1L);

    assertThat(returnFromUpdateOperation).isNotNull();
    assertThat(returnFromUpdateOperation).isEqualTo(0);
  }

  @Test
  public void updateProductStockByProductBarCode_ReturnZero_WhenProductBarCodeIsNull() {
    Integer returnFromUpdateOperation = productRepository.updateStockByBarCode(100, null);

    assertThat(returnFromUpdateOperation).isNotNull();
    assertThat(returnFromUpdateOperation).isEqualTo(0);
  }

  private SubCategory createSubCategoryWithPersistedMainCategory(){
    SubCategory subCategory = createSubCategory();

    MainCategory mainCategory = mainCategoryRepository.save(createMainCategory());

    subCategory.setMainCategory(mainCategory);

    return subCategory;
  }
}
