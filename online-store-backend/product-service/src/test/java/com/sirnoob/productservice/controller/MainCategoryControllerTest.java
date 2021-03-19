package com.sirnoob.productservice.controller;

import static com.sirnoob.productservice.util.RandomEntityGenerator.createMainCategory;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProduct;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createSubCategory;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
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
@Import({ MainCategoryServiceImpl.class, ProductServiceImpl.class, ProductMapperImpl.class, SubCategoryServiceImpl.class, SubCategoryMapperImpl.class })
@DisplayName("Main Category Controller Test")
class MainCategoryControllerTest {

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
  private static final MainCategory MAINCATEGORY = createMainCategory();
  private static final SubCategory SUBCATEGORY = createSubCategory();
  private static final Product PRODUCT = createProduct();

  @BeforeEach
  public void setUp() {
    BDDMockito.when(iMainCategoryRepository.save(any(MainCategory.class))).thenReturn(MAINCATEGORY);

    BDDMockito.when(iMainCategoryRepository.updateMainCategoryName(anyString(), anyLong())).thenReturn(1);

    BDDMockito.when(iSubCategoryRespository.findById(anyLong())).thenReturn(Optional.of(SUBCATEGORY));

    BDDMockito.when(iMainCategoryRepository.findById(anyLong())).thenReturn(Optional.of(MAINCATEGORY));

    BDDMockito.doNothing().when(iSubCategoryRespository).delete(any(SubCategory.class));

    BDDMockito.when(iProductRepository.findByMainCategoryMainCategoryId(anyLong())).thenReturn(List.of(PRODUCT));

    BDDMockito.when(iMainCategoryRepository.findByMainCategoryName(anyString())).thenReturn(Optional.of(MAINCATEGORY));

    BDDMockito.when(iMainCategoryRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(MAINCATEGORY)));
  }

  @Test
  @DisplayName("createMainCategory return 201 status code when successful")
  public void createMainCategory_Return201StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(post("/main-categories").contentType(JSON)
                                            .content(OBJECT_MAPPER.writeValueAsString(MAINCATEGORY))
                                            .accept(JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("createMainCategory return 400 status code when MainCategory has invalid fields")
  public void createMainCategory_Return400StatusCode_WhenMainCategoryHasInvalidFields() throws Exception{
    mockMvc.perform(post("/main-categories").contentType(JSON)
                                            .content(OBJECT_MAPPER.writeValueAsString(new MainCategory()))
                                            .accept(JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("updateMainCategory return 204 status code when successful")
  public void updateMainCategory_Return204StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(put("/main-categories/1?mainCategoryName=maincategory"))
            .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("updateMainCategory return 404 status code when update operation returns 0")
  public void updateMainCategory_Return404StatusCode_WhenUpdateOperationReturnsZero() throws Exception{
    BDDMockito.when(iMainCategoryRepository.updateMainCategoryName(anyString(), anyLong())).thenReturn(0);

    mockMvc.perform(put("/main-categories/1?mainCategoryName=maincategory"))
            .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("deleteMainCategory return 204 status code when successful")
  public void deleteMainCategory_Return204StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(delete("/main-categories/1")).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("deleteMainCategory return 404 status code when MainCategory was not found")
  public void deleteMainCategory_Return404StatusCode_WhenMainCategoryWasNotFound() throws Exception{
    BDDMockito.when(iMainCategoryRepository.findById(anyLong())).thenReturn(Optional.empty());

    mockMvc.perform(delete("/main-categories/1")).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("getMainCategoryByName return 200 status code when successful")
  public void getMainCategoryByName_Return200StatusCode_WhenSuccesful() throws Exception{
    mockMvc.perform(get("/main-categories/maincategory").accept(JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("getMainCategoryByName return 404 status code when MainCategory was not found")
  public void getMainCategoryByName_Return404StatusCode_WhenMainCategoryWasNotFound() throws Exception{
    BDDMockito.when(iMainCategoryRepository.findByMainCategoryName(anyString())).thenReturn(Optional.empty());

    mockMvc.perform(get("/main-categories/maincategory").accept(JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("getAllMainCategory return 200 status code when successful")
  public void getAllMainCategory_Return200StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(get("/main-categories?page=0&size=10").accept(JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("getAllMainCategory return 404 status code when no MainCategory are found")
  public void getAllMainCategory_Return404StatusCode_WhenNoMainCategoriesAreFound() throws Exception{
    BDDMockito.when(iMainCategoryRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of()));

    mockMvc.perform(get("/main-categories?page=0&size=10").accept(JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }
}