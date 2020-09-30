package com.sirnoob.productservice.controller;

import static com.sirnoob.productservice.util.RandomEntityGenerator.createMainCategoryStaticValues;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createSubCategoryResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.Set;

import com.sirnoob.productservice.dto.SubCategoryRequest;
import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.service.IMainCategoryService;
import com.sirnoob.productservice.service.ISubCategoryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DisplayName("Sub Category Controller Test")
class SubCategoryControllerTest {

  @InjectMocks
  private SubCategoryController subCategoryController;

  @Mock
  private ISubCategoryService iSubCategoryService;

  @Mock
  private IMainCategoryService iMainCategoryService;

  @BeforeEach
  public void setUp() {
    MainCategory mainCategory = createMainCategoryStaticValues();

    SubCategoryResponse subCategory = createSubCategoryResponse();

    Set<String> subCategories = Set.of(subCategory.getSubCategoryName());

    BDDMockito.when(iMainCategoryService.getMainCategoryByName(anyString())).thenReturn(mainCategory);

    BDDMockito.when(iSubCategoryService.createSubCategory(anyString(), any(MainCategory.class))).thenReturn(subCategory);

    BDDMockito.doNothing().when(iSubCategoryService).updateSubCategoryName(anyLong(), anyString());

    BDDMockito.doNothing().when(iSubCategoryService).deleteSubCategory(anyLong());

    BDDMockito.when(iSubCategoryService.getSubCategoryResponseByName(anyString())).thenReturn(subCategory);

    BDDMockito.when(iSubCategoryService.getAllSubCategories(any(Pageable.class))).thenReturn(subCategories);
  }

  @Test
  @DisplayName("createSubCategory return a sub category when successful")
  public void createSubCategoryCategory_ReturnASubCategory_WhenSuccessful() {
    ResponseEntity<SubCategoryResponse> subCategory = subCategoryController.createSubCategory(new SubCategoryRequest("Sub Category", "Main Category"));

    assertThat(subCategory).isNotNull();
    assertThat(subCategory.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(subCategory.getBody()).isNotNull();
  }

  @Test
  @DisplayName("updateSubCategoryNameCategory update the name of a sub category when successful")
  public void updateSubCategoryName_UpdateTheNameOfASubCategory_WhenSuccessful() {
    ResponseEntity<Void> responseEntity = subCategoryController.updateSubCategoryName(1L, "category");

    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(responseEntity.getBody()).isNull();
  }

  @Test
  @DisplayName("deleteSubCategory delete a sub category when successful")
  public void deleteSubCategory_DelteASubCategory_WhenSuccessful() {
    ResponseEntity<Void> responseEntity = subCategoryController.deleteSubCategory(1L);

    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(responseEntity.getBody()).isNull();
  }

  @Test
  @DisplayName("getSubCategoryResponseByNameCategoryByName return a sub category when successful")
  public void getSubCategoryResponseByNameCategoryByName_ResturnASubCategory_WhenSuccesful() {
    ResponseEntity<SubCategoryResponse> subCategory = subCategoryController.getSubCategoryResponseByName("name");

    assertThat(subCategory).isNotNull();
    assertThat(subCategory.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(subCategory.getBody()).isNotNull();
  }

  @Test
  @DisplayName("getAllSubCategoriesCategory return a set of sub categories names when successful")
  public void getAllSubCategories_ReturnASetOfSubCategoriesNamesWhenSuccessful(){
    ResponseEntity<Set<String>> names = subCategoryController.getAllSubCategories(PageRequest.of(0, 10));

    assertThat(names).isNotNull();
    assertThat(names.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(names.getBody()).isNotNull();
    assertThat(names.getBody().isEmpty()).isFalse();
  }
}
