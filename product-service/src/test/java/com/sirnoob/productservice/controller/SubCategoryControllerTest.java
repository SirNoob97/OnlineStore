package com.sirnoob.productservice.controller;

import static com.sirnoob.productservice.util.RandomEntityGenerator.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sirnoob.productservice.entity.MainCategory;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest
@Import({ SubCategoryServiceImpl.class, SubCategoryMapperImpl.class, MainCategoryServiceImpl.class, ProductServiceImpl.class, ProductMapperImpl.class })
@DisplayName("Sub Category Controller Test")
class SubCategoryControllerTest {

  @MockBean
  private ISubCategoryRepository iSubCategoryRepository;

  @MockBean
  private IMainCategoryRepository iMainCategoryRepository;

  @MockBean
  private IProductRepository iProductRepository;

  @Autowired
  private MockMvc mockMvc;

  private static final MainCategory MAIN_CATEGORY = createMainCategoryStaticValues();
  private static final SubCategory SUB_CATEGORY = createSubCategoryForIT();
  private static final MediaType JSON = MediaType.APPLICATION_JSON;
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @BeforeEach
  public void setUp() {
    BDDMockito.when(iMainCategoryRepository.findByMainCategoryName(anyString())).thenReturn(Optional.of(MAIN_CATEGORY));

    BDDMockito.when(iSubCategoryRepository.save(any(SubCategory.class))).thenReturn(SUB_CATEGORY);

    BDDMockito.when(iSubCategoryRepository.updateSubCategoryName(anyString(), anyLong())).thenReturn(1);

    BDDMockito.when(iSubCategoryRepository.findById(anyLong())).thenReturn(Optional.of(SUB_CATEGORY));

    BDDMockito.doNothing().when(iSubCategoryRepository).delete(any(SubCategory.class));

    BDDMockito.when(iSubCategoryRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(SUB_CATEGORY)));

    BDDMockito.when(iSubCategoryRepository.findBySubCategoryName(anyString())).thenReturn(Optional.of(SUB_CATEGORY));
  }

  @Test
  @DisplayName("createSubCategory return a sub category when successful")
  public void createSubCategoryCategory_ReturnASubCategory_WhenSuccessful() throws Exception{
    mockMvc.perform(post("/sub-categories").contentType(JSON)
                                            .accept(JSON)
                                            .content(OBJECT_MAPPER.writeValueAsString(createSubCategoryRequest())))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("updateSubCategoryNameCategory update the name of a sub category when successful")
  public void updateSubCategoryName_UpdateTheNameOfASubCategory_WhenSuccessful() throws Exception{
    mockMvc.perform(put("/sub-categories/1?subCategoryName=subcategory").contentType(JSON)
                                            .content(OBJECT_MAPPER.writeValueAsString(createSubCategoryRequest())))
            .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("deleteSubCategory delete a sub category when successful")
  public void deleteSubCategory_DelteASubCategory_WhenSuccessful() throws Exception{
    mockMvc.perform(delete("/sub-categories/1")).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("getSubCategoryResponseByNameCategoryByName return a sub category when successful")
  public void getSubCategoryResponseByName_ResturnASubCategory_WhenSuccesful() throws Exception{
    mockMvc.perform(get("/sub-categories/subcategory").accept(JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  @DisplayName("getAllSubCategoriesCategory return a set of sub categories names when successful")
  public void getAllSubCategories_ReturnASetOfSubCategoriesNamesWhenSuccessful() throws Exception{
    mockMvc.perform(get("/sub-categories?page=0&size=10").accept(JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }
}
