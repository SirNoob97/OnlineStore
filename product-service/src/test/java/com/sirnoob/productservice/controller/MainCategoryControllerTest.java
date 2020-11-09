package com.sirnoob.productservice.controller;

import static com.sirnoob.productservice.util.RandomEntityGenerator.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;

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
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


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
  @DisplayName("updateMainCategory return 204 status code when successful")
  public void updateMainCategory_Return204StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(put("/main-categories/1?mainCategoryName=maincategory"))
            .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("deleteMainCategory return 204 status code when successful")
  public void deleteMainCategory_Return204StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(delete("/main-categories/1")).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("getMainCategoryByName return 200 status code when successful")
  public void getMainCategoryByName_Return200StatusCode_WhenSuccesful() throws Exception{
    mockMvc.perform(get("/main-categories/maincategory").accept(JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("getAllMainCategory return 201 status code when successful")
  public void getAllMainCategory_Return200StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(get("/main-categories?page=0&size=10").accept(JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }
}
