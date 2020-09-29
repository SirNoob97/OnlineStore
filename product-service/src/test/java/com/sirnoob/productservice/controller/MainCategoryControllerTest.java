package com.sirnoob.productservice.controller;

import static com.sirnoob.productservice.util.RandomEntityGenerator.createMainCategory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.Set;

import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.service.IMainCategoryService;

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
@DisplayName("Main Category Controller Test")
class MainCategoryControllerTest {

  @InjectMocks
  private MainCategoryController mainCategoryController;

  @Mock
  private IMainCategoryService iMainCategoryService;

  @BeforeEach
  public void setUp() {
    MainCategory mainCategory = createMainCategory();

    Set<String> mainCategoryPage = Set.of(mainCategory.getMainCategoryName());

    BDDMockito.when(iMainCategoryService.createMainCategory(any(MainCategory.class))).thenReturn(mainCategory);

    BDDMockito.doNothing().when(iMainCategoryService).updateMainCategoryName(anyLong(), anyString());

    BDDMockito.doNothing().when(iMainCategoryService).deleteMainCategory(anyLong());

    BDDMockito.when(iMainCategoryService.getMainCategoryByName(anyString())).thenReturn(mainCategory);

    BDDMockito.when(iMainCategoryService.getAllMainCategory(any(Pageable.class))).thenReturn(mainCategoryPage);
  }

  @Test
  @DisplayName("createMainCategory return a main category when successful")
  public void createMainCategory_ReturnAMainCategory_WhenSuccessful() {
    ResponseEntity<MainCategory> mainCategory = mainCategoryController.createMainCategory(createMainCategory());

    assertThat(mainCategory).isNotNull();
    assertThat(mainCategory.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(mainCategory.getBody()).isNotNull();
  }

  @Test
  @DisplayName("updateMainCategory update the name of a main category when successful")
  public void updateMainCategory_UpdateTheNameOfAMainCategory_WhenSuccessful() {
    ResponseEntity<Void> responseEntity = mainCategoryController.updateMainCategory(1L, "category");

    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(responseEntity.getBody()).isNull();
  }

  @Test
  @DisplayName("deleteMainCategory delete a main category when successful")
  public void deleteMainCategory_DelteAMainCategory_WhenSuccessful() {
    ResponseEntity<Void> responseEntity = mainCategoryController.deleteMainCategory(1L);

    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(responseEntity.getBody()).isNull();
  }

  @Test
  @DisplayName("getMainCategoryByName return a main category when successful")
  public void getMainCategoryByName_ResturnAMainCategory_WhenSuccesful() {
    ResponseEntity<MainCategory> mainCategory = mainCategoryController.getMainCategoryByName("name");

    assertThat(mainCategory).isNotNull();
    assertThat(mainCategory.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(mainCategory.getBody()).isNotNull();
  }

  @Test
  @DisplayName("getAllMainCategory return a set of main categories names when successful")
  public void getAllMainCategory_ReturnASetOfMainCategoriesNamesWhenSuccessful(){
    ResponseEntity<Set<String>> names = mainCategoryController.getAllMainCategory(PageRequest.of(0, 10));

    assertThat(names).isNotNull();
    assertThat(names.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(names.getBody()).isNotNull();
    assertThat(names.getBody().isEmpty()).isFalse();
  }
}
