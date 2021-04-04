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
import com.sirnoob.productservice.mapper.ProductMapper;
import com.sirnoob.productservice.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
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
public class ProductServiceTest {

  private static final String PRODUCT_NOT_FOUND = "Product Not Found";
  private static final String NO_PRODUCTS_FOUND = "No Products Found";
  private static final String PRODUCT = "Product";

  @Mock
  SubCategoryService subCategoryService;

  @Mock
  ProductRepository productRepository;

  @Mock
  ProductMapper productMapper;

  ProductService productService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    productService = new ProductServiceImpl(subCategoryService, productRepository, productMapper);

    Set<SubCategory> subCategories = Set.of(createSubCategory(), createSubCategory());

    Product product = createProductWithMainCategoryAndSubCategory();

    ProductResponse productResponse = createProductResponseStaticValues();

    ProductInvoiceResponse productForInvoiceResponse = createProductInvoiceResponse();

    ProductView productView = createProductViewStaticValues();

    ProductListView productListView = createProductListViewStaticValues();

    PageImpl<ProductListView> productListViewPage = new PageImpl<>(List.of(productListView));

    BDDMockito.when(subCategoryService.getSetByName(any())).thenReturn(subCategories);

    BDDMockito.when(productMapper.productRequestToProduct(any(ProductRequest.class), any(MainCategory.class), anySet())).thenReturn(product);

    BDDMockito.when(productRepository.save(any(Product.class))).thenReturn(product);

    BDDMockito.when(productMapper.productToProductResponse(any(Product.class))).thenReturn(productResponse);

    BDDMockito.when(productRepository.updateStockByBarCode(anyInt(), anyLong())).thenReturn(1);

    BDDMockito.doNothing().when(productRepository).delete(any(Product.class));

    BDDMockito.when(productRepository.findByProductBarCodeOrProductName(anyLong(), anyString())).thenReturn(Optional.of(product));

    BDDMockito.when(productRepository.findForInvoice(anyLong(), anyString())).thenReturn(Optional.of(productForInvoiceResponse));

    BDDMockito.when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

    BDDMockito.when(productRepository.findByMainCategoryMainCategoryId(anyLong())).thenReturn(List.of(product));

    BDDMockito.when(productRepository.findByProductName(anyString())).thenReturn(Optional.of(product));

    BDDMockito.when(productMapper.productToProductView(any(Product.class))).thenReturn(productView);

    BDDMockito.when(productRepository.findByProductNameContainingIgnoreCase(anyString(), any(Pageable.class))).thenReturn(productListViewPage);

    BDDMockito.when(productRepository.getAll(any(Pageable.class))).thenReturn(productListViewPage);

    BDDMockito.when(productRepository.findByMainCategoryMainCategoryId(anyLong(), any(Pageable.class))).thenReturn(productListViewPage);

    BDDMockito.when(subCategoryService.getSetByName(any())).thenReturn(subCategories);

    BDDMockito.when(productRepository.findBySubCategory(any(SubCategory.class))).thenReturn(List.of(productListView));
  }

  @Test
  public void createProduct_CreateProduct_WhenSuccessful() {
    ProductResponse productSaved = productService.create(createProductRequest(), createMainCategoryStaticValues());

    assertThat(productSaved).isNotNull();
    assertThat(productSaved.getProductStatus()).isEqualTo("CREATED");
    assertThat(productSaved.getMainCategoryName()).isNotNull();
    assertThat(productSaved.getSubCategories()).isNotNull();
    assertThat(productSaved.getSubCategories().isEmpty()).isNotNull();
  }

  @Test
  public void updateProduct_UpdateAnExistingProduct_WhenSuccessful() {
    ProductResponse productUpdated = productService.update(1L, createProductRequest(), createMainCategoryStaticValues());

    assertThat(productUpdated).isNotNull();
    assertThat(productUpdated.getMainCategoryName()).isNotNull();
    assertThat(productUpdated.getSubCategories()).isNotNull();
    assertThat(productUpdated.getSubCategories().isEmpty()).isNotNull();
  }

  @Test
  public void updateProductStock_UpdateStockOfAnExistingProduct_WhenSuccessful() {
    assertThatCode(() -> productService.updateStock(1L, 100)).doesNotThrowAnyException();
  }

  @Test
  public void updateProductStock_ThrowResourceNotFoundException_WhenTheReturnOfTheQueryIsLessThanOne() {
    BDDMockito.when(productRepository.updateStockByBarCode(anyInt(), anyLong()))
        .thenThrow(new ResourceNotFoundException(PRODUCT_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> productService.updateStock(-1L, 0))
      .withMessage(PRODUCT_NOT_FOUND);
  }

  @Test
  public void deleteProductById_RemovesAProduct_WhenSuccessful() {
    assertThatCode(() -> productService.deleteById(1L)).doesNotThrowAnyException();
  }

  @Test
  public void getProductResponseByBarCodeOrProductName_ReturnAProductResponse_WhenSuccessful() {
    ProductResponse productFetchedByName = productService.getProductResponseByBarCodeOrProductName(-1L,
      PRODUCT);

    assertThat(productFetchedByName).isNotNull();
    assertThat(productFetchedByName.getMainCategoryName()).isNotNull();
    assertThat(productFetchedByName.getSubCategories()).isNotNull();
  }

  @Test
  public void getProductResponseByBarCodeOrProductName_ThrowResourceNotFoundException_WhenProductNotFound() {
    BDDMockito.when(productRepository.findByProductBarCodeOrProductName(anyLong(), anyString()))
        .thenThrow(new ResourceNotFoundException(PRODUCT_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> productService.getProductResponseByBarCodeOrProductName(-1L, "")).withMessage(PRODUCT_NOT_FOUND);
  }

  @Test
  public void getProductForInvoiceResponse_ReturnAProductInvoiceResponse_WhenSuccessful() {
    ProductInvoiceResponse productForInvoiceResponse = productService
      .getForInvoiceResponse(1L, PRODUCT);

    assertThat(productForInvoiceResponse).isNotNull();
    assertThat(productForInvoiceResponse.getProductName()).isNotNull();
    assertThat(productForInvoiceResponse.getProductPrice()).isNotNull();
  }

  @Test
  public void getProductForInvoiceResponse_ThrowResourceNotFoundException_WhenProductNotFound() {
    BDDMockito.when(productRepository.findForInvoice(anyLong(), anyString()))
        .thenThrow(new ResourceNotFoundException(PRODUCT_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> productService.getForInvoiceResponse(-1L, "")).withMessage(PRODUCT_NOT_FOUND);
  }

  @Test
  public void getProductById_ReturnAProduct_WhenSuccessful() {
    Product productFetchedById = productService.getById(1L);

    assertThat(productFetchedById).isNotNull();
    assertThat(productFetchedById.getMainCategory()).isNotNull();
    assertThat(productFetchedById.getSubCategories()).isNotNull();
  }

  @Test
  public void getProductById_ThrowResourceNotFoundException_WhenProductWasNotFound() {
    BDDMockito.when(productRepository.findById(anyLong())).thenThrow(new ResourceNotFoundException(PRODUCT_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> productService.getById(-1L))
      .withMessage(PRODUCT_NOT_FOUND);
  }

  @Test
  public void getProductByMainCategory_ReturnAListOfProduct_WhenSuccessful() {
    List<Product> products = productService.getByMainCategory(1L);

    assertThat(products).isNotNull();
    assertThat(products.isEmpty()).isFalse();
  }

  @Test
  public void getProductByMainCategory_ReturnAnEmptyProductList_WhenTheMainCategoryHasNoProducts() {
    BDDMockito.when(productRepository.findByMainCategoryMainCategoryId(anyLong())).thenReturn(List.of());

    List<Product> products = productService.getByMainCategory(1L);

    assertThat(products).isNotNull();
    assertThat(products.isEmpty()).isTrue();
  }

  @Test
  public void findProductViewByName_ReturnAProductView_WhenSuccessful() {
    ProductView productView = productService.getProductViewByName(PRODUCT);

    assertThat(productView).isNotNull();
    assertThat(productView.getMainCategoryName()).isNotNull();
    assertThat(productView.getSubCategories()).isNotNull();
  }

  @Test
  public void findProductViewByName_ThrowResourceNotFoundException_WhenProductNotFound() {
    BDDMockito.when(productRepository.findByProductName(anyString())).thenThrow(new ResourceNotFoundException(PRODUCT_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> productService.getProductViewByName(""))
      .withMessage(PRODUCT_NOT_FOUND);
  }

  @Test
  public void getProductListViewByName_ReturnAPageProductListView_WhenSuccessful() {
    Page<ProductListView> products = productService.getListViewByName("sa", PageRequest.of(0, 10));

    assertThat(products).isNotNull();
    assertThat(products.isEmpty()).isFalse();
  }

  @Test
  public void getPageOfProductListViewByName_ThrowResourceNotFoundException_WhenTheNamesDoNotMatch() {
    BDDMockito.when(productRepository.findByProductNameContainingIgnoreCase(anyString(), any(Pageable.class)))
        .thenThrow(new ResourceNotFoundException(NO_PRODUCTS_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> productService.getListViewByName("?>~!#", PageRequest.of(0, 10)))
      .withMessage(NO_PRODUCTS_FOUND);
  }

  @Test
  public void getPageOfProductListView_ReturnAPageProductListView_WhenSuccessful() {
    Page<ProductListView> products = productService.getListView(PageRequest.of(0, 10));

    assertThat(products).isNotNull();
    assertThat(products.isEmpty()).isFalse();
  }

  @Test
  public void getPageOfProductListView_ThrowResourceNotFoundException_WhenThereAreNoProductsInTheRegistry() {
    BDDMockito.when(productRepository.getAll(any(Pageable.class))).thenThrow(new ResourceNotFoundException(NO_PRODUCTS_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> productService.getListView(PageRequest.of(0, 10))).withMessage(NO_PRODUCTS_FOUND);
  }

  @Test
  public void getPageOfProductListViewByMainCategory_ReturnAPageProductListView_WhenSuccessful() {
    Page<ProductListView> products = productService.getListViewByMainCategory(1L, PageRequest.of(0, 10));

    assertThat(products).isNotNull();
    assertThat(products.isEmpty()).isFalse();
  }

  @Test
  public void getPageOfProductListViewByMainCategory_ThrowResourceNotFoundException_WhenTheMainCategoryHasNoProducts() {
    BDDMockito.when(productRepository.findByMainCategoryMainCategoryId(anyLong(), any(Pageable.class)))
        .thenThrow(new ResourceNotFoundException(NO_PRODUCTS_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
        .isThrownBy(() -> productService.getListViewByMainCategory(1L, PageRequest.of(0, 10)))
        .withMessage(NO_PRODUCTS_FOUND);
  }

  @Test
  public void getSetOfProductListViewBySubCategory_ReturnASetProductListView_WhenSuccessful() {
    String[] subcategories = { "Sub Category 1" };

    Set<ProductListView> products = productService.getListViewBySubCategory(subcategories);

    assertThat(products).isNotNull();
    assertThat(products.isEmpty()).isFalse();
  }

  @Test
  public void getSetOfProductListViewBySubCategory_ThrowResourceNotFoundException_WhenTheSubCategoryOrSubCategoriesHasNoProducts() {
    String[] subcategories = { "Sub Category 1", "Sub Category 2" };

    BDDMockito.when(productRepository.findBySubCategory(any(SubCategory.class))).thenReturn(List.of());

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> productService.getListViewBySubCategory(subcategories)).withMessage(NO_PRODUCTS_FOUND);
  }
}
