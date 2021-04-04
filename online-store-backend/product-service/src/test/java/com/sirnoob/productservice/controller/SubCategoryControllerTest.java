package com.sirnoob.productservice.controller;

import static com.sirnoob.productservice.util.RandomEntityGenerator.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sirnoob.productservice.dto.SubCategoryRequest;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.SubCategory;
import com.sirnoob.productservice.mapper.ProductMapperImpl;
import com.sirnoob.productservice.mapper.SubCategoryMapperImpl;
import com.sirnoob.productservice.repository.MainCategoryRepository;
import com.sirnoob.productservice.repository.ProductRepository;
import com.sirnoob.productservice.repository.SubCategoryRepository;
import com.sirnoob.productservice.service.MainCategoryServiceImpl;
import com.sirnoob.productservice.service.ProductServiceImpl;
import com.sirnoob.productservice.service.SubCategoryServiceImpl;

import org.junit.jupiter.api.BeforeEach;
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
class SubCategoryControllerTest {

  @MockBean
  private SubCategoryRepository subCategoryRepository;

  @MockBean
  private MainCategoryRepository mainCategoryRepository;

  @MockBean
  private ProductRepository productRepository;

  @Autowired
  private MockMvc mockMvc;

  private static final MainCategory MAIN_CATEGORY = createMainCategoryStaticValues();
  private static final SubCategory SUB_CATEGORY = createSubCategoryForIT();
  private static final MediaType JSON = MediaType.APPLICATION_JSON;
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @BeforeEach
  public void setUp() {
    BDDMockito.when(mainCategoryRepository.findByMainCategoryName(anyString())).thenReturn(Optional.of(MAIN_CATEGORY));

    BDDMockito.when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(SUB_CATEGORY);

    BDDMockito.when(subCategoryRepository.updateName(anyString(), anyLong())).thenReturn(1);

    BDDMockito.when(subCategoryRepository.findById(anyLong())).thenReturn(Optional.of(SUB_CATEGORY));

    BDDMockito.doNothing().when(subCategoryRepository).delete(any(SubCategory.class));

    BDDMockito.when(subCategoryRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(SUB_CATEGORY)));

    BDDMockito.when(subCategoryRepository.findBySubCategoryName(anyString())).thenReturn(Optional.of(SUB_CATEGORY));
  }

  @Test
  public void createSubCategoryCategory_Return201StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(post("/sub-categories").contentType(JSON)
                                            .accept(JSON)
                                            .content(OBJECT_MAPPER.writeValueAsString(createSubCategoryRequest())))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(JSON));
  }

  @Test
  public void createSubCategoryCategory_Return400StatusCode_WhenSubCategoryRequestHasInvalidFields() throws Exception{
    mockMvc.perform(post("/sub-categories").contentType(JSON)
                                            .accept(JSON)
                                            .content(OBJECT_MAPPER.writeValueAsString(new SubCategoryRequest())))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(JSON));
  }

  @Test
  public void updateSubCategoryName_Return204StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(put("/sub-categories/1?subCategoryName=subcategory").contentType(JSON)
                                            .content(OBJECT_MAPPER.writeValueAsString(createSubCategoryRequest())))
            .andExpect(status().isNoContent());
  }

  @Test
  public void updateSubCategoryName_Return404StatusCode_WhenUpdateOperationReturnsZero() throws Exception{
    BDDMockito.when(subCategoryRepository.updateName(anyString(), anyLong())).thenReturn(0);

    mockMvc.perform(put("/sub-categories/1?subCategoryName=subcategory").contentType(JSON)
                                            .content(OBJECT_MAPPER.writeValueAsString(createSubCategoryRequest())))
            .andExpect(status().isNotFound());
  }

  @Test
  public void deleteSubCategory_Return204StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(delete("/sub-categories/1")).andExpect(status().isNoContent());
  }

  @Test
  public void deleteSubCategory_Return404StatusCode_WhenSubCategoryWasNotFound() throws Exception{
    BDDMockito.when(subCategoryRepository.findById(anyLong())).thenReturn(Optional.empty());

    mockMvc.perform(delete("/sub-categories/1")).andExpect(status().isNotFound());
  }

  @Test
  public void getSubCategoryResponseByName_Return200StatusCode_WhenSuccesful() throws Exception{
    mockMvc.perform(get("/sub-categories/subcategory").accept(JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  public void getSubCategoryResponseByName_Return404StatusCode_WhenSubCategoryWasNotFound() throws Exception{
    BDDMockito.when(subCategoryRepository.findBySubCategoryName(anyString())).thenReturn(Optional.empty());

    mockMvc.perform(get("/sub-categories/subcategory").accept(JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }

  @Test
  public void getAllSubCategories_Return200StatusCode_WhenSuccessful() throws Exception{
    mockMvc.perform(get("/sub-categories?page=0&size=10").accept(JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(JSON));
  }

  @Test
  public void getAllSubCategories_Return404StatusCode_WhenNoSubCategoryAreFound() throws Exception{
    BDDMockito.when(subCategoryRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of()));

    mockMvc.perform(get("/sub-categories?page=0&size=10").accept(JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(JSON));
  }
}
