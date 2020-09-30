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
import com.sirnoob.productservice.mapper.ISubCategoryMapper;
import com.sirnoob.productservice.repository.ISubCategoryRepository;

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
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@DisplayName("Sub Category Service Test")
public class SubCategoryServiceTest {

  private static final String SUB_CATEGORY_NOT_FOUND = "Sub Category Not Found";
  private static final String NO_SUB_CATEGORIES_FOUND = "No Sub Categories Found";


  @Mock
  ISubCategoryMapper iSubCategoryMapper;

  @Mock
  ISubCategoryRepository iSubCategoryRepository;

  ISubCategoryService iSubCategoryService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    iSubCategoryService = new SubCategoryServiceImpl(iSubCategoryMapper, iSubCategoryRepository);

    MainCategory mainCategory = createMainCategoryStaticValues();

    SubCategory subCategory = createSubCategoryStaticValues(1, mainCategory);

    PageImpl<SubCategory> subCategories = new PageImpl<>(List.of(subCategory));

    BDDMockito.when(iSubCategoryMapper.mapSubCategoryRequestToSubCategory(anyString(), any(MainCategory.class))).thenReturn(subCategory);

    BDDMockito.when(iSubCategoryRepository.save(any(SubCategory.class))).thenReturn(subCategory);

    BDDMockito.when(iSubCategoryMapper.mapSubCategoryToSubCategoryResponse(any(SubCategory.class))).thenReturn(createSubCategoryResponse());

    BDDMockito.when(iSubCategoryRepository.updateSubCategoryName(anyString(), anyLong())).thenReturn(1);

    BDDMockito.doNothing().when(iSubCategoryRepository).delete(any(SubCategory.class));

    BDDMockito.when(iSubCategoryRepository.findById(anyLong())).thenReturn(Optional.of(subCategory));

    BDDMockito.when(iSubCategoryRepository.findBySubCategoryName(anyString())).thenReturn(Optional.of(subCategory));

    BDDMockito.when(iSubCategoryRepository.findAll(any(Pageable.class))).thenReturn(subCategories);

    BDDMockito.when(iSubCategoryRepository.findByMainCategoryMainCategoryId(anyLong())).thenReturn(List.of(subCategory));
  }

  @Test
  @DisplayName("createSubCategory create a new sub category log when successful")
  public void createMainCategory_CreateMainCategory_WhenSuccessful() {
    SubCategoryResponse subCategory = iSubCategoryService.createSubCategory("Sub Category", createMainCategoryStaticValues());

    assertThat(subCategory).isNotNull();
    assertThat(subCategory.getSubCategoryId()).isNotNull();
    assertThat(subCategory.getSubCategoryName()).isEqualTo("Sub Category");
    assertThat(subCategory.getMainCategory()).isNotNull();
    assertThat(subCategory.getProducts()).isNotNull();
    assertThat(subCategory.getProducts().isEmpty()).isFalse();
  }

  @Test
  @DisplayName("updateSubCategoryName update the name of an existing sub category when successful")
  public void updateSubCategoryName_UpdateTheNameOfAnExistingSubCategory_WhenSuccessful() {
    assertThatCode(() -> iSubCategoryService.updateSubCategoryName(1L, "TEST")).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("updateSubCategoryName throw ResourceNotFoundException when the return of the query is less than one")
  public void updateSubCategoryName_ThrowResourceNotFoundEsception_WhenTheReturnOfTheQueryIsLessThanOne() {
    BDDMockito.when(iSubCategoryRepository.updateSubCategoryName(anyString(), anyLong()))
        .thenThrow(new ResourceNotFoundException(SUB_CATEGORY_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iSubCategoryService.updateSubCategoryName(-1L, "")).withMessage(SUB_CATEGORY_NOT_FOUND);
  }

  @Test
  @DisplayName("deleteSubCategory delete sub category even if it is related to products and sub categories when successful")
  public void deleteSubCategory_DeleteSubCategoryEvenIfItIsRelatedToProducts_WhenSuccessful() {
    BDDMockito.when(iSubCategoryRepository.findById(anyLong())).thenReturn(Optional.of(createSubCategoryForDeleteTest()));

    assertThatCode(() -> iSubCategoryService.deleteSubCategory(1L)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("deleteSubCategory delete sub category even though it has a null product set when successful")
  public void deleteSubCategory_DeleteSubCategoryEvenThoughItHasANullProductSet_WhenSuccessful() {
    MainCategory mainCategory = createMainCategoryStaticValues();

    BDDMockito.when(iSubCategoryRepository.findById(anyLong())).thenReturn(Optional.of(createSubCategoryStaticValues(1, mainCategory)));

    assertThatCode(() -> iSubCategoryService.deleteSubCategory(1L)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("deleteSubCategory delete main category even if the products have null the set of sub categories when successful")
  public void deleteSubCategory_RemoveSubCategoryEvenIfTheProductsHaveNullTheSetOfSubCategories_WhenSuccessful() {
    SubCategory subCategory = createSubCategoryForDeleteTest();

    subCategory.getProducts().forEach(pr -> pr.setSubCategories(null));

    BDDMockito.when(iSubCategoryRepository.findById(anyLong())).thenReturn(Optional.of(subCategory));

    assertThatCode(() -> iSubCategoryService.deleteSubCategory(1L)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("getSubCategoryById return a sub category when successful")
  public void getSubCategoryById_ReturnAMainCategory_WhenSuccessful() {
    String expectedName = "Sub Category 1";

    SubCategory subCategoryFetchedById = iSubCategoryService.getSubCategoryById(1L);

    assertThat(subCategoryFetchedById).isNotNull();
    assertThat(subCategoryFetchedById.getSubCategoryName()).isEqualTo(expectedName);
    assertThat(subCategoryFetchedById.getMainCategory()).isNotNull();
  }

  @Test
  @DisplayName("getSubCategoryById throw ResourceNotFoundException when sub category was not found")
  public void getSubCategoryById_ThrowResourceNotFoundEsception_WhenSubCategoryWasNotFound() {
    BDDMockito.when(iSubCategoryRepository.findById(anyLong())).thenThrow(new ResourceNotFoundException(SUB_CATEGORY_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> iSubCategoryService.getSubCategoryById(-1L))
      .withMessage(SUB_CATEGORY_NOT_FOUND);
  }

  @Test
  @DisplayName("getSubCategoryResponseByName return a sub category response when successful")
  public void getSubCategoryResponseByName_ReturnASubCategoryResponse_WhenSuccessful() {
    String expectedName = "Sub Category";

    SubCategoryResponse subCategoryFetchedByName = iSubCategoryService.getSubCategoryResponseByName(expectedName);

    assertThat(subCategoryFetchedByName).isNotNull();
    assertThat(subCategoryFetchedByName.getSubCategoryName()).isEqualTo(expectedName);
    assertThat(subCategoryFetchedByName.getMainCategory()).isNotNull();
    assertThat(subCategoryFetchedByName.getProducts()).isNotNull();
    assertThat(subCategoryFetchedByName.getProducts().isEmpty()).isFalse();
  }

  @Test
  @DisplayName("getSubCategoryByName return a sub category when successful")
  public void getSubCategoryByName_ReturnAMainCategory_WhenSuccessful() {
    String expectedName = "Sub Category 1";

    SubCategory subCategoryFetchedByName = iSubCategoryService.getSubCategoryByName(expectedName);

    assertThat(subCategoryFetchedByName).isNotNull();
    assertThat(subCategoryFetchedByName.getSubCategoryName()).isEqualTo(expectedName);
    assertThat(subCategoryFetchedByName.getMainCategory()).isNotNull();
  }

  @Test
  @DisplayName("getSubCategoryByName throw ResourceNotFoundException when sub category was not found")
  public void getSubCategoryByName_ThrowResourceNotFoundEsception_WhenSubCategoryWasNotFound() {
    BDDMockito.when(iSubCategoryRepository.findBySubCategoryName(anyString())).thenThrow(new ResourceNotFoundException(SUB_CATEGORY_NOT_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iSubCategoryService.getSubCategoryByName("@#$%%*&")).withMessage(SUB_CATEGORY_NOT_FOUND);
  }

  @Test
  @DisplayName("getAllSubCategories return a set of string when successful")
  public void getAllSubCategories_ReturnASetOfString_WhenSuccessful() {
    Set<String> subCategories = iSubCategoryService.getAllSubCategories(PageRequest.of(0, 10));

    assertThat(subCategories).isNotNull();
    assertThat(subCategories.isEmpty()).isFalse();
  }

  @Test
  @DisplayName("getAllSubCategories throw ResourceNotFoundException when there are no sub categories in the registry")
  public void getAllSubCategories_ThrowResourceNotFoundEsception_WhenThereAreNoSubCategoriessInTheRegistry() {
    BDDMockito.when(iSubCategoryRepository.findAll(any(Pageable.class))).thenThrow(new ResourceNotFoundException(NO_SUB_CATEGORIES_FOUND));

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iSubCategoryService.getAllSubCategories(PageRequest.of(0, 10))).withMessage(NO_SUB_CATEGORIES_FOUND);
  }

  @Test
  @DisplayName("getSubcategoriesByName return a set of sub category when successful")
  public void getSubcategoriesByName_ReturnASetSubCategory_WhenSuccessful() {
    String[] names = { "Sub Category 1", "Sub Category 2", "Sub Category 3" };

    Set<SubCategory> subCategoriesFetchedByName = iSubCategoryService.getSubcategoriesByName(names);

    assertThat(subCategoriesFetchedByName).isNotNull();
    assertThat(subCategoriesFetchedByName.isEmpty()).isFalse();
  }

  @Test
  @DisplayName("getSubCategoryByMainCategory return a list of sub category when successful")
  public void getSubCategoryByMainCategory_ReturnAListSubCategory_WhenSuccessful() {
    List<SubCategory> subCategories = iSubCategoryService.getSubCategoryByMainCategory(1L);

    assertThat(subCategories).isNotNull();
    assertThat(subCategories.isEmpty()).isFalse();
  }

  @Test
  @DisplayName("getSubCategoryByMainCategory return an empty list of sub category when successful")
  public void getSubCategoryByMainCategory_ReturnAnEmptyListSubCategory_WhenSuccessful() {
    BDDMockito.when(iSubCategoryRepository.findByMainCategoryMainCategoryId(anyLong())).thenReturn(List.of());

    List<SubCategory> subCategoriesFetchedByName = iSubCategoryService.getSubCategoryByMainCategory(-1L);

    assertThat(subCategoriesFetchedByName).isNotNull();
    assertThat(subCategoriesFetchedByName.isEmpty()).isTrue();
  }

}
