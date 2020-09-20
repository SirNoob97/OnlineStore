package com.sirnoob.productservice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class ProductRepositoryTest {

  Logger log = LoggerFactory.getLogger(ProductRepositoryTest.class);

  @Autowired
  private IProductRepository iProductRepository;
  @Autowired
  private IMainCategoryRepository iMainCategoryRepository;

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
  @DisplayName("Save throw DataIntegrityViolationException when product barcode is null")
  public void save_ThrowDataIntegrityViolationException_WhenProductBarCodeIsNull() {
    Product product = new Product();

    Assertions.assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> iProductRepository.save(product));
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
  @DisplayName("Find By Product Name return empty optional product when successful")
  public void findByProductName_ReturnEmptyOptionalProduct_WhenSuccessful() {
    Product productSaved = iProductRepository.save(createProduct());

    Optional<Product> productOptional = iProductRepository.findByProductName(productSaved.getProductName() + "TEST");

    Assertions.assertThat(productOptional).isEmpty();
  }

  @Test
  @DisplayName("Find By Product Name Or Product Bar Code return product when successful")
  public void findByProductNameOrProductBarCode_ReturnProduct_WhenSuccessful() {
    Product productSaved = iProductRepository.save(createProduct());

    Optional<Product> productFetchedByName = iProductRepository.findByProductBarCodeOrProductName(-1L, productSaved.getProductName());
    Optional<Product> productFetchedByBarCode = iProductRepository.findByProductBarCodeOrProductName(productSaved.getProductBarCode(), "");

    Assertions.assertThat(productFetchedByName).isPresent();
    Assertions.assertThat(productFetchedByName.get()).isEqualTo(productSaved);
    Assertions.assertThat(productFetchedByBarCode).isPresent();
    Assertions.assertThat(productFetchedByBarCode.get()).isEqualTo(productSaved);
  }

  @Test
  @DisplayName("Find By Product Name Or Product Bar Code return empty optional product when successful")
  public void findByProductNameOrProductBarCode_ReturnEmptyOptionalProduct_WhenSuccessful() {
    iProductRepository.save(createProduct());

    Optional<Product> productFetchedByNameOrBarCode = iProductRepository.findByProductBarCodeOrProductName(-1L, " ");

    Assertions.assertThat(productFetchedByNameOrBarCode).isEmpty();
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


  private Product createProduct() {
    //@formatter:off
    return Product.builder()
                  .productId(getRandomLongNumber())
                  .productName(UUID.randomUUID().toString())
                  .productBarCode(getRandomLongNumber())
                  .productDescription(UUID.randomUUID().toString())
                  .productStock(new Random().nextInt(Integer.MAX_VALUE))
                  .productPrice(new Random().nextDouble() * (new Random().nextInt(100) + 1))
                  .productStatus(UUID.randomUUID().toString())
                  .createAt(LocalDate.now())
                  .build();
    //@formatter:on
  }

  private MainCategory createMainCategory(){
    //@formatter:off
    return MainCategory.builder()
                       .mainCategoryId(getRandomLongNumber())
                       .mainCategoryName(UUID.randomUUID().toString())
                       .build();
    //formatter:on
  }

  private Long getRandomLongNumber(){
    Long rn = new Random().nextLong();
    return rn > 0 ? rn : rn * -1;
  }
}
