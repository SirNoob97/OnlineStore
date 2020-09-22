package com.sirnoob.productservice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class ProductRepositoryTest {

  Logger log = LoggerFactory.getLogger(ProductRepositoryTest.class);

  @Autowired
  private IProductRepository iProductRepository;
  @Autowired
  private IMainCategoryRepository iMainCategoryRepository;
  @Autowired
  private ISubCategoryRepository iSubCategoryRepository;

  @Test
  @DisplayName("Save creates product when successful")
  public void save_PersistProduct_WhenSuccessful() {
    Product product = createProduct();
    Product savedProduct = iProductRepository.save(product);

    Assertions.assertThat(savedProduct.getProductId()).isNotNull();
    Assertions.assertThat(savedProduct.getProductName()).isEqualTo(product.getProductName());
    Assertions.assertThat(savedProduct.getProductStatus()).isEqualTo(product.getProductStatus());
  }

  @Test
  @DisplayName("Save throw DataIntegrityViolationException when product is empty")
  public void save_ThrowDataIntegrityViolationException_WhenProductIsEmpty() {
    Product product = new Product();

    Assertions.assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> iProductRepository.save(product));
  }

  @Test
  @DisplayName("Save throw ConstraintViolationException when Main Category is not saved")
  public void save_ThrowConstraintViolationException_WhenMainCategoryIsNotSaved() {
    Product productSaved = createProduct();

    MainCategory mainCategory = createMainCategory();

    productSaved.setMainCategory(mainCategory);

    Assertions.assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> iProductRepository.save(productSaved));
  }

  @Test
  @DisplayName("Save throw UnsupportedOperationException when subcategory is added to a product after it has been persisted")
  public void save_ThrowUnsupportedOperationException_WhenSubCategoryIsAddedToAProductAfterItHasBeenPersisted() {
    Product productSaved = iProductRepository.save(createProduct());

    SubCategory subCategory = iSubCategoryRepository.save(createSubCategory());
    SubCategory subCategory2 = iSubCategoryRepository.save(createSubCategory());
    SubCategory subCategory3 = iSubCategoryRepository.save(createSubCategory());

    productSaved.setSubCategories(Set.of(subCategory, subCategory2, subCategory3));

    Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> iProductRepository.save(productSaved));
  }

  @Test
  @DisplayName("Save Updates product when successful")
  public void save_UpdateProduct_WhenSuccessful() {
    Product productToUpdate = iProductRepository.save(createProduct());

    String name = productToUpdate.getProductName();
    String status = productToUpdate.getProductStatus();

    productToUpdate.setProductName("Test");
    productToUpdate.setProductStatus("TEST");

    iProductRepository.save(productToUpdate);

    Assertions.assertThat(productToUpdate.getProductId()).isNotNull();
    Assertions.assertThat(productToUpdate.getProductName()).isNotEqualToIgnoringCase(name);
    Assertions.assertThat(productToUpdate.getProductName()).isEqualTo(productToUpdate.getProductName());
    Assertions.assertThat(productToUpdate.getProductStatus()).isNotEqualToIgnoringCase(status);
    Assertions.assertThat(productToUpdate.getProductStatus()).isEqualTo(productToUpdate.getProductStatus());
  }

  @Test
  @DisplayName("Delete removes product when successful")
  public void delete_RemoveProduct_WhenSuccessful() {
    Product productToDelete = iProductRepository.save(createProduct());

    iProductRepository.delete(productToDelete);

    Optional<Product> productOptional = iProductRepository.findById(productToDelete.getProductId());

    Assertions.assertThat(productToDelete.getProductId()).isNotNull();
    Assertions.assertThat(productOptional.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Find By Product Name return product when successful")
  public void findByProductName_ReturnProduct_WhenSuccessful() {
    Product productSaved = iProductRepository.save(createProduct());

    String name = productSaved.getProductName();

    Optional<Product> productOptional = iProductRepository.findByProductName(name);

    Assertions.assertThat(productOptional.isPresent()).isTrue();
    Assertions.assertThat(productOptional.get().getProductName()).isEqualTo(name);
  }

  @Test
  @DisplayName("Find By Product Name return empty optional product when names not matches")
  public void findByProductName_ReturnEmptyOptionalProduct_WhenTheNamesNotMatches() {
    Product productSaved = iProductRepository.save(createProduct());

    Optional<Product> productOptional = iProductRepository.findByProductName(productSaved.getProductName() + "TEST");

    Assertions.assertThat(productOptional.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Find By Product Name Or Product Bar Code return product when successful")
  public void findByProductNameOrProductBarCode_ReturnPresentOptionalProduct_WhenSuccessful() {
    Product productSaved = iProductRepository.save(createProduct());

    Optional<Product> productFetchedByName = iProductRepository.findByProductBarCodeOrProductName(-1L, productSaved.getProductName());
    Optional<Product> productFetchedByBarCode = iProductRepository.findByProductBarCodeOrProductName(productSaved.getProductBarCode(), "");

    Assertions.assertThat(productFetchedByName.isPresent()).isTrue();
    Assertions.assertThat(productFetchedByName.get()).isEqualTo(productSaved);
    Assertions.assertThat(productFetchedByBarCode.isPresent()).isTrue();
    Assertions.assertThat(productFetchedByBarCode.get()).isEqualTo(productSaved);
  }

  @Test
  @DisplayName("Find By Product Name Or Product Bar Code return empty optional product when names or bar code not matches")
  public void findByProductNameOrProductBarCode_ReturnEmptyOptionalProduct_WhenNamesOrBarCodeNotMatches() {
    iProductRepository.save(createProduct());

    Optional<Product> productFetchedByNameOrBarCode = iProductRepository.findByProductBarCodeOrProductName(-1L, " ");

    Assertions.assertThat(productFetchedByNameOrBarCode.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Find By Main Category return a list of product when successful")
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

    List<Product> productsFetchedByMainCategory = iProductRepository.findByMainCategory(mainCategory);

    int productCount = productsFetchedByMainCategory.size();

    Assertions.assertThat(productsFetchedByMainCategory.isEmpty()).isFalse();
    Assertions.assertThat(productsFetchedByMainCategory.size()).isEqualTo(productCount);
  }

  @Test
  @DisplayName("Find By Main Category return a list of product when main category is saved but doenst contains product")
  public void findByMainCategory_ReturnEmptyProductList_WhenMainCategoryIsSavedButDoenstContainsProduct() {
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());

    MainCategory mainCategorySaved = iMainCategoryRepository.save(createMainCategory());

    List<Product> productsFetchedByMainCategory = iProductRepository.findByMainCategory(mainCategorySaved);

    Assertions.assertThat(productsFetchedByMainCategory.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Find By Main Category return empty list of product when main category isnt saved")
  public void findByMainCategory_ReturnEmptyProductList_WhenMainCategoryIsNotSaved() {
    List<Product> productsFetchedByMainCategory = iProductRepository.findByMainCategory(createMainCategory());

    Assertions.assertThat(productsFetchedByMainCategory.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Find product for invoice return product invoice response when successful")
  public void findProductForInvoice_ReturnPresentProductInvoiceResponse_WhenSuccessful() {
    Product productSaved = iProductRepository.save(createProduct());

    Optional<ProductInvoiceResponse> productInvoiceResponse = iProductRepository.findProductForInvoice(productSaved.getProductBarCode(), productSaved.getProductName());

    Assertions.assertThat(productInvoiceResponse.isPresent()).isTrue();
    Assertions.assertThat(productInvoiceResponse.get().getClass()).isEqualTo(ProductInvoiceResponse.class);
    Assertions.assertThat(productInvoiceResponse.get().getClass().getFields().length).isEqualTo(ProductInvoiceResponse.class.getFields().length);
  }

  @Test
  @DisplayName("Find product for invoice return product invoice response when names and bar code not matches")
  public void findProductForInvoice_ReturnEmptyProductInvoiceResponse_WhenNamesAndBarCodeNotMatches() {
    iProductRepository.save(createProduct());

    Optional<ProductInvoiceResponse> productInvoiceResponse = iProductRepository.findProductForInvoice(0L, "");

    Assertions.assertThat(productInvoiceResponse.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Get all return a page of product list view when successful")
  public void getAll_ReturnPageOfProductListView_WhenSuccessful() {
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());

    int size = 3;

    Page<ProductListView> pageOfProducts = iProductRepository.getAll(PageRequest.of(0, size));

    Assertions.assertThat(pageOfProducts.isEmpty()).isFalse();
    Assertions.assertThat(pageOfProducts.getSize()).isEqualTo(size);
    Assertions.assertThat(pageOfProducts.getPageable().getPageNumber()).isEqualTo(0);
  }

  @Test
  @DisplayName("Get all return empty page of product list view when there are no records in the product table")
  public void getAll_ReturnEmptyPageOfProductListView_WhenThereAreNoRecordsInTheProductTable() {
    iProductRepository.deleteAllInBatch();
    Page<ProductListView> pageOfProducts = iProductRepository.getAll(PageRequest.of(0, 10));

    Assertions.assertThat(pageOfProducts.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("List by name return page of ProductListView when the product name matches the search parameter")
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

    Page<ProductListView> pageOfProducts = iProductRepository.listByNameMatches("mo", PageRequest.of(0, size));

    Assertions.assertThat(pageOfProducts.isEmpty()).isFalse();
    Assertions.assertThat(pageOfProducts.getSize()).isEqualTo(size);
    Assertions.assertThat(pageOfProducts.getPageable().getPageNumber()).isEqualTo(0);
  }

  @Test
  @DisplayName("List by name return empty page of ProductListView when the product name not matches the search parameter")
  public void listByName_ReturnEmptyPageOfProductListView_WhenTheProductNameNotMatchesTheSearchParameter() {
    Page<ProductListView> pageOfProducts = iProductRepository.listByNameMatches("asdf", PageRequest.of(0, 10));

    Assertions.assertThat(pageOfProducts.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Find By Main Category return a page of productListView when successful")
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

    Page<ProductListView> productsFetchedByMainCategory = iProductRepository.findByMainCategory(mainCategory, PageRequest.of(0, 10));

    int productCount = productsFetchedByMainCategory.getSize();

    Assertions.assertThat(productsFetchedByMainCategory.isEmpty()).isFalse();
    Assertions.assertThat(productsFetchedByMainCategory.getSize()).isEqualTo(productCount);
    Assertions.assertThat(productsFetchedByMainCategory.getContent().get(0).getClass()).isEqualTo(ProductListView.class);
  }

  @Test
  @DisplayName("Find By Main Category return empty page of product when main category isnt saved")
  public void findByMainCategory_ReturnEmptyProductListView_WhenMainCategoryIsNotSaved() {
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());
    iProductRepository.save(createProduct());

    Page<ProductListView> productsFetchedByMainCategory = iProductRepository.findByMainCategory(createMainCategory(), PageRequest.of(0, 10));

    Assertions.assertThat(productsFetchedByMainCategory.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Find By Main Category return empty list of product when main category is saved but doenst contains product")
  public void findByMainCategory_ReturnEmptyProductListView_WhenMainCategoryIsSavedButDoesNotContainsProduct() {
    MainCategory mainCategory = iMainCategoryRepository.save(createMainCategory());

    Page<ProductListView> productsFetchedByMainCategory = iProductRepository.findByMainCategory(mainCategory, PageRequest.of(0, 10));

    Assertions.assertThat(productsFetchedByMainCategory.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Find By Sub Category return a list of productListView when successful")
  public void findBySubCategory_ReturnListOfProductListView_WhenSuccessful() {
    Product product1 = createProduct();
    Product product2 = createProduct();
    Product product3 = createProduct();
    Product product4 = createProduct();
    Product product5 = createProduct();

    SubCategory subCategory = iSubCategoryRepository.save(createSubCategory());
    SubCategory subCategory2 = iSubCategoryRepository.save(createSubCategory());
    SubCategory subCategory3 = iSubCategoryRepository.save(createSubCategory());
    SubCategory subCategory4 = iSubCategoryRepository.save(createSubCategory());

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

    Assertions.assertThat(productsFetchedBySubCategory.isEmpty()).isFalse();
    Assertions.assertThat(productsFetchedBySubCategory.size()).isEqualTo(3);
  }

  @Test
  @DisplayName("Find By Sub Category return empty list of productListView when no product contains that sub category")
  public void findBySubCategory_ReturnEmptyListOfProductListView_WhenNoProductContainsThatSubCategory() {
    Product product1 = createProduct();
    Product product2 = createProduct();
    Product product3 = createProduct();

    SubCategory subCategory = iSubCategoryRepository.save(createSubCategory());
    SubCategory subCategory2 = iSubCategoryRepository.save(createSubCategory());
    SubCategory subCategory3 = iSubCategoryRepository.save(createSubCategory());
    SubCategory subCategory4 = iSubCategoryRepository.save(createSubCategory());

    product1.setSubCategories(Set.of(subCategory, subCategory2));
    product2.setSubCategories(Set.of(subCategory, subCategory3));
    product3.setSubCategories(Set.of(subCategory3, subCategory2));

    iProductRepository.save(product1);
    iProductRepository.save(product2);
    iProductRepository.save(product3);

    List<ProductListView> productsFetchedBySubCategory = iProductRepository.findBySubCategory(subCategory4);

    Assertions.assertThat(productsFetchedBySubCategory.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Find By Sub Category return empty list of productListView when that sub category is not saved")
  public void findBySubCategory_ReturnEmptyListOfProductListView_WhenThatSubCategoryIsNotSaved() {
    Product product1 = createProduct();
    Product product2 = createProduct();
    Product product3 = createProduct();

    SubCategory subCategory = iSubCategoryRepository.save(createSubCategory());
    SubCategory subCategory2 = iSubCategoryRepository.save(createSubCategory());
    SubCategory subCategory3 = iSubCategoryRepository.save(createSubCategory());

    product1.setSubCategories(Set.of(subCategory, subCategory2));
    product2.setSubCategories(Set.of(subCategory, subCategory3));
    product3.setSubCategories(Set.of(subCategory3, subCategory2));

    iProductRepository.save(product1);
    iProductRepository.save(product2);
    iProductRepository.save(product3);

    List<ProductListView> productsFetchedBySubCategory = iProductRepository.findBySubCategory(createSubCategory());

    Assertions.assertThat(productsFetchedBySubCategory.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Update product stock by product barcode return an integer greater than zero when successful")
  public void updateProductStockByProductBarCode_ReturnAnIntegerGreaterThanZero_WhenSuccessful(){
    Product productSaved = iProductRepository.save(createProduct());

    Integer returnFromUpdateOperation = iProductRepository.updateProductStockByProductBarCode(100, productSaved.getProductBarCode());

    Product productWithUpdatedStock = iProductRepository.findById(productSaved.getProductId()).get();

    Assertions.assertThat(returnFromUpdateOperation).isNotNull();
    Assertions.assertThat(returnFromUpdateOperation).isGreaterThan(0);
    Assertions.assertThat(productWithUpdatedStock.getProductStock()).isNotEqualTo(productSaved.getProductStock());
  }

  @Test
  @DisplayName("Update product stock by product barcode return zero when no product has that barcode")
  public void updateProductStockByProductBarCode_ReturnZero_WhenNoProductHasThatBarCode(){
    Integer returnFromUpdateOperation = iProductRepository.updateProductStockByProductBarCode(100, -1L);

    Assertions.assertThat(returnFromUpdateOperation).isNotNull();
    Assertions.assertThat(returnFromUpdateOperation).isEqualTo(0);
  }

  @Test
  @DisplayName("Update product stock by product barcode return zero when no product has that barcode")
  public void updateProductStockByProductBarCode_ReturnZero_WhenProductBarCodeIsNull(){
    Integer returnFromUpdateOperation = iProductRepository.updateProductStockByProductBarCode(100, null);

    Assertions.assertThat(returnFromUpdateOperation).isNotNull();
    Assertions.assertThat(returnFromUpdateOperation).isEqualTo(0);
  }

  private Product createProduct() {
    //@formatter:off
    return Product.builder()
                  .productId(getRandomLongNumber())
                  .productName(getRandomString())
                  .productBarCode(getRandomLongNumber())
                  .productDescription(getRandomString())
                  .productStock(new Random().nextInt(Integer.MAX_VALUE))
                  .productPrice(new Random().nextDouble() * (new Random().nextInt(100) + 1))
                  .productStatus(getRandomString())
                  .createAt(LocalDate.now())
                  .build();
    //@formatter:on
  }

  private MainCategory createMainCategory(){
    //@formatter:off
    return MainCategory.builder()
                       .mainCategoryId(getRandomLongNumber())
                       .mainCategoryName(getRandomString())
                       .build();
    //formatter:on
  }

  private SubCategory createSubCategory(){
    //@formatter:off
    return SubCategory.builder()
                      .subCategoryId(getRandomLongNumber())
                      .subCategoryName(getRandomString())
                      .build();
    //@formatter:on
  }

  private Long getRandomLongNumber(){
    Long rn = new Random().nextLong();
    return rn > 0 ? rn : rn * -1;
  }

  private String getRandomString(){
    return UUID.randomUUID().toString().replaceAll("-", "");
  }
}
