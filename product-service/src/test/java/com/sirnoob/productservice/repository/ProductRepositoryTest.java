package com.sirnoob.productservice.repository;

import java.time.LocalDate;
import java.util.Optional;

import com.sirnoob.productservice.entity.Product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class ProductRepositoryTest {

  @Autowired
  private IProductRepository iProductRepository;

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
  @DisplayName("Save throw ConstraintViolationException when product barcode is null")
  public void save_ThrowConstraintViolationException_WhenProductBarCodeIsNull() {
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

    productToUpdate.setProductName("Product Update Test");
    productToUpdate.setProductStatus("UPDATE TEST");

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



  private Product createProduct() {
    //@formatter:off
    return Product.builder()
                  .productId(1L)
                  .productName("Product Repository Test")
                  .productBarCode(123456789123L)
                  .productDescription("Product Repository Test Description")
                  .productStock(100)
                  .productPrice(99.99)
                  .productStatus("TESTING")
                  .createAt(LocalDate.now())
                  .build();
    //@formatter:on
  }
}
