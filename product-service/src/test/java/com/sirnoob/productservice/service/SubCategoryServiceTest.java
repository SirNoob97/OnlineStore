package com.sirnoob.productservice.service;

import static com.sirnoob.productservice.util.RandomEntityGenerator.createSubSetCategoryStaticValues;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;
import com.sirnoob.productservice.exception.ResourceNotFoundException;
import com.sirnoob.productservice.repository.IMainCategoryRepository;
import com.sirnoob.productservice.repository.IProductRepository;
import com.sirnoob.productservice.repository.ISubCategoryRepository;
import com.sirnoob.productservice.util.RandomEntityGenerator;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@DisplayName("Sub Category Service Test")
public class SubCategoryServiceTest {

  private static final String SUB_CATEGORY_NOT_FOUND = "Sub Category Not Found";
  private static final String NO_SUB_CATEGORIES_FOUND = "No Sub Categories Found";
  private static final String MAIN_CATEGORY_NAME = "Main Category for delete method test";
  private static final String TEST = "TEST";
  private static final String SUB_CATEGORY_1 = "Sub Category 1";

  @Autowired
  IProductRepository iProductRepository;

  @Autowired
  IProductService iProductService;

  @Autowired
  ISubCategoryRepository iSubCategoryRepository;

  @Autowired
  ISubCategoryService iSubCategoryService;

  @Autowired
  IMainCategoryRepository iMainCategoryRepository;

  @Autowired
  IMainCategoryService iMainCategoryService;

  @BeforeEach
  public void setUp() {
    MainCategory mainCategory = iMainCategoryRepository.save(MainCategory.builder().mainCategoryName(MAIN_CATEGORY_NAME).build());
    SubCategory subCategory = iSubCategoryRepository.save(createSubSetCategoryStaticValues(1, mainCategory));
    iProductRepository.save(RandomEntityGenerator.createProductWithMainCategoryAndSubCategory(mainCategory, subCategory));
  }

  @Test
  @DisplayName("createSubCategory create a new sub category log when successful")
  public void createMainCategory_CreateMainCategory_WhenSuccessful() {
    Optional<MainCategory> mainCategory = iMainCategoryRepository.findByMainCategoryName(MAIN_CATEGORY_NAME);

    SubCategoryResponse subCategory = iSubCategoryService.createSubCategory(TEST, mainCategory.get());

    assertThat(subCategory).isNotNull();
    assertThat(subCategory.getSubCategoryName()).isEqualTo(TEST);
  }

  @Test
  @DisplayName("getSubCategoryByName return a sub category when successful")
  public void getSubCategoryByName_ReturnAMainCategory_WhenSuccessful() {
    SubCategory subCategoryFetchedByName = iSubCategoryService.getSubCategoryByName(SUB_CATEGORY_1);

    assertThat(subCategoryFetchedByName).isNotNull();
    assertThat(subCategoryFetchedByName.getSubCategoryName()).isEqualTo(SUB_CATEGORY_1);
  }

  @Test
  @DisplayName("getSubCategoryByName throw ResourceNotFoundException when sub category was not found")
  public void getSubCategoryByName_ThrowResourceNotFoundEsception_WhenSubCategoryWasNotFound() {
    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iSubCategoryService.getSubCategoryByName("@#$%%*&")).withMessage(SUB_CATEGORY_NOT_FOUND);
  }

  @Test
  @DisplayName("getSubCategoryById return a sub category when successful")
  public void getSubCategoryById_ReturnAMainCategory_WhenSuccessful() {
    SubCategory subCategory = iSubCategoryRepository.findBySubCategoryName(SUB_CATEGORY_1).get();

    SubCategory subCategoryFetchedById = iSubCategoryService.getSubCategoryById(subCategory.getSubCategoryId());

    assertThat(subCategory).isNotNull();
    assertThat(subCategoryFetchedById).isNotNull();
    assertThat(subCategory.getSubCategoryName()).isEqualTo(subCategoryFetchedById.getSubCategoryName());
  }

  @Test
  @DisplayName("getSubCategoryById throw ResourceNotFoundException when sub category was not found")
  public void getSubCategoryById_ThrowResourceNotFoundEsception_WhenSubCategoryWasNotFound() {
    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> iSubCategoryService.getSubCategoryById(-1L))
      .withMessage(SUB_CATEGORY_NOT_FOUND);
  }

  @Test
  @DisplayName("updateSubCategoryName update the name of an existing sub category when successful")
  public void updateSubCategoryName_UpdateTheNameOfAnExistingSubCategory_WhenSuccessful() {
    SubCategory subCategory = iSubCategoryRepository.findBySubCategoryName(SUB_CATEGORY_1).get();

    iSubCategoryService.updateSubCategoryName(subCategory.getSubCategoryId(), TEST);

    Optional<SubCategory> subCategoryFetchedById = iSubCategoryRepository.findById(subCategory.getSubCategoryId());

    assertThat(subCategory).isNotNull();
    assertThat(subCategoryFetchedById).isNotNull();
    assertThat(subCategory.getSubCategoryName()).isNotEqualTo(TEST);
    assertThat(subCategoryFetchedById.get().getSubCategoryName()).isEqualTo(TEST);
  }

  @Test
  @DisplayName("updateSubCategoryName throw ResourceNotFoundException when the return of the query is less than one")
  public void updateSubCategoryName_ThrowResourceNotFoundEsception_WhenTheReturnOfTheQueryIsLessThanOne() {
    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iSubCategoryService.updateSubCategoryName(-1L, "")).withMessage(SUB_CATEGORY_NOT_FOUND);
  }

  @Test
  @DisplayName("deleteSubCategory delete sub category even if it is related to products and sub categories when successful")
  public void deleteSubCategory_DeleteSubCategoryEvenIfItIsRelatedToProducts_WhenSuccessful() {
    SubCategory subCategory = iSubCategoryRepository.findBySubCategoryName(SUB_CATEGORY_1).get();

    List<Product> products = iProductService.getProductByMainCategory(subCategory.getMainCategory().getMainCategoryId());

    assertThat(subCategory).isNotNull();
    assertThat(products).isNotNull();
    assertThat(products.isEmpty()).isFalse();
    assertThatCode(() -> iSubCategoryService.deleteSubCategory(subCategory.getSubCategoryId())).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("deleteSubCategory delete sub category even though it has a null product set when successful")
  public void deleteSubCategory_DeleteSubCategoryEvenThoughItHasANullProductSet_WhenSuccessful() {
    SubCategory subCategory = iSubCategoryRepository.findBySubCategoryName(SUB_CATEGORY_1).get();

    List<Product> products = iProductService.getProductByMainCategory(subCategory.getMainCategory().getMainCategoryId());

    subCategory.setProducts(null);

    assertThat(subCategory).isNotNull();
    assertThat(products).isNotNull();
    assertThat(products.isEmpty()).isFalse();
    assertThatCode(() -> iSubCategoryService.deleteSubCategory(subCategory.getSubCategoryId())).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("deleteSubCategory delete main category even if the products have null the set of sub categories when successful")
  public void deleteSubCategory_RemoveSubCategoryEvenIfTheProductsHaveNullTheSetOfSubCategories_WhenSuccessful() {
    SubCategory subCategory = iSubCategoryRepository.findBySubCategoryName(SUB_CATEGORY_1).get();

    List<Product> products = iProductService.getProductByMainCategory(subCategory.getMainCategory().getMainCategoryId());

    products.forEach(pr -> pr.setSubCategories(null));

    assertThat(subCategory).isNotNull();
    assertThat(products).isNotNull();
    assertThat(products.isEmpty()).isFalse();
    assertThatCode(() -> iSubCategoryService.deleteSubCategory(subCategory.getSubCategoryId())).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("getSubCategoryResponseByName return a sub category response when successful")
  public void getSubCategoryResponseByName_ReturnASubCategoryResponse_WhenSuccessful() {
    SubCategoryResponse subCategoryFetchedByName = iSubCategoryService.getSubCategoryResponseByName(SUB_CATEGORY_1);

    assertThat(subCategoryFetchedByName).isNotNull();
    assertThat(subCategoryFetchedByName.getSubCategoryName()).isEqualTo(SUB_CATEGORY_1);
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
    iSubCategoryRepository.deleteAll();
    iProductRepository.deleteAll();
    iMainCategoryRepository.deleteAll();

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iSubCategoryService.getAllSubCategories(PageRequest.of(0, 10))).withMessage(NO_SUB_CATEGORIES_FOUND);
  }

  @Test
  @DisplayName("getSubcategoriesByName return a set of sub category when successful")
  public void getSubcategoriesByName_ReturnASetSubCategory_WhenSuccessful() {
    Optional<MainCategory> mainCategory = iMainCategoryRepository.findByMainCategoryName(MAIN_CATEGORY_NAME);

    SubCategory subCategory = iSubCategoryRepository.save(createSubSetCategoryStaticValues(2, mainCategory.get()));
    iSubCategoryRepository.save(createSubSetCategoryStaticValues(3, mainCategory.get()));

    String[] names = { "Sub Category 1", "Sub Category 2", "Sub Category 3" };

    Set<SubCategory> subCategoriesFetchedByName = iSubCategoryService.getSubcategoriesByName(names);

    assertThat(subCategoriesFetchedByName).isNotNull();
    assertThat(subCategoriesFetchedByName.isEmpty()).isFalse();
    assertThat(subCategoriesFetchedByName.contains(subCategory)).isTrue();
  }
  
  @Test
  @DisplayName("getSubCategoryByMainCategory return a list of sub category when successful")
  public void getSubCategoryByMainCategory_ReturnAListSubCategory_WhenSuccessful() {
    Optional<MainCategory> mainCategory = iMainCategoryRepository.findByMainCategoryName(MAIN_CATEGORY_NAME);
    
    List<SubCategory> subCategoriesFetchedByName = iSubCategoryService.getSubCategoryByMainCategory(mainCategory.get().getMainCategoryId());

    assertThat(subCategoriesFetchedByName).isNotNull();
    assertThat(subCategoriesFetchedByName.isEmpty()).isFalse();
  }
  
  @Test
  @DisplayName("getSubCategoryByMainCategory return an empty list of sub category when successful")
  public void getSubCategoryByMainCategory_ReturnAnEmptyListSubCategory_WhenSuccessful() {
    List<SubCategory> subCategoriesFetchedByName = iSubCategoryService.getSubCategoryByMainCategory(-1L);

    assertThat(subCategoriesFetchedByName).isNotNull();
    assertThat(subCategoriesFetchedByName.isEmpty()).isTrue();
  }

}
