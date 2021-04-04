package com.sirnoob.productservice.service;

import static com.sirnoob.productservice.util.RandomEntityGenerator.createMainCategoryStaticValues;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createSubCategoryForDeleteTest;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createSubCategoryResponse;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createSubCategoryStaticValues;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.SubCategory;
import com.sirnoob.productservice.exception.ResourceNotFoundException;
import com.sirnoob.productservice.mapper.SubCategoryMapper;
import com.sirnoob.productservice.repository.SubCategoryRepository;

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
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class SubCategoryServiceTest {

  private static final String SUB_CATEGORY_NOT_FOUND = "Sub Category Not Found";
  private static final String NO_SUB_CATEGORIES_FOUND = "No Sub Categories Found";


  @Mock
  SubCategoryMapper subCategoryMapper;

  @Mock
  SubCategoryRepository subCategoryRepository;

  SubCategoryService subCategoryService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    subCategoryService = new SubCategoryServiceImpl(subCategoryMapper, subCategoryRepository);

    MainCategory mainCategory = createMainCategoryStaticValues();

    SubCategory subCategory = createSubCategoryStaticValues(1, mainCategory);

    PageImpl<SubCategory> subCategories = new PageImpl<>(List.of(subCategory));

    BDDMockito.when(subCategoryMapper.subCategoryRequestToSubCategory(anyString(), any(MainCategory.class))).thenReturn(subCategory);

    BDDMockito.when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(subCategory);

    BDDMockito.when(subCategoryMapper.subCategoryToSubCategoryResponse(any(SubCategory.class))).thenReturn(createSubCategoryResponse());

    BDDMockito.when(subCategoryRepository.updateName(anyString(), anyLong())).thenReturn(1);

    BDDMockito.doNothing().when(subCategoryRepository).delete(any(SubCategory.class));

    BDDMockito.when(subCategoryRepository.findById(anyLong())).thenReturn(Optional.of(subCategory));

    BDDMockito.when(subCategoryRepository.findBySubCategoryName(anyString())).thenReturn(Optional.of(subCategory));

    BDDMockito.when(subCategoryRepository.findAll(any(Pageable.class))).thenReturn(subCategories);

    BDDMockito.when(subCategoryRepository.findByMainCategoryMainCategoryId(anyLong())).thenReturn(List.of(subCategory));
  }

  @Test
  public void createMainCategory_CreateMainCategory_WhenSuccessful() {
    SubCategoryResponse subCategory = subCategoryService.create("Sub Category", createMainCategoryStaticValues());

    assertThat(subCategory).isNotNull();
    assertThat(subCategory.getSubCategoryId()).isNotNull();
    assertThat(subCategory.getSubCategoryName()).isEqualTo("Sub Category");
    assertThat(subCategory.getMainCategory()).isNotNull();
    assertThat(subCategory.getProducts()).isNotNull();
    assertThat(subCategory.getProducts().isEmpty()).isFalse();
  }

  @Test
  public void updateSubCategoryName_UpdateTheNameOfAnExistingSubCategory_WhenSuccessful() {
    assertThatCode(() -> subCategoryService.updateName(1L, "TEST")).doesNotThrowAnyException();
  }

  @Test
  public void updateSubCategoryName_ThrowResourceNotFoundEsception_WhenTheReturnOfTheQueryIsLessThanOne() {
    BDDMockito.when(subCategoryRepository.updateName(anyString(), anyLong()))
        .thenThrow(new ResourceNotFoundException(SUB_CATEGORY_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> subCategoryService.updateName(-1L, "")).withMessage(SUB_CATEGORY_NOT_FOUND);
  }

  @Test
  public void deleteSubCategory_DeleteSubCategoryEvenIfItIsRelatedToProducts_WhenSuccessful() {
    BDDMockito.when(subCategoryRepository.findById(anyLong())).thenReturn(Optional.of(createSubCategoryForDeleteTest()));

    assertThatCode(() -> subCategoryService.deleteById(1L)).doesNotThrowAnyException();
  }

  @Test
  public void deleteSubCategory_DeleteSubCategoryEvenThoughItHasANullProductSet_WhenSuccessful() {
    MainCategory mainCategory = createMainCategoryStaticValues();

    BDDMockito.when(subCategoryRepository.findById(anyLong())).thenReturn(Optional.of(createSubCategoryStaticValues(1, mainCategory)));

    assertThatCode(() -> subCategoryService.deleteById(1L)).doesNotThrowAnyException();
  }

  @Test
  public void deleteSubCategory_RemoveSubCategoryEvenIfTheProductsHaveNullTheSetOfSubCategories_WhenSuccessful() {
    SubCategory subCategory = createSubCategoryForDeleteTest();

    subCategory.getProducts().forEach(pr -> pr.setSubCategories(null));

    BDDMockito.when(subCategoryRepository.findById(anyLong())).thenReturn(Optional.of(subCategory));

    assertThatCode(() -> subCategoryService.deleteById(1L)).doesNotThrowAnyException();
  }

  @Test
  public void getSubCategoryById_ReturnAMainCategory_WhenSuccessful() {
    String expectedName = "Sub Category 1";

    SubCategory subCategoryFetchedById = subCategoryService.getById(1L);

    assertThat(subCategoryFetchedById).isNotNull();
    assertThat(subCategoryFetchedById.getSubCategoryName()).isEqualTo(expectedName);
    assertThat(subCategoryFetchedById.getMainCategory()).isNotNull();
  }

  @Test
  public void getSubCategoryById_ThrowResourceNotFoundEsception_WhenSubCategoryWasNotFound() {
    BDDMockito.when(subCategoryRepository.findById(anyLong())).thenThrow(new ResourceNotFoundException(SUB_CATEGORY_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> subCategoryService.getById(-1L))
      .withMessage(SUB_CATEGORY_NOT_FOUND);
  }

  @Test
  public void getSubCategoryResponseByName_ReturnASubCategoryResponse_WhenSuccessful() {
    String expectedName = "Sub Category";

    SubCategoryResponse subCategoryFetchedByName = subCategoryService.getSubCategoryResponseByName(expectedName);

    assertThat(subCategoryFetchedByName).isNotNull();
    assertThat(subCategoryFetchedByName.getSubCategoryName()).isEqualTo(expectedName);
    assertThat(subCategoryFetchedByName.getMainCategory()).isNotNull();
    assertThat(subCategoryFetchedByName.getProducts()).isNotNull();
    assertThat(subCategoryFetchedByName.getProducts().isEmpty()).isFalse();
  }

  @Test
  public void getSubCategoryByName_ReturnAMainCategory_WhenSuccessful() {
    String expectedName = "Sub Category 1";

    SubCategory subCategoryFetchedByName = subCategoryService.getByName(expectedName);

    assertThat(subCategoryFetchedByName).isNotNull();
    assertThat(subCategoryFetchedByName.getSubCategoryName()).isEqualTo(expectedName);
    assertThat(subCategoryFetchedByName.getMainCategory()).isNotNull();
  }

  @Test
  public void getSubCategoryByName_ThrowResourceNotFoundEsception_WhenSubCategoryWasNotFound() {
    BDDMockito.when(subCategoryRepository.findBySubCategoryName(anyString())).thenThrow(new ResourceNotFoundException(SUB_CATEGORY_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> subCategoryService.getByName("@#$%%*&")).withMessage(SUB_CATEGORY_NOT_FOUND);
  }

  @Test
  public void getAllSubCategories_ReturnASetOfString_WhenSuccessful() {
    Set<String> subCategories = subCategoryService.getAll(PageRequest.of(0, 10));

    assertThat(subCategories).isNotNull();
    assertThat(subCategories.isEmpty()).isFalse();
  }

  @Test
  public void getAllSubCategories_ThrowResourceNotFoundEsception_WhenThereAreNoSubCategoriessInTheRegistry() {
    BDDMockito.when(subCategoryRepository.findAll(any(Pageable.class))).thenThrow(new ResourceNotFoundException(NO_SUB_CATEGORIES_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> subCategoryService.getAll(PageRequest.of(0, 10))).withMessage(NO_SUB_CATEGORIES_FOUND);
  }

  @Test
  public void getSubcategoriesByName_ReturnASetSubCategory_WhenSuccessful() {
    String[] names = { "Sub Category 1", "Sub Category 2", "Sub Category 3" };

    Set<SubCategory> subCategoriesFetchedByName = subCategoryService.getSetByName(names);

    assertThat(subCategoriesFetchedByName).isNotNull();
    assertThat(subCategoriesFetchedByName.isEmpty()).isFalse();
  }

  @Test
  public void getSubCategoryByMainCategory_ReturnAListSubCategory_WhenSuccessful() {
    List<SubCategory> subCategories = subCategoryService.getMainCategory(1L);

    assertThat(subCategories).isNotNull();
    assertThat(subCategories.isEmpty()).isFalse();
  }

  @Test
  public void getSubCategoryByMainCategory_ReturnAnEmptyListSubCategory_WhenSuccessful() {
    BDDMockito.when(subCategoryRepository.findByMainCategoryMainCategoryId(anyLong())).thenReturn(List.of());

    List<SubCategory> subCategoriesFetchedByName = subCategoryService.getMainCategory(-1L);

    assertThat(subCategoriesFetchedByName).isNotNull();
    assertThat(subCategoriesFetchedByName.isEmpty()).isTrue();
  }

}
