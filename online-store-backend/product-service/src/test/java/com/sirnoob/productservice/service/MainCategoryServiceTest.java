package com.sirnoob.productservice.service;

import static com.sirnoob.productservice.util.RandomEntityGenerator.createMainCategoryStaticValues;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProduct;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createSubCategory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;
import com.sirnoob.productservice.exception.ResourceNotFoundException;
import com.sirnoob.productservice.repository.IMainCategoryRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("Main Category Service Test")
public class MainCategoryServiceTest {

  private static final String MAIN_CATEGORY_NOT_FOUND = "Main Category Not Found";
  private static final String NO_MAIN_CATEGORIES_FOUND = "No Main Categories Found";

  @Mock
  private IMainCategoryRepository iMainCategoryRepository;

  @Mock
  private IProductService iProductService;

  @Mock
  private ISubCategoryService iSubCategoryService;

  private IMainCategoryService iMainCategoryService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    iMainCategoryService = new MainCategoryServiceImpl(iProductService, iSubCategoryService, iMainCategoryRepository);

    MainCategory mainCategory = createMainCategoryStaticValues();

    List<SubCategory> subCategories = List.of(createSubCategory());

    List<Product> products = List.of(createProduct());

    PageImpl<MainCategory> mainCategories = new PageImpl<>(List.of(mainCategory));

    BDDMockito.when(iMainCategoryRepository.save(any(MainCategory.class))).thenReturn(mainCategory);

    BDDMockito.when(iMainCategoryRepository.updateMainCategoryName(anyString(), anyLong())).thenReturn(1);

    BDDMockito.doNothing().when(iMainCategoryRepository).delete(any(MainCategory.class));

    BDDMockito.when(iSubCategoryService.getSubCategoryByMainCategory(anyLong())).thenReturn(subCategories);

    BDDMockito.when(iProductService.getProductByMainCategory(anyLong())).thenReturn(products);

    BDDMockito.when(iMainCategoryRepository.findById(anyLong())).thenReturn(Optional.of(mainCategory));

    BDDMockito.when(iMainCategoryRepository.findByMainCategoryName(anyString())).thenReturn(Optional.of(mainCategory));

    BDDMockito.when(iMainCategoryRepository.findAll(any(Pageable.class))).thenReturn(mainCategories);
  }

  @Test
  @DisplayName("createMainCategory create a new main category log when successful")
  public void createMainCategory_CreateMainCategory_WhenSuccessful() {
    MainCategory mainCategory = iMainCategoryService.createMainCategory(createMainCategoryStaticValues());

    assertThat(mainCategory).isNotNull();
    assertThat(mainCategory.getMainCategoryId()).isNotNull();
    assertThat(mainCategory.getMainCategoryName()).isNotNull();
  }

  @Test
  @DisplayName("updateMainCategoryName update the name of an existing main category when successful")
  public void updateMainCategoryName_UpdateTheNameOfAnExistingMainCategory_WhenSuccessful() {
    assertThatCode(() -> iMainCategoryService.updateMainCategoryName(1L, "TEST")).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("updateMainCategoryName throw ResourceNotFoundException when the return of the query is less than one")
  public void updateMainCategoryName_ThrowResourceNotFoundEsception_WhenTheReturnOfTheQueryIsLessThanOne() {
    BDDMockito.when(iMainCategoryRepository.updateMainCategoryName(anyString(), anyLong()))
        .thenThrow(new ResourceNotFoundException(MAIN_CATEGORY_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iMainCategoryService.updateMainCategoryName(-1L, "")).withMessage(MAIN_CATEGORY_NOT_FOUND);
  }

  @Test
  @DisplayName("deleteMainCategory delete a main category log when successful")
  public void deleteMainCategory_DeleteMainCategory_WhenSuccessful() {
    assertThatCode(() -> iMainCategoryService.deleteMainCategory(1L)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("getMainCategoryById return a main category when successful")
  public void getMainCategoryById_ReturnAMainCategory_WhenSuccessful() {
    MainCategory mainCategoryFetchedById = iMainCategoryService.getMainCategoryById(1L);

    assertThat(mainCategoryFetchedById).isNotNull();
    assertThat(mainCategoryFetchedById.getMainCategoryId()).isNotNull();
    assertThat(mainCategoryFetchedById.getMainCategoryName()).isNotNull();
  }

  @Test
  @DisplayName("getMainCategoryById throw ResourceNotFoundException when main category was not found")
  public void getMainCategoryById_ThrowResourceNotFoundEsception_WhenMainCategoryWasNotFound() {
    BDDMockito.when(iMainCategoryRepository.findById(anyLong()))
        .thenThrow(new ResourceNotFoundException(MAIN_CATEGORY_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> iMainCategoryService.getMainCategoryById(-1L))
      .withMessage(MAIN_CATEGORY_NOT_FOUND);
  }

  @Test
  @DisplayName("getMainCategoryByName return a main category when successful")
  public void getMainCategoryByName_ReturnAMainCategory_WhenSuccessful() {
    MainCategory mainCategoryFetchedByName = iMainCategoryService.getMainCategoryByName("TEST");

    assertThat(mainCategoryFetchedByName).isNotNull();
    assertThat(mainCategoryFetchedByName.getMainCategoryId()).isNotNull();
    assertThat(mainCategoryFetchedByName.getMainCategoryName()).isNotNull();
  }

  @Test
  @DisplayName("getMainCategoryByName throw ResourceNotFoundException when the main category was not found")
  public void getMainCategoryByName_ThrowResourceNotFoundEsception_WhenTheMainCategoryWasNotFound() {
    BDDMockito.when(iMainCategoryRepository.findByMainCategoryName(anyString()))
        .thenThrow(new ResourceNotFoundException(MAIN_CATEGORY_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iMainCategoryService.getMainCategoryByName("@#$%%*&")).withMessage(MAIN_CATEGORY_NOT_FOUND);
  }

  @Test
  @DisplayName("getAllMainCategory return a set of string when successful")
  public void getAllMainCategory_ReturnASetOfString_WhenSuccessful() {
    Set<String> mainCategories = iMainCategoryService.getAllMainCategory(PageRequest.of(0, 10));

    assertThat(mainCategories).isNotNull();
    assertThat(mainCategories.isEmpty()).isFalse();
  }

  @Test
  @DisplayName("getAllMainCategory throw ResourceNotFoundException when there are no main categories in the registry")
  public void getAllMainCategory_ThrowResourceNotFoundEsception_WhenThereAreNoMainCategoriessInTheRegistry() {
    BDDMockito.when(iMainCategoryRepository.findAll(PageRequest.of(0, 10)))
        .thenThrow(new ResourceNotFoundException(NO_MAIN_CATEGORIES_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iMainCategoryService.getAllMainCategory(PageRequest.of(0, 10))).withMessage(NO_MAIN_CATEGORIES_FOUND);
  }
}
