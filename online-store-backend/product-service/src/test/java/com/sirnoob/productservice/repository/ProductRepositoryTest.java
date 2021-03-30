package com.sirnoob.productservice.repository;

import static com.sirnoob.productservice.util.RandomEntityGenerator.createMainCategory;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProduct;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createSubCategory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;

@DataJpaTest
class ProductRepositoryTest {

  @Autowired
  private IProductRepository iProductRepository;
  @Autowired
  private IMainCategoryRepository iMainCategoryRepository;
  @Autowired
  private ISubCategoryRepository iSubCategoryRepository;

  @Test
  public void save_PersistProduct_WhenSuccessful() {
    Product product = createProduct();
    Product savedProduct = iProductRepository.save(product);

    assertThat(savedProduct.getProductId()).isNotNull();
    assertThat(savedProduct.getProductId()).isNotEqualTo(product.getProductId());
    assertThat(savedProduct.getProductName()).isEqualTo(product.getProductName());
    assertThat(savedProduct.getProductStatus()).isEqualTo(product.getProductStatus());
  }

  @Test
  public void save_ThrowDataIntegrityViolationException_WhenProductIsEmpty() {
    Product product = new Product();

    assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> iProductRepository.save(product));
  }

  @Test
  public void save_ThrowInvalidDataAccessApiUsageException_WhenProductIsNull() {
    assertThatExceptionOfType(InvalidDataAccessApiUsageException.class).isThrownBy(() -> iProductRepository.save(null));
  }

  @Test
  public void save_ThrowConstraintViolationException_WhenMainCategoryIsNotSaved() {
    Product productSaved = createProduct();

    MainCategory mainCategory = createMainCategory();

    productSaved.setMainCategory(mainCategory);

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> iProductRepository.save(productSaved));
  }

  @Test
  public void save_ThrowUnsupportedOperationException_WhenSubCategoryIsAddedToAProductAfterItHasBeenPersisted() {
    Product productSaved = iProductRepository.save(createProduct());

    SubCategory subCategory = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory2 = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory3 = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());

    productSaved.setSubCategories(Set.of(subCategory, subCategory2, subCategory3));

    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> iProductRepository.save(productSaved));
  }

  @Test
  public void save_UpdateProduct_WhenSuccessful() {
    Product productToUpdate = iProductRepository.save(createProduct());

    String name = productToUpdate.getProductName();
    String status = productToUpdate.getProductStatus();

    productToUpdate.setProductName("Test");
    productToUpdate.setProductStatus("TEST");

    iProductRepository.save(productToUpdate);

    assertThat(productToUpdate.getProductId()).isNotNull();
    assertThat(productToUpdate.getProductName()).isNotEqualToIgnoringCase(name);
    assertThat(productToUpdate.getProductName()).isEqualTo(productToUpdate.getProductName());
    assertThat(productToUpdate.getProductStatus()).isNotEqualToIgnoringCase(status);
    assertThat(productToUpdate.getProductStatus()).isEqualTo(productToUpdate.getProductStatus());
  }

  @Test
  public void delete_RemoveProduct_WhenSuccessful() {
    Product productToDelete = iProductRepository.save(createProduct());

    iProductRepository.delete(productToDelete);

    Optional<Product> productOptional = iProductRepository.findById(productToDelete.getProductId());

    assertThat(productToDelete.getProductId()).isNotNull();
    assertThat(productOptional.isEmpty()).isTrue();
  }

  @Test
  public void delete_ThrowInvalidDataAccessApiUsageException_WhenProductIsNull() {
    assertThatExceptionOfType(InvalidDataAccessApiUsageException.class).isThrownBy(() -> iProductRepository.delete(null));
  }

  @Test
  public void findByProductName_ReturnProduct_WhenSuccessful() {
    Product productSaved = iProductRepository.save(createProduct());

    String name = productSaved.getProductName();

    Optional<Product> productOptional = iProductRepository.findByProductName(name);

    assertThat(productOptional.isPresent()).isTrue();
    assertThat(productOptional.get().getProductName()).isEqualTo(name);
  }

  @Test
  public void findByProductName_ReturnEmptyOptionalProduct_WhenTheNamesNotMatches() {
    Product productSaved = iProductRepository.save(createProduct());

    Optional<Product> productOptional = iProductRepository.findByProductName(productSaved.getProductName() + "TEST");

    assertThat(productOptional.isEmpty()).isTrue();
  }

  @Test
  public void findByProductNameOrProductBarCode_ReturnPresentOptionalProduct_WhenSuccessful() {
    Product productSaved = iProductRepository.save(createProduct());

    Optional<Product> productFetchedByName = iProductRepository.findByProductBarCodeOrProductName(-1L,
        productSaved.getProductName());
    Optional<Product> productFetchedByBarCode = iProductRepository
        .findByProductBarCodeOrProductName(productSaved.getProductBarCode(), "");

    assertThat(productFetchedByName.isPresent()).isTrue();
    assertThat(productFetchedByName.get()).isEqualTo(productSaved);
    assertThat(productFetchedByBarCode.isPresent()).isTrue();
    assertThat(productFetchedByBarCode.get()).isEqualTo(productSaved);
  }

  @Test
  public void findByProductNameOrProductBarCode_ReturnEmptyOptionalProduct_WhenNamesOrBarCodeNotMatches() {
    iProductRepository.save(createProduct());

    Optional<Product> productFetchedByNameOrBarCode = iProductRepository.findByProductBarCodeOrProductName(-1L, " ");

    assertThat(productFetchedByNameOrBarCode.isEmpty()).isTrue();
  }

  @Test
  public void findByMainCategory_ReturnProductList_WhenSuccessful() {
    Product product1 = iProductRepository.save(createProduct());
    Product product2 = iProductRepository.save(createProduct());
    Product product3 = iProductRepository.save(createProduct());

    MainCategory mainCategory = iMainCategoryRepository.save(createMainCategory());

    product1.setMainCategory(mainCategory);
    product2.setMainCategory(mainCategory);
    product3.setMainCategory(mainCategory);

    iProductRepository.save(product1);
    iProductRepository.save(product2);
    iProductRepository.save(product3);

    List<Product> productsFetchedByMainCategory = iProductRepository.findByMainCategoryMainCategoryId(mainCategory.getMainCategoryId());

    int productCount = productsFetchedByMainCategory.size();

    assertThat(productsFetchedByMainCategory.isEmpty()).isFalse();
    assertThat(productsFetchedByMainCategory.size()).isEqualTo(productCount);
  }

  @Test
  public void findByMainCategory_ReturnEmptyProductList_WhenMainCategoryIsSavedButDoenstContainsProduct() {
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());

    MainCategory mainCategorySaved = iMainCategoryRepository.save(createMainCategory());

    List<Product> productsFetchedByMainCategory = iProductRepository.findByMainCategoryMainCategoryId(mainCategorySaved.getMainCategoryId());

    assertThat(productsFetchedByMainCategory.isEmpty()).isTrue();
  }

  @Test
  public void findByMainCategory_ReturnEmptyProductList_WhenMainCategoryIsNotSaved() {
    List<Product> productsFetchedByMainCategory = iProductRepository.findByMainCategoryMainCategoryId(createMainCategory().getMainCategoryId());

    assertThat(productsFetchedByMainCategory.isEmpty()).isTrue();
  }

  @Test
  public void findProductForInvoice_ReturnPresentProductInvoiceResponse_WhenSuccessful() {
    Product productSaved = iProductRepository.save(createProduct());

    Optional<ProductInvoiceResponse> productInvoiceResponse = iProductRepository
        .findProductForInvoice(productSaved.getProductBarCode(), productSaved.getProductName());

    assertThat(productInvoiceResponse.isPresent()).isTrue();
    assertThat(productInvoiceResponse.get().getClass()).isEqualTo(ProductInvoiceResponse.class);
    assertThat(productInvoiceResponse.get().getClass().getFields().length)
        .isEqualTo(ProductInvoiceResponse.class.getFields().length);
  }

  @Test
  public void findProductForInvoice_ReturnEmptyProductInvoiceResponse_WhenNamesAndBarCodeNotMatches() {
    iProductRepository.save(createProduct());

    Optional<ProductInvoiceResponse> productInvoiceResponse = iProductRepository.findProductForInvoice(0L, "");

    assertThat(productInvoiceResponse.isEmpty()).isTrue();
  }

  @Test
  public void getAll_ReturnPageOfProductListView_WhenSuccessful() {
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());

    int size = 3;

    Page<ProductListView> pageOfProducts = iProductRepository.getAll(PageRequest.of(0, size));

    assertThat(pageOfProducts.isEmpty()).isFalse();
    assertThat(pageOfProducts.getSize()).isEqualTo(size);
    assertThat(pageOfProducts.getPageable().getPageNumber()).isEqualTo(0);
  }

  @Test
  public void getAll_ReturnEmptyPageOfProductListView_WhenThereAreNoRecordsInTheProductTable() {
    iProductRepository.deleteAllInBatch();
    Page<ProductListView> pageOfProducts = iProductRepository.getAll(PageRequest.of(0, 10));

    assertThat(pageOfProducts.isEmpty()).isTrue();
  }

  @Test
  public void listByName_ReturnPageOfProductListView_WhenTheProductNameMatchesTheSearchParameter() {
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());

    int size = 5;

    Page<ProductListView> products = iProductRepository.findByProductNameContainingIgnoreCase("mo", PageRequest.of(0, 5));

    assertThat(products.isEmpty()).isFalse();
    assertThat(products.getSize()).isEqualTo(size);
    assertThat(products.getPageable().getPageNumber()).isEqualTo(0);
  }

  @Test
  public void listByName_ReturnEmptyPageOfProductListView_WhenTheProductNameNotMatchesTheSearchParameter() {
    Page<ProductListView> products = iProductRepository.findByProductNameContainingIgnoreCase("asdf", PageRequest.of(0, 5));

    assertThat(products.isEmpty()).isTrue();
  }

  @Test
  public void findByMainCategory_ReturnPageOfProductListView_WhenSuccessful() {
    Product product1 = iProductRepository.save(createProduct());
    Product product2 = iProductRepository.save(createProduct());
    Product product3 = iProductRepository.save(createProduct());

    MainCategory mainCategory = iMainCategoryRepository.save(createMainCategory());

    product1.setMainCategory(mainCategory);
    product2.setMainCategory(mainCategory);
    product3.setMainCategory(mainCategory);

    iProductRepository.save(product1);
    iProductRepository.save(product2);
    iProductRepository.save(product3);

    Page<ProductListView> productsFetchedByMainCategory = iProductRepository.findByMainCategoryMainCategoryId(mainCategory.getMainCategoryId(),
        PageRequest.of(0, 10));

    int productCount = productsFetchedByMainCategory.getSize();

    assertThat(productsFetchedByMainCategory.isEmpty()).isFalse();
    assertThat(productsFetchedByMainCategory.getSize()).isEqualTo(productCount);
    assertThat(productsFetchedByMainCategory.getContent().get(0).getClass()).isEqualTo(ProductListView.class);
  }

  @Test
  public void findByMainCategory_ReturnEmptyProductListView_WhenMainCategoryIsNotSaved() {
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());

    Page<ProductListView> productsFetchedByMainCategory = iProductRepository.findByMainCategoryMainCategoryId(createMainCategory().getMainCategoryId(),
        PageRequest.of(0, 10));

    assertThat(productsFetchedByMainCategory.isEmpty()).isTrue();
  }

  @Test
  public void findByMainCategory_ReturnEmptyProductListView_WhenMainCategoryIsSavedButDoesNotContainsProduct() {
    MainCategory mainCategory = iMainCategoryRepository.save(createMainCategory());

    Page<ProductListView> productsFetchedByMainCategory = iProductRepository.findByMainCategoryMainCategoryId(mainCategory.getMainCategoryId(),
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

    SubCategory subCategory = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory2 = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory3 = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory4 = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());

    product1.setSubCategories(Set.of(subCategory, subCategory2));
    product2.setSubCategories(Set.of(subCategory, subCategory3));
    product3.setSubCategories(Set.of(subCategory, subCategory4));

    product4.setSubCategories(Set.of(subCategory2, subCategory4));
    product5.setSubCategories(Set.of(subCategory3, subCategory4));

    iProductRepository.save(product1);
    iProductRepository.save(product2);
    iProductRepository.save(product3);
    iProductRepository.save(product4);
    iProductRepository.save(product5);

    List<ProductListView> productsFetchedBySubCategory = iProductRepository.findBySubCategory(subCategory);

    assertThat(productsFetchedBySubCategory.isEmpty()).isFalse();
    assertThat(productsFetchedBySubCategory.size()).isEqualTo(3);
  }

  @Test
  public void findBySubCategory_ReturnEmptyListOfProductListView_WhenNoProductContainsThatSubCategory() {
    Product product1 = createProduct();
    Product product2 = createProduct();
    Product product3 = createProduct();

    SubCategory subCategory = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory2 = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory3 = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory4 = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());

    product1.setSubCategories(Set.of(subCategory, subCategory2));
    product2.setSubCategories(Set.of(subCategory, subCategory3));
    product3.setSubCategories(Set.of(subCategory3, subCategory2));

    iProductRepository.save(product1);
    iProductRepository.save(product2);
    iProductRepository.save(product3);

    List<ProductListView> productsFetchedBySubCategory = iProductRepository.findBySubCategory(subCategory4);

    assertThat(productsFetchedBySubCategory.isEmpty()).isTrue();
  }

  @Test
  public void findBySubCategory_ReturnEmptyListOfProductListView_WhenThatSubCategoryIsNotSaved() {
    Product product1 = createProduct();
    Product product2 = createProduct();
    Product product3 = createProduct();

    SubCategory subCategory = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory2 = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());
    SubCategory subCategory3 = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());

    product1.setSubCategories(Set.of(subCategory, subCategory2));
    product2.setSubCategories(Set.of(subCategory, subCategory3));
    product3.setSubCategories(Set.of(subCategory3, subCategory2));

    iProductRepository.save(product1);
    iProductRepository.save(product2);
    iProductRepository.save(product3);

    List<ProductListView> productsFetchedBySubCategory = iProductRepository.findBySubCategory(createSubCategory());

    assertThat(productsFetchedBySubCategory.isEmpty()).isTrue();
  }

  @Test
  public void updateProductStockByProductBarCode_ReturnAnIntegerGreaterThanZero_WhenSuccessful() {
    Product productSaved = iProductRepository.save(createProduct());

    Integer returnFromUpdateOperation = iProductRepository.updateProductStockByProductBarCode(100,
        productSaved.getProductBarCode());

    Product productWithUpdatedStock = iProductRepository.findById(productSaved.getProductId()).get();

    assertThat(returnFromUpdateOperation).isNotNull();
    assertThat(returnFromUpdateOperation).isGreaterThan(0);
    assertThat(productWithUpdatedStock.getProductStock()).isNotEqualTo(productSaved.getProductStock());
  }

  @Test
  public void updateProductStockByProductBarCode_ReturnZero_WhenNoProductHasThatBarCode() {
    Integer returnFromUpdateOperation = iProductRepository.updateProductStockByProductBarCode(100, -1L);

    assertThat(returnFromUpdateOperation).isNotNull();
    assertThat(returnFromUpdateOperation).isEqualTo(0);
  }

  @Test
  public void updateProductStockByProductBarCode_ReturnZero_WhenProductBarCodeIsNull() {
    Integer returnFromUpdateOperation = iProductRepository.updateProductStockByProductBarCode(100, null);

    assertThat(returnFromUpdateOperation).isNotNull();
    assertThat(returnFromUpdateOperation).isEqualTo(0);
  }

  private SubCategory createSubCategoryWithPersistedMainCategory(){
    SubCategory subCategory = createSubCategory();

    MainCategory mainCategory = iMainCategoryRepository.save(createMainCategory());

    subCategory.setMainCategory(mainCategory);

    return subCategory;
  }
}
