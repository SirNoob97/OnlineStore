package com.sirnoob.productservice.controller;

import static com.sirnoob.productservice.util.RandomEntityGenerator.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductListView;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    BDDMockito.when((iProductRepository.findByProductBarCodeOrProductName(anyLong(), anyString()))).thenReturn(Optional.of(product));

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
  @DisplayName("createProduct return a ProductResponse when successful")
  public void createProduct_ReturnAProductResponse_WhenSuccessful() throws Exception{
    mockMvc.perform(post("/products").contentType(JSON)
                                      .content(OBJECT_MAPPER.writeValueAsString(createProductRequest())))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(JSON))
            .andExpect(jsonPath(PRODUCT_ID, Matchers.isA(Integer.class)))
            .andExpect(jsonPath(PRODUCT_BAR_CODE, Matchers.isA(Long.class)));
  }

  @Test
  @DisplayName("updateProduct return a ProductResponse when successful")
  public void updateProductReturnAProductResponseWhenSuccessful() throws Exception{
    mockMvc.perform(put("/products/1").contentType(JSON)
                                      .content(OBJECT_MAPPER.writeValueAsString(createProductRequest())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON))
            .andExpect(jsonPath(PRODUCT_ID, Matchers.isA(Integer.class)))
            .andExpect(jsonPath(PRODUCT_BAR_CODE, Matchers.isA(Long.class)));
  }

  @Test
  @DisplayName("updateProductStock update the stock of a product log when successful")
  public void updateProductStockUpdateTheStockOfAProductLogWhenSuccessful() throws Exception{
    mockMvc.perform(put("/products/1/stock?quantity=100"))
            .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("getProductResponse return a ProductResponse when successful")
  public void getProductResponse_ReturnAProductResponse_WhenSuccesful() throws Exception{
    mockMvc.perform(get("/products/responses?productBarCode=1&productName=product"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("getProductForInvoice return a ProductInvoiceResponse when successful")
  public void getProductForInvoice_ResturnAProductInvoiceResponse_WhenSuccessful() throws Exception{
    mockMvc.perform(get("/products/invoices?productBarCode=1&productName=product"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("deleteProductById delete a product log when successful")
  public void deleteProductByIdDeleteAProductLogWhenSuccessful() throws Exception{
    mockMvc.perform(delete("/products/1")).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("listAllProducts return a page of ProductListView when successful")
  public void listAllProducts_ReturnAPageOfProductListView_WhenSuccessful() throws Exception{
    mockMvc.perform(get("/products?page=0&size=10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("listProductsByMainCategoryId return a page of ProductListView when successful")
  public void listProductsByMainCategoryId_ReturnAPageOfProductListView_WhenSuccessful() throws Exception{
    mockMvc.perform(get("/products/main-categories/1?page=0&size=10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("listProductsBySubCategories return a set of ProductListView when successful")
  public void listProductsBySubCategories_ReturnASetOfProductListView_WhenSuccessful() throws Exception{
    String[] subCategories = {"SubCategory 1", "SubCategory2"};

    mockMvc.perform(get("/products/sub-categories").contentType(JSON)
                                                    .content(OBJECT_MAPPER.writeValueAsString(subCategories)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("getProductByProductName return a ProductView when successful")
  public void getProductByProductName_ReturnAProductView_WhenSuccessful() throws Exception{
    mockMvc.perform(get("/products/names?productName=product"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("listByProductNameCoincidences return a page of ProductListView when successful")
  public void listByProductNameCoincidences_ReturnAPageOfProductListView_WhenSuccessful() throws Exception{
    mockMvc.perform(get("/products/names/search?productName=product&page=0&size=10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }
}
