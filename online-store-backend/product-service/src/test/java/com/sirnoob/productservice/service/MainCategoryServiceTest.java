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
import com.sirnoob.productservice.repository.MainCategoryRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class MainCategoryServiceTest {

  private static final String MAIN_CATEGORY_NOT_FOUND = "Main Category Not Found";
  private static final String NO_MAIN_CATEGORIES_FOUND = "No Main Categories Found";

  @Mock
  private MainCategoryRepository mainCategoryRepository;

  @Mock
  private ProductService productService;

  @Mock
  private SubCategoryService subCategoryService;

  private MainCategoryService mainCategoryService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mainCategoryService = new MainCategoryServiceImpl(productService, subCategoryService, mainCategoryRepository);

    MainCategory mainCategory = createMainCategoryStaticValues();

    List<SubCategory> subCategories = List.of(createSubCategory());

    List<Product> products = List.of(createProduct());

    PageImpl<MainCategory> mainCategories = new PageImpl<>(List.of(mainCategory));

    BDDMockito.when(mainCategoryRepository.save(any(MainCategory.class))).thenReturn(mainCategory);

    BDDMockito.when(mainCategoryRepository.updateName(anyString(), anyLong())).thenReturn(1);

    BDDMockito.doNothing().when(mainCategoryRepository).delete(any(MainCategory.class));

    BDDMockito.when(subCategoryService.getMainCategory(anyLong())).thenReturn(subCategories);

    BDDMockito.when(productService.getByMainCategory(anyLong())).thenReturn(products);

    BDDMockito.when(mainCategoryRepository.findById(anyLong())).thenReturn(Optional.of(mainCategory));

    BDDMockito.when(mainCategoryRepository.findByMainCategoryName(anyString())).thenReturn(Optional.of(mainCategory));

    BDDMockito.when(mainCategoryRepository.findAll(any(Pageable.class))).thenReturn(mainCategories);
  }

  @Test
  public void createMainCategory_CreateMainCategory_WhenSuccessful() {
    MainCategory mainCategory = mainCategoryService.create(createMainCategoryStaticValues());

    assertThat(mainCategory).isNotNull();
    assertThat(mainCategory.getMainCategoryId()).isNotNull();
    assertThat(mainCategory.getMainCategoryName()).isNotNull();
  }

  @Test
  public void updateMainCategoryName_UpdateTheNameOfAnExistingMainCategory_WhenSuccessful() {
    assertThatCode(() -> mainCategoryService.updateName(1L, "TEST")).doesNotThrowAnyException();
  }

  @Test
  public void updateMainCategoryName_ThrowResourceNotFoundEsception_WhenTheReturnOfTheQueryIsLessThanOne() {
    BDDMockito.when(mainCategoryRepository.updateName(anyString(), anyLong()))
        .thenThrow(new ResourceNotFoundException(MAIN_CATEGORY_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> mainCategoryService.updateName(-1L, "")).withMessage(MAIN_CATEGORY_NOT_FOUND);
  }

  @Test
  public void deleteMainCategory_DeleteMainCategory_WhenSuccessful() {
    assertThatCode(() -> mainCategoryService.deleteById(1L)).doesNotThrowAnyException();
  }

  @Test
  public void getMainCategoryById_ReturnAMainCategory_WhenSuccessful() {
    MainCategory mainCategoryFetchedById = mainCategoryService.getById(1L);

    assertThat(mainCategoryFetchedById).isNotNull();
    assertThat(mainCategoryFetchedById.getMainCategoryId()).isNotNull();
    assertThat(mainCategoryFetchedById.getMainCategoryName()).isNotNull();
  }

  @Test
  public void getMainCategoryById_ThrowResourceNotFoundEsception_WhenMainCategoryWasNotFound() {
    BDDMockito.when(mainCategoryRepository.findById(anyLong()))
        .thenThrow(new ResourceNotFoundException(MAIN_CATEGORY_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> mainCategoryService.getById(-1L))
      .withMessage(MAIN_CATEGORY_NOT_FOUND);
  }

  @Test
  public void getMainCategoryByName_ReturnAMainCategory_WhenSuccessful() {
    MainCategory mainCategoryFetchedByName = mainCategoryService.getByName("TEST");

    assertThat(mainCategoryFetchedByName).isNotNull();
    assertThat(mainCategoryFetchedByName.getMainCategoryId()).isNotNull();
    assertThat(mainCategoryFetchedByName.getMainCategoryName()).isNotNull();
  }

  @Test
  public void getMainCategoryByName_ThrowResourceNotFoundEsception_WhenTheMainCategoryWasNotFound() {
    BDDMockito.when(mainCategoryRepository.findByMainCategoryName(anyString()))
        .thenThrow(new ResourceNotFoundException(MAIN_CATEGORY_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> mainCategoryService.getByName("@#$%%*&")).withMessage(MAIN_CATEGORY_NOT_FOUND);
  }

  @Test
  public void getAllMainCategory_ReturnASetOfString_WhenSuccessful() {
    Set<String> mainCategories = mainCategoryService.getAll(PageRequest.of(0, 10));

    assertThat(mainCategories).isNotNull();
    assertThat(mainCategories.isEmpty()).isFalse();
  }

  @Test
  public void getAllMainCategory_ThrowResourceNotFoundEsception_WhenThereAreNoMainCategoriessInTheRegistry() {
    BDDMockito.when(mainCategoryRepository.findAll(PageRequest.of(0, 10)))
        .thenThrow(new ResourceNotFoundException(NO_MAIN_CATEGORIES_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> mainCategoryService.getAll(PageRequest.of(0, 10))).withMessage(NO_MAIN_CATEGORIES_FOUND);
  }
}
