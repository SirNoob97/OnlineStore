package com.sirnoob.productservice.service;

import static com.sirnoob.productservice.util.RandomEntityGenerator.createMainCategoryStaticValues;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductInvoiceResponse;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductListViewStaticValues;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductRequest;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductResponseStaticValues;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductViewStaticValues;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductWithMainCategoryAndSubCategory;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createSubCategory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.dto.ProductView;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;
import com.sirnoob.productservice.exception.ResourceNotFoundException;
import com.sirnoob.productservice.mapper.IProductMapper;
import com.sirnoob.productservice.repository.IProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@DisplayName("Produc Service Test")
public class ProductServiceTest {

  private static final String PRODUCT_NOT_FOUND = "Product Not Found";
  private static final String NO_PRODUCTS_FOUND = "No Products Found";
  private static final String PRODUCT = "Product";

  @Mock
  ISubCategoryService iSubCategoryService;

  @Mock
  IProductRepository iProductRepository;

  @Mock
  IProductMapper iProductMapper;

  IProductService iProductService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    iProductService = new ProductServiceImpl(iSubCategoryService, iProductRepository, iProductMapper);

    Set<SubCategory> subCategories = Set.of(createSubCategory(), createSubCategory());

    Product product = createProductWithMainCategoryAndSubCategory();

    ProductResponse productResponse = createProductResponseStaticValues();

    ProductInvoiceResponse productForInvoiceResponse = createProductInvoiceResponse();

    ProductView productView = createProductViewStaticValues();

    ProductListView productListView = createProductListViewStaticValues();

    PageImpl<ProductListView> productListViewPage = new PageImpl<>(List.of(productListView));

    BDDMockito.when(iSubCategoryService.getSubcategoriesByName(any())).thenReturn(subCategories);

    BDDMockito.when(iProductMapper.mapProductRequestToProduct(any(ProductRequest.class), any(MainCategory.class), anySet())).thenReturn(product);

    BDDMockito.when(iProductRepository.save(any(Product.class))).thenReturn(product);

    BDDMockito.when(iProductMapper.mapProductToProductResponse(any(Product.class))).thenReturn(productResponse);

    BDDMockito.when(iProductRepository.updateProductStockByProductBarCode(anyInt(), anyLong())).thenReturn(1);

    BDDMockito.doNothing().when(iProductRepository).delete(any(Product.class));

    BDDMockito.when(iProductRepository.findByProductBarCodeOrProductName(anyLong(), anyString())).thenReturn(Optional.of(product));

    BDDMockito.when(iProductRepository.findProductForInvoice(anyLong(), anyString())).thenReturn(Optional.of(productForInvoiceResponse));

    BDDMockito.when(iProductRepository.findById(anyLong())).thenReturn(Optional.of(product));

    BDDMockito.when(iProductRepository.findByMainCategoryMainCategoryId(anyLong())).thenReturn(List.of(product));

    BDDMockito.when(iProductRepository.findByProductName(anyString())).thenReturn(Optional.of(product));

    BDDMockito.when(iProductMapper.mapProductToProductView(any(Product.class))).thenReturn(productView);

    BDDMockito.when(iProductRepository.findByProductNameContainingIgnoreCase(anyString(), any(Pageable.class))).thenReturn(productListViewPage);

    BDDMockito.when(iProductRepository.getAll(any(Pageable.class))).thenReturn(productListViewPage);

    BDDMockito.when(iProductRepository.findByMainCategoryMainCategoryId(anyLong(), any(Pageable.class))).thenReturn(productListViewPage);

    BDDMockito.when(iSubCategoryService.getSubcategoriesByName(any())).thenReturn(subCategories);

    BDDMockito.when(iProductRepository.findBySubCategory(any(SubCategory.class))).thenReturn(List.of(productListView));
  }

  @Test
  @DisplayName("createProduct create a new product log when successful")
  public void createProduct_CreateProduct_WhenSuccessful() {
    ProductResponse productSaved = iProductService.createProduct(createProductRequest(), createMainCategoryStaticValues());

    assertThat(productSaved).isNotNull();
    assertThat(productSaved.getProductStatus()).isEqualTo("CREATED");
    assertThat(productSaved.getMainCategoryName()).isNotNull();
    assertThat(productSaved.getSubCategories()).isNotNull();
    assertThat(productSaved.getSubCategories().isEmpty()).isNotNull();
  }

  @Test
  @DisplayName("updateProduct update an existing product when successful")
  public void updateProduct_UpdateAnExistingProduct_WhenSuccessful() {
    ProductResponse productUpdated = iProductService.updateProduct(1L, createProductRequest(), createMainCategoryStaticValues());

    assertThat(productUpdated).isNotNull();
    assertThat(productUpdated.getMainCategoryName()).isNotNull();
    assertThat(productUpdated.getSubCategories()).isNotNull();
    assertThat(productUpdated.getSubCategories().isEmpty()).isNotNull();
  }

  @Test
  @DisplayName("updateProductStock update the stock of an existing product when successful")
  public void updateProductStock_UpdateStockOfAnExistingProduct_WhenSuccessful() {
    assertThatCode(() -> iProductService.updateProductStock(1L, 100)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("updateProductStock throw ResourceNotFoundException when the return of the query is less than one")
  public void updateProductStock_ThrowResourceNotFoundException_WhenTheReturnOfTheQueryIsLessThanOne() {
    BDDMockito.when(iProductRepository.updateProductStockByProductBarCode(anyInt(), anyLong()))
        .thenThrow(new ResourceNotFoundException(PRODUCT_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> iProductService.updateProductStock(-1L, 0))
      .withMessage(PRODUCT_NOT_FOUND);
  }

  @Test
  @DisplayName("deleteProductById removes a product when successful")
  public void deleteProductById_RemovesAProduct_WhenSuccessful() {
    assertThatCode(() -> iProductService.deleteProductById(1L)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("getProductResponseByBarCodeOrProductName return a product response when successful")
  public void getProductResponseByBarCodeOrProductName_ReturnAProductResponse_WhenSuccessful() {
    ProductResponse productFetchedByName = iProductService.getProductResponseByBarCodeOrProductName(-1L,
      PRODUCT);

    assertThat(productFetchedByName).isNotNull();
    assertThat(productFetchedByName.getMainCategoryName()).isNotNull();
    assertThat(productFetchedByName.getSubCategories()).isNotNull();
  }

  @Test
  @DisplayName("getProductResponseByBarCodeOrProductName throw ResourceNotFoundException when product was not found")
  public void getProductResponseByBarCodeOrProductName_ThrowResourceNotFoundException_WhenProductNotFound() {
    BDDMockito.when(iProductRepository.findByProductBarCodeOrProductName(anyLong(), anyString()))
        .thenThrow(new ResourceNotFoundException(PRODUCT_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iProductService.getProductResponseByBarCodeOrProductName(-1L, "")).withMessage(PRODUCT_NOT_FOUND);
  }

  @Test
  @DisplayName("getProductForInvoiceResponse return a product invoice response when successful")
  public void getProductForInvoiceResponse_ReturnAProductInvoiceResponse_WhenSuccessful() {
    ProductInvoiceResponse productForInvoiceResponse = iProductService
      .getProductForInvoiceResponse(1L, PRODUCT);

    assertThat(productForInvoiceResponse).isNotNull();
    assertThat(productForInvoiceResponse.getProductName()).isNotNull();
    assertThat(productForInvoiceResponse.getProductPrice()).isNotNull();
  }

  @Test
  @DisplayName("getProductForInvoiceResponse throw ResourceNotFoundException when product was not found")
  public void getProductForInvoiceResponse_ThrowResourceNotFoundException_WhenProductNotFound() {
    BDDMockito.when(iProductRepository.findProductForInvoice(anyLong(), anyString()))
        .thenThrow(new ResourceNotFoundException(PRODUCT_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iProductService.getProductForInvoiceResponse(-1L, "")).withMessage(PRODUCT_NOT_FOUND);
  }

  @Test
  @DisplayName("getProductById return a product when successful")
  public void getProductById_ReturnAProduct_WhenSuccessful() {
    Product productFetchedById = iProductService.getProductById(1L);

    assertThat(productFetchedById).isNotNull();
    assertThat(productFetchedById.getMainCategory()).isNotNull();
    assertThat(productFetchedById.getSubCategories()).isNotNull();
  }

  @Test
  @DisplayName("getProductById throw ResourceNotFoundException when product was not found")
  public void getProductById_ThrowResourceNotFoundException_WhenProductWasNotFound() {
    BDDMockito.when(iProductRepository.findById(anyLong())).thenThrow(new ResourceNotFoundException(PRODUCT_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> iProductService.getProductById(-1L))
      .withMessage(PRODUCT_NOT_FOUND);
  }

  @Test
  @DisplayName("getProductByMainCategory return a list of product when successful")
  public void getProductByMainCategory_ReturnAListOfProduct_WhenSuccessful() {
    List<Product> products = iProductService.getProductByMainCategory(1L);

    assertThat(products).isNotNull();
    assertThat(products.isEmpty()).isFalse();
  }

  @Test
  @DisplayName("getProductByMainCategory return an empty product list when the main category has no products")
  public void getProductByMainCategory_ReturnAnEmptyProductList_WhenTheMainCategoryHasNoProducts() {
    BDDMockito.when(iProductRepository.findByMainCategoryMainCategoryId(anyLong())).thenReturn(List.of());

    List<Product> products = iProductService.getProductByMainCategory(1L);

    assertThat(products).isNotNull();
    assertThat(products.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("findProductViewByName return a product view when successful")
  public void findProductViewByName_ReturnAProductView_WhenSuccessful() {
    ProductView productView = iProductService.getProductViewByName(PRODUCT);

    assertThat(productView).isNotNull();
    assertThat(productView.getMainCategoryName()).isNotNull();
    assertThat(productView.getSubCategories()).isNotNull();
  }

  @Test
  @DisplayName("findProductViewByName throw ResourceNotFoundException when product was not found")
  public void findProductViewByName_ThrowResourceNotFoundException_WhenProductNotFound() {
    BDDMockito.when(iProductRepository.findByProductName(anyString())).thenThrow(new ResourceNotFoundException(PRODUCT_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> iProductService.getProductViewByName(""))
      .withMessage(PRODUCT_NOT_FOUND);
  }

  @Test
  @DisplayName("getProductListViewByName return a page of product list view when successful")
  public void getProductListViewByName_ReturnAPageProductListView_WhenSuccessful() {
    Page<ProductListView> products = iProductService.getPageOfProductListViewByName("sa", PageRequest.of(0, 10));

    assertThat(products).isNotNull();
    assertThat(products.isEmpty()).isFalse();
  }

  @Test
  @DisplayName("getPageOfProductListViewByName throw ResourceNotFoundException when the names dont match")
  public void getPageOfProductListViewByName_ThrowResourceNotFoundException_WhenTheNamesDoNotMatch() {
    BDDMockito.when(iProductRepository.findByProductNameContainingIgnoreCase(anyString(), any(Pageable.class)))
        .thenThrow(new ResourceNotFoundException(NO_PRODUCTS_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iProductService.getPageOfProductListViewByName("?>~!#", PageRequest.of(0, 10)))
      .withMessage(NO_PRODUCTS_FOUND);
  }

  @Test
  @DisplayName("getPageOfProductListView return a page of product list view when successful")
  public void getPageOfProductListView_ReturnAPageProductListView_WhenSuccessful() {
    Page<ProductListView> products = iProductService.getPageOfProductListView(PageRequest.of(0, 10));

    assertThat(products).isNotNull();
    assertThat(products.isEmpty()).isFalse();
  }

  @Test
  @DisplayName("getPageOfProductListView throw ResourceNotFoundException when there are no products in the registry")
  public void getPageOfProductListView_ThrowResourceNotFoundException_WhenThereAreNoProductsInTheRegistry() {
    BDDMockito.when(iProductRepository.getAll(any(Pageable.class))).thenThrow(new ResourceNotFoundException(NO_PRODUCTS_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iProductService.getPageOfProductListView(PageRequest.of(0, 10))).withMessage(NO_PRODUCTS_FOUND);
  }

  @Test
  @DisplayName("getPageOfProductListViewByMainCategory return a page of product list view when successful")
  public void getPageOfProductListViewByMainCategory_ReturnAPageProductListView_WhenSuccessful() {
    Page<ProductListView> products = iProductService.getPageOfProductListViewByMainCategory(1L, PageRequest.of(0, 10));

    assertThat(products).isNotNull();
    assertThat(products.isEmpty()).isFalse();
  }

  @Test
  @DisplayName("getPageOfProductListViewByMainCategory throw ResourceNotFoundException when the main category has no products")
  public void getPageOfProductListViewByMainCategory_ThrowResourceNotFoundException_WhenTheMainCategoryHasNoProducts() {
    BDDMockito.when(iProductRepository.findByMainCategoryMainCategoryId(anyLong(), any(Pageable.class)))
        .thenThrow(new ResourceNotFoundException(NO_PRODUCTS_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
        .isThrownBy(() -> iProductService.getPageOfProductListViewByMainCategory(1L, PageRequest.of(0, 10)))
        .withMessage(NO_PRODUCTS_FOUND);
  }

  @Test
  @DisplayName("getSetOfProductListViewBySubCategory return a set of product list view when successful")
  public void getSetOfProductListViewBySubCategory_ReturnASetProductListView_WhenSuccessful() {
    String[] subcategories = { "Sub Category 1" };

    Set<ProductListView> products = iProductService.getSetOfProductListViewBySubCategory(subcategories);

    assertThat(products).isNotNull();
    assertThat(products.isEmpty()).isFalse();
  }

  @Test
  @DisplayName("getSetOfProductListViewBySubCategory throw ResourceNotFoundException when the sub category or sub categories has no products")
  public void getSetOfProductListViewBySubCategory_ThrowResourceNotFoundException_WhenTheSubCategoryOrSubCategoriesHasNoProducts() {
    String[] subcategories = { "Sub Category 1", "Sub Category 2" };

    BDDMockito.when(iProductRepository.findBySubCategory(any(SubCategory.class))).thenReturn(List.of());

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iProductService.getSetOfProductListViewBySubCategory(subcategories)).withMessage(NO_PRODUCTS_FOUND);
  }
}
