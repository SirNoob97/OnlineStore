package com.sirnoob.productservice.controller;

import static com.sirnoob.productservice.util.RandomEntityGenerator.createMainCategory;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductInvoiceResponse;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductListViewStaticValues;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductRequest;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductWithMainCategoryAndSubCategory;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createSubCategory;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;
import com.sirnoob.productservice.mapper.ProductMapperImpl;
import com.sirnoob.productservice.mapper.SubCategoryMapperImpl;
import com.sirnoob.productservice.repository.IMainCategoryRepository;
import com.sirnoob.productservice.repository.IProductRepository;
import com.sirnoob.productservice.repository.ISubCategoryRepository;
import com.sirnoob.productservice.service.MainCategoryServiceImpl;
import com.sirnoob.productservice.service.ProductServiceImpl;
import com.sirnoob.productservice.service.SubCategoryServiceImpl;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import({ ProductServiceImpl.class, ProductMapperImpl.class, MainCategoryServiceImpl.class, SubCategoryServiceImpl.class, SubCategoryMapperImpl.class })
@DisplayName("Product Controller Test")
class ProductControllerTest {

  @MockBean
  private IMainCategoryRepository iMainCategoryRepository;

  @MockBean
  private ISubCategoryRepository iSubCategoryRespository;

  @MockBean
  private IProductRepository iProductRepository;

  @Autowired
  private MockMvc mockMvc;

  private static final MediaType JSON = MediaType.APPLICATION_JSON;
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final String PRODUCT_ID = "$.productId";
  private static final String PRODUCT_BAR_CODE = "$.productBarCode";

  @BeforeEach
  public void setUp() {

    Optional<MainCategory> mainCategory = Optional.of(createMainCategory());

    Optional<SubCategory> subCategory = Optional.of(createSubCategory());

    Product product = createProductWithMainCategoryAndSubCategory();

    ProductInvoiceResponse productInvoiceResponse = createProductInvoiceResponse();

    List<ProductListView> listOfProducts = List.of(createProductListViewStaticValues());

    PageImpl<ProductListView> pageOfProducts = new PageImpl<>(listOfProducts);

    BDDMockito.when(iMainCategoryRepository.findByMainCategoryName(anyString())).thenReturn(mainCategory);

    BDDMockito.when(iSubCategoryRespository.findBySubCategoryName(anyString())).thenReturn(subCategory);

    BDDMockito.when(iProductRepository.save(any(Product.class))).thenReturn(product);

    BDDMockito.when(iProductRepository.findById(anyLong())).thenReturn(Optional.of(product));

    BDDMockito.when(iProductRepository.updateProductStockByProductBarCode(anyInt(), anyLong())).thenReturn(1);

    BDDMockito.when(iProductRepository.findByProductBarCodeOrProductName(anyLong(), anyString())).thenReturn(Optional.of(product));

    BDDMockito.when(iProductRepository.findProductForInvoice(anyLong(), anyString())).thenReturn(Optional.of(productInvoiceResponse));

    BDDMockito.doNothing().when(iProductRepository).delete(any(Product.class));

    BDDMockito.when(iProductRepository.getAll(any(PageRequest.class))).thenReturn(pageOfProducts);

    BDDMockito.when(iProductRepository.findByMainCategoryMainCategoryId(anyLong(), any(PageRequest.class))).thenReturn(pageOfProducts);

    BDDMockito.when(iSubCategoryRespository.findBySubCategoryName(anyString())).thenReturn(subCategory);

    BDDMockito.when(iProductRepository.findBySubCategory(any(SubCategory.class))).thenReturn(listOfProducts);

    BDDMockito.when(iProductRepository.findByProductName(anyString())).thenReturn(Optional.of(product));

    BDDMockito.when(iProductRepository.findByProductNameContainingIgnoreCase(anyString(), any(PageRequest.class))).thenReturn(pageOfProducts);
  }

  @Test
  @DisplayName("createProduct return 201 status code when successful")
  public void createProduct_Return201StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(post("/products").contentType(JSON)
                                      .content(OBJECT_MAPPER.writeValueAsString(createProductRequest())))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(JSON))
            .andExpect(jsonPath(PRODUCT_ID, Matchers.isA(Integer.class)))
            .andExpect(jsonPath(PRODUCT_BAR_CODE, Matchers.isA(Long.class)));
  }

  @Test
  @DisplayName("createProduct return 404 status code when main category was not found")
  public void createProduct_Return404StatusCode_WhenMainCategoryWasNotFound() throws Exception{
    BDDMockito.when(iMainCategoryRepository.findByMainCategoryName(anyString())).thenReturn(Optional.empty());

    mockMvc.perform(post("/products").contentType(JSON)
                                      .content(OBJECT_MAPPER.writeValueAsString(createProductRequest())))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("createProduct return 400 status code when ProductRequest has invalid fields")
  public void createProduct_Return400StatusCode_WhenProductRequestHasInvalidFields() throws Exception{
    mockMvc.perform(post("/products").contentType(JSON)
                                      .content(OBJECT_MAPPER.writeValueAsString(new ProductRequest())))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("createProduct return 404 status code when sub category was not found")
  public void createProduct_Return404StatusCode_WhenSubCategoryWasNotFound() throws Exception{
    BDDMockito.when(iSubCategoryRespository.findBySubCategoryName(anyString())).thenReturn(Optional.empty());

    mockMvc.perform(post("/products").contentType(JSON)
                                      .content(OBJECT_MAPPER.writeValueAsString(createProductRequest())))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("updateProduct return 200 status code when successful")
  public void updateProduct_Return200StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(put("/products/1").contentType(JSON)
                                      .content(OBJECT_MAPPER.writeValueAsString(createProductRequest())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON))
            .andExpect(jsonPath(PRODUCT_ID, Matchers.isA(Integer.class)))
            .andExpect(jsonPath(PRODUCT_BAR_CODE, Matchers.isA(Long.class)));
  }

  @Test
  @DisplayName("createProduct return 404 status code when main category was not found")
  public void updateProduct_Return404StatusCode_WhenMainCategoryWasNotFound() throws Exception{
    BDDMockito.when(iMainCategoryRepository.findByMainCategoryName(anyString())).thenReturn(Optional.empty());

    mockMvc.perform(put("/products/1").contentType(JSON)
                                      .content(OBJECT_MAPPER.writeValueAsString(createProductRequest())))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("createProduct return 404 status code when sub category was not found")
  public void updateProduct_Return404StatusCode_WhenSubCategoryWasNotFound() throws Exception{
    BDDMockito.when(iSubCategoryRespository.findBySubCategoryName(anyString())).thenReturn(Optional.empty());

    mockMvc.perform(put("/products/1").contentType(JSON)
                                      .content(OBJECT_MAPPER.writeValueAsString(createProductRequest())))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("updateProductStock return 204 status code when successful")
  public void updateProductStock_Return204StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(put("/products/1/stock?quantity=100"))
            .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("updateProductStock return 404 status code when update operation returns 0")
  public void updateProductStock_Return404StatusCode_WhenUpdateOperationsReturnsZero() throws Exception{
    BDDMockito.when(iProductRepository.updateProductStockByProductBarCode(anyInt(), anyLong())).thenReturn(0);

    mockMvc.perform(put("/products/1/stock?quantity=100"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("getProductResponse return 200 status code when successful")
  public void getProductResponse_Return200StatusCode_WhenSuccesful() throws Exception{
    mockMvc.perform(get("/products/responses?productBarCode=1&productName=product"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("getProductResponse return 404 status code when product was not found")
  public void getProductResponse_Return404StatusCode_WhenProductWasNotFound() throws Exception{
    BDDMockito.when(iProductRepository.findByProductBarCodeOrProductName(anyLong(), anyString())).thenReturn(Optional.empty());

    mockMvc.perform(get("/products/responses?productBarCode=1&productName=product"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("getProductForInvoice return 200 status code when successful")
  public void getProductForInvoice_Return200StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(get("/products/invoices?productBarCode=1&productName=product"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("getProductForInvoice return 404 status code when product was not found")
  public void getProductForInvoice_Return404StatusCode_WhenProductWasNotFound() throws Exception{
    BDDMockito.when(iProductRepository.findProductForInvoice(anyLong(), anyString())).thenReturn(Optional.empty());

    mockMvc.perform(get("/products/invoices?productBarCode=1&productName=product"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("deleteProductById return 204 status code when successful")
  public void deleteProductById_Return204StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(delete("/products/1")).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("deleteProductById return 404 status code when product was not found")
  public void deleteProductById_Return404StatusCode_WhenProductWasNotFound() throws Exception{
    BDDMockito.when(iProductRepository.findById(anyLong())).thenReturn(Optional.empty());

    mockMvc.perform(delete("/products/1")).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("listAllProducts return 200 status code when successful")
  public void listAllProducts_Return200StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(get("/products?page=0&size=10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("listAllProducts return 404 status code when no products are found")
  public void listAllProducts_Return404StatusCode_WhenNoProductsAreFound() throws Exception{
    BDDMockito.when(iProductRepository.getAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of()));

    mockMvc.perform(get("/products?page=0&size=10"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("listProductsByMainCategoryId return 200 status code when successful")
  public void listProductsByMainCategoryId_Return200StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(get("/products/main-categories/1?page=0&size=10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("listProductsByMainCategoryId return 404 status code when no products are found")
  public void listProductsByMainCategoryId_Return404StatusCode_WhenNoProductsAreFound() throws Exception{
    BDDMockito.when(iProductRepository.findByMainCategoryMainCategoryId(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of()));

    mockMvc.perform(get("/products/main-categories/1?page=0&size=10"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("listProductsBySubCategories return 200 status code when successful")
  public void listProductsBySubCategories_Return200StatusCode_WhenSuccessful() throws Exception{
    String[] subCategories = {"SubCategory 1", "SubCategory2"};

    mockMvc.perform(get("/products/sub-categories").contentType(JSON)
                                                    .content(OBJECT_MAPPER.writeValueAsString(subCategories)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("listProductsBySubCategories return 404 status code when that subcategory has no products")
  public void listProductsBySubCategories_Return404StatusCode_WhenThatSubCategoryHasNoProducts() throws Exception{
    BDDMockito.when(iProductRepository.findBySubCategory(any(SubCategory.class))).thenReturn(List.of());

    String[] subCategories = {"SubCategory 1", "SubCategory2"};

    mockMvc.perform(get("/products/sub-categories").contentType(JSON)
                                                    .content(OBJECT_MAPPER.writeValueAsString(subCategories)))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("getProductByProductName return 200 status code when successful")
  public void getProductByProductName_Return200StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(get("/products/names?productName=product"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("getProductByProductName return 404 status code when product was not found")
  public void getProductByProductName_Return404StatusCode_WhenProductWasNotFound() throws Exception{
    BDDMockito.when(iProductRepository.findByProductName(anyString())).thenReturn(Optional.empty());

    mockMvc.perform(get("/products/names?productName=product"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("listByProductNameCoincidences return 200 status code when successful")
  public void listByProductNameCoincidences_Return200StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(get("/products/names/search?productName=product&page=0&size=10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("listByProductNameCoincidences return 404 status code when no products are found")
  public void listByProductNameCoincidences_Return404StatusCode_WhenNoProductsAreFound() throws Exception{
    BDDMockito.when(iProductRepository.findByProductNameContainingIgnoreCase(anyString(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of()));

    mockMvc.perform(get("/products/names/search?productName=product&page=0&size=10"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }
}
