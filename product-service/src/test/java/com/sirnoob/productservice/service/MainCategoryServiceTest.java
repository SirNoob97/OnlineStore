package com.sirnoob.productservice.service;

import static com.sirnoob.productservice.util.RandomEntityGenerator.createMainCategoryStaticValues;
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
@DisplayName("Main Category Service Test")
public class MainCategoryServiceTest {

  private static final String MAIN_CATEGORY_NOT_FOUND = "Main Category Not Found";
  private static final String NO_MAIN_CATEGORIES_FOUND = "No Main Categories Found";
  private static final String MAIN_CATEGORY_FOR_DELETE_METHOD_TEST = "Main Category for delete method test";

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
    MainCategory mainCategory = iMainCategoryRepository
      .save(MainCategory.builder().mainCategoryName(MAIN_CATEGORY_FOR_DELETE_METHOD_TEST).build());

    SubCategory subCategory = iSubCategoryRepository
      .save(SubCategory.builder().mainCategory(mainCategory).subCategoryName("Sub Category for main category test").build());

    iProductRepository.save(RandomEntityGenerator.createProductWithMainCategoryAndSubCategory(mainCategory, subCategory));
  }

  @Test
  @DisplayName("createMainCategory create a new main category log when successful")
  public void createMainCategory_CreateMainCategory_WhenSuccessful() {
    MainCategory mainCategory = iMainCategoryService.createMainCategory(createMainCategoryStaticValues());

    assertThat(mainCategory).isNotNull();
    assertThat(mainCategory.getMainCategoryName()).isEqualTo("Main Category");
  }

  @Test
  @DisplayName("getMainCategoryById return a main category when successful")
  public void getMainCategoryById_ReturnAMainCategory_WhenSuccessful() {
    MainCategory mainCategory = iMainCategoryService.createMainCategory(createMainCategoryStaticValues());

    MainCategory mainCategoryFetchedById = iMainCategoryService.getMainCategoryById(mainCategory.getMainCategoryId());

    assertThat(mainCategory).isNotNull();
    assertThat(mainCategoryFetchedById).isNotNull();
    assertThat(mainCategory.getMainCategoryName()).isEqualTo(mainCategoryFetchedById.getMainCategoryName());
  }

  @Test
  @DisplayName("getMainCategoryById throw ResourceNotFoundException when main category was not found")
  public void getMainCategoryById_ThrowResourceNotFoundEsception_WhenMainCategoryWasNotFound() {
    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> iMainCategoryService.getMainCategoryById(-1L))
      .withMessage(MAIN_CATEGORY_NOT_FOUND);
  }

  @Test
  @DisplayName("updateMainCategoryName update the name of an existing main category when successful")
  public void updateMainCategoryName_UpdateTheNameOfAnExistingMainCategory_WhenSuccessful() {
    String newName = "TEST";

    MainCategory mainCategory = iMainCategoryService.createMainCategory(createMainCategoryStaticValues());

    iMainCategoryService.updateMainCategoryName(mainCategory.getMainCategoryId(), newName);

    MainCategory mainCategoryFetchedById = iMainCategoryService.getMainCategoryById(mainCategory.getMainCategoryId());

    assertThat(mainCategory).isNotNull();
    assertThat(mainCategoryFetchedById).isNotNull();
    assertThat(mainCategory.getMainCategoryName()).isNotEqualTo(newName);
    assertThat(mainCategoryFetchedById.getMainCategoryName()).isEqualTo(newName);
  }

  @Test
  @DisplayName("updateMainCategoryName throw ResourceNotFoundException when the return of the query is less than one")
  public void updateMainCategoryName_ThrowResourceNotFoundEsception_WhenTheReturnOfTheQueryIsLessThanOne() {
    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iMainCategoryService.updateMainCategoryName(-1L, "")).withMessage(MAIN_CATEGORY_NOT_FOUND);
  }

  @Test
  @DisplayName("deleteMainCategory delete a main category log when successful")
  public void deleteMainCategory_DeleteMainCategory_WhenSuccessful() {
    MainCategory mainCategory = iMainCategoryService.createMainCategory(createMainCategoryStaticValues());

    assertThatCode(() -> iMainCategoryService.deleteMainCategory(mainCategory.getMainCategoryId())).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("deleteMainCategory delete main category even if it is related to products and sub categories when successful")
  public void deleteMainCategory_DeleteMainCategoryEvenIfItIsRelatedToProductsAndSubCategories_WhenSuccessful() {
    Optional<MainCategory> mainCategory = iMainCategoryRepository.findByMainCategoryName(MAIN_CATEGORY_FOR_DELETE_METHOD_TEST);

    Set<SubCategory> subCategories = iSubCategoryService.getSubCategoryByMainCategory(mainCategory.get());

    List<Product> products = iProductService.getProductByMainCategory(mainCategory.get().getMainCategoryId());

    assertThat(subCategories).isNotNull();
    assertThat(products).isNotNull();
    assertThat(subCategories.isEmpty()).isFalse();
    assertThat(products.isEmpty()).isFalse();
    assertThatCode(() -> iMainCategoryService.deleteMainCategory(mainCategory.get().getMainCategoryId()))
      .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("deleteMainCategory delete main category even if the sub categories belonging to this have a null set of products when successful")
  public void deleteMainCategory_DeleteMainCategoryEvenIfTheSubCategoriesBelongingToThisHaveANullSetOfProducts_WhenSuccessful() {
    Optional<MainCategory> mainCategory = iMainCategoryRepository.findByMainCategoryName(MAIN_CATEGORY_FOR_DELETE_METHOD_TEST);

    Set<SubCategory> subCategories = iSubCategoryService.getSubCategoryByMainCategory(mainCategory.get());

    subCategories.forEach(sc -> sc.setProducts(null));

    List<Product> products = iProductService.getProductByMainCategory(mainCategory.get().getMainCategoryId());

    assertThat(subCategories).isNotNull();
    assertThat(products).isNotNull();
    assertThat(subCategories.isEmpty()).isFalse();
    assertThat(products.isEmpty()).isFalse();
    assertThatCode(() -> iMainCategoryService.deleteMainCategory(mainCategory.get().getMainCategoryId()))
      .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("deleteMainCategory delete main category even if the products belonging to it have a null set of sub categories when successful")
  public void deleteMainCategory_RemoveMainCategoryEvenIfTheProductsBelongingToItHaveANullSetOfSubCategories_WhenSuccessful() {
    Optional<MainCategory> mainCategory = iMainCategoryRepository.findByMainCategoryName(MAIN_CATEGORY_FOR_DELETE_METHOD_TEST);

    Set<SubCategory> subCategories = iSubCategoryService.getSubCategoryByMainCategory(mainCategory.get());

    List<Product> products = iProductService.getProductByMainCategory(mainCategory.get().getMainCategoryId());

    products.forEach(pr -> pr.setSubCategories(null));

    assertThat(subCategories).isNotNull();
    assertThat(products).isNotNull();
    assertThat(subCategories.isEmpty()).isFalse();
    assertThat(products.isEmpty()).isFalse();
    assertThatCode(() -> iMainCategoryService.deleteMainCategory(mainCategory.get().getMainCategoryId()))
      .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("getMainCategoryByName return a main category when successful")
  public void getMainCategoryByName_ReturnAMainCategory_WhenSuccessful() {
    MainCategory mainCategory = iMainCategoryService.createMainCategory(createMainCategoryStaticValues());

    MainCategory mainCategoryFetchedByName = iMainCategoryService.getMainCategoryByName(mainCategory.getMainCategoryName());

    assertThat(mainCategory).isNotNull();
    assertThat(mainCategoryFetchedByName).isNotNull();
    assertThat(mainCategory.getMainCategoryName()).isEqualTo(mainCategoryFetchedByName.getMainCategoryName());
  }

  @Test
  @DisplayName("getMainCategoryByName throw ResourceNotFoundException when the return of the query is less than one")
  public void getMainCategoryByName_ThrowResourceNotFoundEsception_WhenTheReturnOfTheQueryIsLessThanOne() {
    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iMainCategoryService.getMainCategoryByName("@#$%%*&")).withMessage(MAIN_CATEGORY_NOT_FOUND);
  }

  @Test
  @DisplayName("getAllMainCategory return a set of string when successful")
  public void getAllMainCategory_ReturnASetOfString_WhenSuccessful() {
    MainCategory mainCategory = iMainCategoryService.createMainCategory(createMainCategoryStaticValues());

    Set<String> mainCategories = iMainCategoryService.getAllMainCategory(PageRequest.of(0, 10));

    assertThat(mainCategory).isNotNull();
    assertThat(mainCategories).isNotNull();
    assertThat(mainCategories.isEmpty()).isFalse();
    assertThat(mainCategories.contains(mainCategory.getMainCategoryName())).isTrue();
  }

  @Test
  @DisplayName("getAllMainCategory throw ResourceNotFoundException when there are no main categories in the registry")
  public void getAllMainCategory_ThrowResourceNotFoundEsception_WhenThereAreNoMainCategoriessInTheRegistry() {
    iSubCategoryRepository.deleteAll();
    iProductRepository.deleteAll();
    iMainCategoryRepository.deleteAll();

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> iMainCategoryService.getAllMainCategory(PageRequest.of(0, 10))).withMessage(NO_MAIN_CATEGORIES_FOUND);
  }
}
