package com.sirnoob.productservice.service;

import static com.sirnoob.productservice.util.RandomEntityGenerator.createMainCategoryStaticValues;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductRequest;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createSubSetCategoryStaticValues;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.dto.ProductView;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.exception.ResourceNotFoundException;
import com.sirnoob.productservice.repository.IMainCategoryRepository;
import com.sirnoob.productservice.repository.IProductRepository;
import com.sirnoob.productservice.repository.ISubCategoryRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@DisplayName("Produc Service Test")
public class ProductServiceTest {

  private static final String PRODUCT_NOT_FOUND = "Product Not Found";
  private static final String NO_PRODUCTS_FOUND = "No Products Found";

  Logger log = LoggerFactory.getLogger(ProductServiceTest.class);

  @Autowired
  ISubCategoryRepository iSubCategoryRepository;

  @Autowired
  IMainCategoryRepository iMainCategoryRepository;

  @Autowired
  IProductRepository iProductRepository;

  @Autowired
  IProductService iProductService;

  @BeforeEach
  public void setUp() {
    MainCategory mainCategory = iMainCategoryRepository.save(createMainCategoryStaticValues());

    iSubCategoryRepository.save(createSubSetCategoryStaticValues(1, mainCategory));
    iSubCategoryRepository.save(createSubSetCategoryStaticValues(2, mainCategory));
  }

  @Test
  @DisplayName("createProduct create a new product log when successful")
  public void createProduct_CreateProduct_WhenSuccessful() {
    Optional<MainCategory> mainCategory = iMainCategoryRepository
      .findByMainCategoryName(createMainCategoryStaticValues().getMainCategoryName());

    ProductResponse productSaved = iProductService.createProduct(createProductRequest(), mainCategory.get());

    assertThat(productSaved).isNotNull();
    assertThat(productSaved.getProductStatus()).isEqualTo("CREATED");
  }

  @Test
  @DisplayName("updateProduct update an existing product when successful")
  public void updateProduct_UpdateAnExistingProduct_WhenSuccessful() {
    String newName = "TEST";
    Long newBarCode = 1000000000000L;

    ProductResponse productSaved = getAProductResponseFromAnAlreadyPersistedProduct();

    Optional<MainCategory> mainCategory = iMainCategoryRepository.findByMainCategoryName(productSaved.getMainCategoryName());

    ProductRequest productRequest = createProductRequest();

    productRequest.setProductBarCode(newBarCode);
    productRequest.setProductName(newName);

    ProductResponse productUpdated = iProductService.updateProduct(productSaved.getProductId(), productRequest,
      mainCategory.get());

    assertThat(productUpdated).isNotNull();
    assertThat(productUpdated).isNotEqualTo(productSaved);
    assertThat(productUpdated.getProductName()).isEqualTo(newName);
    assertThat(productUpdated.getProductBarCode()).isEqualTo(newBarCode);
  }

  @Test
  @DisplayName("getProductById return a product when successful")
  public void getProductById_ReturnAProduct_WhenSuccessful() {
    ProductResponse productSaved = getAProductResponseFromAnAlreadyPersistedProduct();

    Product productFetchedById = iProductService.getProductById(productSaved.getProductId());

    assertThat(productSaved).isNotNull();
    assertThat(productFetchedById).isNotNull();
    assertThat(productSaved.getProductBarCode()).isEqualTo(productFetchedById.getProductBarCode());
    assertThat(productSaved.getProductName()).isEqualTo(productFetchedById.getProductName());
  }

  @Test
  @DisplayName("getProductById throw ResourceNotFoundException when product was not found")
  public void getProductById_ThrowResourceNotFoundException_WhenProductWasNotFound() {
    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> iProductService.getProductById(-1L))
      .withMessage(PRODUCT_NOT_FOUND);
  }

  @Test
  @DisplayName("updateProductStock update the stock of an existing product when successful")
  public void updateProductStock_UpdateStockOfAnExistingProduct_WhenSuccessful() {
    ProductResponse productSaved = getAProductResponseFromAnAlreadyPersistedProduct();

    Integer oldStock = productSaved.getProductStock();
    Integer newStock = 100;

    iProductService.updateProductStock(productSaved.getProductBarCode(), newStock);

    Product productUpdated = iProductService.getProductById(productSaved.getProductId());

    assertThat(productUpdated).isNotNull();
    assertThat(productSaved.getProductStock()).isNotEqualTo(productUpdated.getProductStock());
    assertThat(productUpdated.getProductStock()).isNotEqualTo(oldStock);
    assertThat(productUpdated.getProductStock()).isEqualTo(newStock);
  }

  @Test
  @DisplayName("updateProductStock throw ResourceNotFoundException when the return of the query is less than one")
  public void updateProductStock_ThrowResourceNotFoundException_WhenTheReturnOfTheQueryIsLessThanOne() {
    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> iProductService.updateProductStock(-1L, 0))
      .withMessage(PRODUCT_NOT_FOUND);
  }

  @Test
  @DisplayName("deleteProductById removes a product when successful")
  public void deleteProductById_RemovesAProduct_WhenSuccessful() {
    ProductResponse productSaved = iProductService.createProduct(createProductRequest(), createMainCategoryStaticValues());

    Assertions.assertThatCode(() -> iProductService.deleteProductById(productSaved.getProductId())).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("getProductResponseByBarCodeOrProductName return a product response when successful")
  public void getProductResponseByBarCodeOrProductName_ReturnAProductResponse_WhenSuccessful() {
    ProductResponse productSaved = getAProductResponseFromAnAlreadyPersistedProduct();

    Long id = productSaved.getProductId();

    ProductResponse productFetchedByBarCode = iProductService
      .getProductResponseByBarCodeOrProductName(productSaved.getProductBarCode(), "");

    ProductResponse productFetchedByName = iProductService.getProductResponseByBarCodeOrProductName(-1L,
      productSaved.getProductName());

    assertThat(productSaved).isNotNull();
    assertThat(productFetchedByBarCode).isNotNull();
    assertThat(productFetchedByName).isNotNull();
    assertThat(productSaved).isEqualTo(productFetchedByBarCode);
    assertThat(productSaved).isEqualTo(productFetchedByName);
    assertThat(productFetchedByBarCode).isEqualTo(productFetchedByName);
    assertThat(productFetchedByBarCode.getProductId()).isEqualTo(id);
    assertThat(productFetchedByName.getProductId()).isEqualTo(id);
  }

  @Test
  @DisplayName("getProductResponseByBarCodeOrProductName throw ResourceNotFoundException when product was not found")
  public void getProductResponseByBarCodeOrProductName_ThrowResourceNotFoundException_WhenProductNotFound() {
    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iProductService.getProductResponseByBarCodeOrProductName(-1L, "")).withMessage(PRODUCT_NOT_FOUND);
  }

  @Test
  @DisplayName("getProductForInvoiceResponse return a product invoice response when successful")
  public void getProductForInvoiceResponse_ReturnAProductInvoiceResponse_WhenSuccessful() {
    ProductResponse productSaved = getAProductResponseFromAnAlreadyPersistedProduct();

    ProductInvoiceResponse productForInvoiceResponse = iProductService
      .getProductForInvoiceResponse(productSaved.getProductBarCode(), productSaved.getProductName());

    assertThat(productSaved).isNotNull();
    assertThat(productForInvoiceResponse).isNotNull();
    assertThat(productForInvoiceResponse.getProductName()).isEqualTo(productSaved.getProductName());
    assertThat(productForInvoiceResponse.getProductPrice()).isEqualTo(productSaved.getProductPrice());
  }

  @Test
  @DisplayName("getProductForInvoiceResponse throw ResourceNotFoundException when product was not found")
  public void getProductForInvoiceResponse_ThrowResourceNotFoundException_WhenProductNotFound() {
    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iProductService.getProductForInvoiceResponse(-1L, "")).withMessage(PRODUCT_NOT_FOUND);
  }

  @Test
  @DisplayName("getProductByMainCategory return a list of product when successful")
  public void getProductByMainCategory_ReturnAListOfProduct_WhenSuccessful() {
    ProductResponse productSaved = getAProductResponseFromAnAlreadyPersistedProduct();

    Optional<MainCategory> mainCategory = iMainCategoryRepository.findByMainCategoryName(productSaved.getMainCategoryName());

    List<Product> products = iProductService.getProductByMainCategory(mainCategory.get().getMainCategoryId());

    assertThat(productSaved).isNotNull();
    assertThat(products.isEmpty()).isFalse();
    assertThat(products.size()).isEqualTo(1);
  }

  @Test
  @DisplayName("getProductByMainCategory throw ResourceNotFoundException when the main category has no products")
  public void getProductByMainCategory_ThrowResourceNotFoundException_WhenTheMainCategoryHasNoProducts() {
    Optional<MainCategory> mainCategory = iMainCategoryRepository
      .findByMainCategoryName(createMainCategoryStaticValues().getMainCategoryName());

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iProductService.getProductByMainCategory(mainCategory.get().getMainCategoryId()))
      .withMessage(NO_PRODUCTS_FOUND);
  }

  @Test
  @DisplayName("findProductViewByName return a product view when successful")
  public void findProductViewByName_ReturnAProductView_WhenSuccessful() {
    ProductResponse productSaved = getAProductResponseFromAnAlreadyPersistedProduct();

    ProductView productView = iProductService.findProductViewByName(productSaved.getProductName());

    assertThat(productView).isNotNull();
    assertThat(productView.getProductBarCode()).isEqualTo(productSaved.getProductBarCode());
    assertThat(productView.getProductName()).isEqualTo(productSaved.getProductName());
    assertThat(productView.getMainCategoryName()).isEqualTo(productSaved.getMainCategoryName());
  }

  @Test
  @DisplayName("findProductViewByName throw ResourceNotFoundException when product was not found")
  public void findProductViewByName_ThrowResourceNotFoundException_WhenProductNotFound() {
    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> iProductService.findProductViewByName(""))
      .withMessage(PRODUCT_NOT_FOUND);
  }

  @Test
  @DisplayName("getProductListViewByName return a page of product list view when successful")
  public void getProductListViewByName_ReturnAPageProductListView_WhenSuccessful() {
    getAProductResponseFromAnAlreadyPersistedProduct();

    Page<ProductListView> products = iProductService.getPageOfProductListViewByName("sa", PageRequest.of(0, 10));

    assertThat(products.isEmpty()).isFalse();
    assertThat(products.getSize()).isGreaterThan(1);
  }

  @Test
  @DisplayName("getPageOfProductListViewByName throw ResourceNotFoundException when the names dont match")
  public void getPageOfProductListViewByName_ThrowResourceNotFoundException_WhenTheNamesDoNotMatch() {
    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iProductService.getPageOfProductListViewByName("?>~!#", PageRequest.of(0, 10)))
      .withMessage(NO_PRODUCTS_FOUND);
  }

  @Test
  @DisplayName("getPageOfProductListView return a page of product list view when successful")
  public void getPageOfProductListView_ReturnAPageProductListView_WhenSuccessful() {
    getAProductResponseFromAnAlreadyPersistedProduct();

    Page<ProductListView> products = iProductService.getPageOfProductListView(PageRequest.of(0, 10));

    assertThat(products.isEmpty()).isFalse();
    assertThat(products.getSize()).isGreaterThan(1);
  }

  @Test
  @DisplayName("getPageOfProductListView throw ResourceNotFoundException when there are no products in the registry")
  public void getPageOfProductListView_ThrowResourceNotFoundException_WhenThereAreNoProductsInTheRegistry() {
    iProductRepository.deleteAllInBatch();

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iProductService.getPageOfProductListView(PageRequest.of(0, 10))).withMessage(NO_PRODUCTS_FOUND);
  }

  @Test
  @DisplayName("getPageOfProductListViewByMainCategory return a page of product list view when successful")
  public void getPageOfProductListViewByMainCategory_ReturnAPageProductListView_WhenSuccessful() {
    ProductResponse productSaved = getAProductResponseFromAnAlreadyPersistedProduct();

    Optional<MainCategory> mainCategory = iMainCategoryRepository.findByMainCategoryName(productSaved.getMainCategoryName());

    Page<ProductListView> products = iProductService
      .getPageOfProductListViewByMainCategory(mainCategory.get().getMainCategoryId(), PageRequest.of(0, 10));

    assertThat(products.isEmpty()).isFalse();
    assertThat(products.getSize()).isGreaterThan(1);
  }

  @Test
  @DisplayName("getPageOfProductListViewByMainCategory throw ResourceNotFoundException when the main category has no products")
  public void getPageOfProductListViewByMainCategory_ThrowResourceNotFoundException_WhenTheMainCategoryHasNoProducts() {
    Optional<MainCategory> mainCategory = iMainCategoryRepository
      .findByMainCategoryName(createMainCategoryStaticValues().getMainCategoryName());

    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(
      () -> iProductService.getPageOfProductListViewByMainCategory(mainCategory.get().getMainCategoryId(), PageRequest.of(0, 10)))
      .withMessage(NO_PRODUCTS_FOUND);
  }

  @Test
  @DisplayName("getSetOfProductListViewBySubCategory return a set of product list view when successful")
  public void getSetOfProductListViewBySubCategory_ReturnASetProductListView_WhenSuccessful() {
    getAProductResponseFromAnAlreadyPersistedProduct();

    String[] subcategories = { "Sub Category 1" };

    Set<ProductListView> products = iProductService.getSetOfProductListViewBySubCategory(subcategories);

    assertThat(products.isEmpty()).isFalse();
    assertThat(products.size()).isEqualTo(1);
  }

  @Test
  @DisplayName("getSetOfProductListViewBySubCategory throw ResourceNotFoundException when the sub category or sub categories has no products")
  public void getSetOfProductListViewBySubCategory_ThrowResourceNotFoundException_WhenTheSubCategoryOrSubCategoriesHasNoProducts() {
    String[] subcategories = { "Sub Category 1", "Sub Category 2" };

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iProductService.getSetOfProductListViewBySubCategory(subcategories)).withMessage(NO_PRODUCTS_FOUND);
  }

  private ProductResponse getAProductResponseFromAnAlreadyPersistedProduct() {
    Optional<MainCategory> mainCategory = iMainCategoryRepository
      .findByMainCategoryName(createMainCategoryStaticValues().getMainCategoryName());

    return iProductService.createProduct(createProductRequest(), mainCategory.get());
  }
}
