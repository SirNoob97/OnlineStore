package com.sirnoob.productservice.controller;

import static com.sirnoob.productservice.util.RandomEntityGenerator.createMainCategoryStaticValues;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductInvoiceResponse;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductListView;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductRequest;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductResponseStaticValues;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductResponseStaticValuesForUpdateTest;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductViewStaticValues;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.List;
import java.util.Set;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.dto.ProductView;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.service.IMainCategoryService;
import com.sirnoob.productservice.service.IProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ProductControllerTest {

  @InjectMocks
  private ProductController productController;

  @Mock
  private IProductService iProductService;

  @Mock
  private IMainCategoryService iMainCategoryService;

  @BeforeEach
  public void setUp() {
    MainCategory mainCategory = createMainCategoryStaticValues();

    ProductResponse product = createProductResponseStaticValues();

    ProductResponse productUpdated = createProductResponseStaticValuesForUpdateTest();

    PageImpl<ProductListView> productPage = new PageImpl<>(List.of(createProductListView()));

    ProductView productView = createProductViewStaticValues();

    BDDMockito.when(iMainCategoryService.getMainCategoryByName(ArgumentMatchers.anyString())).thenReturn(mainCategory);

    BDDMockito.when(iProductService.createProduct(any(ProductRequest.class), any(MainCategory.class)))
        .thenReturn(product);

    BDDMockito.when(iProductService.updateProduct(anyLong(), any(ProductRequest.class), any(MainCategory.class)))
        .thenReturn(productUpdated);

    BDDMockito.doNothing().when(iProductService).updateProductStock(anyLong(), anyInt());

    BDDMockito.when(iProductService.getProductResponseByBarCodeOrProductName(anyLong(), anyString()))
        .thenReturn(product);

    BDDMockito.when(iProductService.getProductForInvoiceResponse(anyLong(), anyString()))
        .thenReturn(createProductInvoiceResponse());

    BDDMockito.doNothing().when(iProductService).deleteProductById(anyLong());

    BDDMockito.when(iProductService.getPageOfProductListView(any(Pageable.class))).thenReturn(productPage);

    BDDMockito.when(iProductService.getPageOfProductListViewByMainCategory(anyLong(), any(Pageable.class)))
        .thenReturn(productPage);

    BDDMockito.when(iProductService.getPageOfProductListViewByName(anyString(), any(Pageable.class)))
        .thenReturn(productPage);

    BDDMockito.when(iProductService.getSetOfProductListViewBySubCategory(any())).thenReturn(productPage.toSet());

    BDDMockito.when(iProductService.getProductViewByName(anyString())).thenReturn(productView);

    BDDMockito.when(iProductService.getPageOfProductListViewByName(anyString(), any(Pageable.class)))
        .thenReturn(productPage);
  }

  @Test
  @DisplayName("createProduct return a ProductResponse when successful")
  public void createProduct_ReturnAProductResponse_WhenSuccessful() {
    String expectedName = createProductResponseStaticValues().getProductName();

    ResponseEntity<ProductResponse> productResponse = productController.createProduct(createProductRequest());

    assertThat(productResponse).isNotNull();
    assertThat(productResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(productResponse.getBody()).isNotNull();
    assertThat(productResponse.getBody().getProductName()).isEqualTo(expectedName);
  }

  @Test
  @DisplayName("updateProduct return a ProductResponse when successful")
  public void updateProductReturnAProductResponseWhenSuccessful() {
    Long expectedBarCode = createProductResponseStaticValuesForUpdateTest().getProductBarCode();
    String expectedName = createProductResponseStaticValuesForUpdateTest().getProductName();
    Integer expectedStock = createProductResponseStaticValuesForUpdateTest().getProductStock();

    ResponseEntity<ProductResponse> productUpdated = productController.updateProduct(1L, createProductRequest());

    assertThat(productUpdated).isNotNull();
    assertThat(productUpdated.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(productUpdated.getBody()).isNotNull();
    assertThat(productUpdated.getBody().getProductBarCode()).isEqualTo(expectedBarCode);
    assertThat(productUpdated.getBody().getProductName()).isEqualTo(expectedName);
    assertThat(productUpdated.getBody().getProductStock()).isEqualTo(expectedStock);
  }

  @Test
  @DisplayName("updateProductStock update the stock of a product log when successful")
  public void updateProductStockUpdateTheStockOfAProductLogWhenSuccessful() {
    ResponseEntity<Void> responseEntity = productController.updateProductStock(1L, 1000);

    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(responseEntity.getBody()).isNull();
  }

  @Test
  @DisplayName("getProductResponse return a ProductResponse when successful")
  public void getProductResponse_ReturnAProductResponse_WhenSuccesful() {
    String expectedName = createProductResponseStaticValues().getProductName();
    ResponseEntity<ProductResponse> productResponse = productController.getProductResponse(0L, expectedName);

    ResponseEntity<ProductResponse> productResponse2 = productController.getProductResponse(1L, "");

    assertThat(productResponse).isNotNull();
    assertThat(productResponse2).isNotNull();
    assertThat(productResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(productResponse2.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(productResponse.getBody()).isNotNull();
    assertThat(productResponse2.getBody()).isNotNull();
    assertThat(productResponse.getBody().getProductName()).isEqualTo(expectedName);
    assertThat(productResponse2.getBody().getProductName()).isEqualTo(expectedName);
  }

  @Test
  @DisplayName("getProductForInvoice return a ProductInvoiceResponse when successful")
  public void getProductForInvoice_ResturnAProductInvoiceResponse_WhenSuccessful() {
    ResponseEntity<ProductInvoiceResponse> productInvoiceResponse = productController.getProductForInvoice(1L, "Name");

    assertThat(productInvoiceResponse).isNotNull();
    assertThat(productInvoiceResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(productInvoiceResponse.getBody()).isNotNull();
  }

  @Test
  @DisplayName("deleteProductById delete a product log when successful")
  public void deleteProductByIdDeleteAProductLogWhenSuccessful() {
    ResponseEntity<Void> responseEntity = productController.deleteProductById(1L);

    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(responseEntity.getBody()).isNull();
  }

  @Test
  @DisplayName("listAllProducts return a page of ProductListView when successful")
  public void listAllProducts_ReturnAPageOfProductListView_WhenSuccessful() {
    ResponseEntity<Page<ProductListView>> products = productController.listAllProducts(PageRequest.of(0, 10));

    assertThat(products).isNotNull();
    assertThat(products.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(products.getBody()).isNotNull();
    assertThat(products.getBody().isEmpty()).isFalse();
  }

  @Test
  @DisplayName("listProductsByMainCategoryId return a page of ProductListView when successful")
  public void listProductsByMainCategoryId_ReturnAPageOfProductListView_WhenSuccessful() {
    ResponseEntity<Page<ProductListView>> products = productController.listProductsByMainCategoryId(1L,
        PageRequest.of(0, 10));

    assertThat(products).isNotNull();
    assertThat(products.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(products.getBody()).isNotNull();
    assertThat(products.getBody().isEmpty()).isFalse();
  }

  @Test
  @DisplayName("listProductsBySubCategories return a set of ProductListView when successful")
  public void listProductsBySubCategories_ReturnASetOfProductListView_WhenSuccessful() {
    String[] subCategories = { "Sub Category" };

    ResponseEntity<Set<ProductListView>> products = productController.listProductsBySubCategories(subCategories);

    assertThat(products).isNotNull();
    assertThat(products.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(products.getBody()).isNotNull();
    assertThat(products.getBody().isEmpty()).isFalse();
  }

  @Test
  @DisplayName("getProductByProductName return a ProductView when successful")
  public void getProductByProductName_ReturnAProductView_WhenSuccessful() {
    ResponseEntity<ProductView> product = productController.getProductByProductName("name");

    assertThat(product).isNotNull();
    assertThat(product.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(product.getBody()).isNotNull();
  }

  @Test
  @DisplayName("listByProductNameCoincidences return a page of ProductListView when successful")
  public void listByProductNameCoincidences_ReturnAPageOfProductListView_WhenSuccessful() {
    ResponseEntity<Page<ProductListView>> products = productController.listByProductNameCoincidences("sa",
        PageRequest.of(0, 10));

    assertThat(products).isNotNull();
    assertThat(products.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(products.getBody()).isNotNull();
    assertThat(products.getBody().isEmpty()).isFalse();
  }
}
