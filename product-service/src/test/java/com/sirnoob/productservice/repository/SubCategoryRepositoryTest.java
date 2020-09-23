package com.sirnoob.productservice.repository;

import static com.sirnoob.productservice.util.RandomEntityGenerator.createSubCategory;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createMainCategory;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

@DataJpaTest
@DisplayName("Sub Category Repository Test")
class SubCategoryRepositoryTest {

  Logger log = LoggerFactory.getLogger(SubCategoryRepositoryTest.class);

  @Autowired
  private ISubCategoryRepository iSubCategoryRepository;
  @Autowired
  private IMainCategoryRepository iMainCategoryRepository;
  @Autowired
  private IProductRepository iProductRepository;


  @Test
  @DisplayName("Save creates sub category when successful")
  public void save_PersistSubCategory_WhenSuccessful() {
    MainCategory mainCategory = iMainCategoryRepository.save(createMainCategory());

    SubCategory subCategory = createSubCategory();

    subCategory.setMainCategory(mainCategory);

    SubCategory subCategorySaved = iSubCategoryRepository.save(subCategory);

    assertThat(subCategorySaved).isNotNull();
    assertThat(subCategorySaved.getSubCategoryId()).isNotEqualTo(subCategory.getSubCategoryId());
    assertThat(subCategorySaved.getSubCategoryName()).isEqualTo(subCategory.getSubCategoryName());
    assertThat(subCategorySaved.getMainCategory()).isEqualTo(mainCategory);
  }

  @Test
  @DisplayName("Save throw DataIntegrityViolationException when sub category is empty")
  public void save_ThrowDataIntegrityViolationException_WhenSubCategoryIsEmpty() {
    SubCategory subCategory = new SubCategory();

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> iSubCategoryRepository.save(subCategory));
  }

  @Test
  @DisplayName("Save throw DataIntegrityViolationException when the sub category doesnt have a main category")
  public void save_ThrowDataIntegrityViolationException_WhenTheSubCategoryDoesNotHaveAMainCategory() {
    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> iSubCategoryRepository.save(createSubCategory()));
  }

  @Test
  @DisplayName("Save creates sub category when the main category is null")
  public void save_ThrowDataIntegrityViolationException_WhenTheMainCategoryIsNull() {
    SubCategory subCategory = createSubCategory();

    subCategory.setMainCategory(null);

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> iSubCategoryRepository.save(createSubCategory()));
  }

  @Test
  @DisplayName("Save throw InvalidDataAccessApiUsageException when sub category is null")
  public void save_ThrowInvalidDataAccessApiUsageException_WhenSubCategoryIsNull() {
    assertThatExceptionOfType(InvalidDataAccessApiUsageException.class)
        .isThrownBy(() -> iSubCategoryRepository.save(null));
  }

  @Test
  @DisplayName("Save throw DataIntegrityViolationException when main category was not persisted")
  public void save_ThrowDataIntegriryViolationExCeption_WhenTheMainCategoryWasNotPersisted() {
    MainCategory mainCategory = createMainCategory();

    SubCategory subCategory = createSubCategory();

    subCategory.setMainCategory(mainCategory);

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> iSubCategoryRepository.save(subCategory));
  }

  @Test
  @DisplayName("Delete removes sub category when successful")
  public void delete_RemoveSubCategory_WhenSuccessful() {
    SubCategory subCategoryToDelete = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());

    iSubCategoryRepository.delete(subCategoryToDelete);

    Optional<SubCategory> subCategoryOptional = iSubCategoryRepository.findById(subCategoryToDelete.getSubCategoryId());

    assertThat(subCategoryToDelete.getSubCategoryId()).isNotNull();
    assertThat(subCategoryOptional.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Delete throw InvalidDataAccessApiUsageException when sub category is null")
  public void delete_ThrowInvalidDataAccessApiUsageException_WhenSubCategoryIsNull() {
    assertThatExceptionOfType(InvalidDataAccessApiUsageException.class).isThrownBy(() -> iSubCategoryRepository.delete(null));
  }

  @Test
  @DisplayName("Delete removes sub category when successful")
  public void delete_Throw_WhenASubCategoryRelatedToAProductIsRemoved() {
    SubCategory subCategoryToDelete = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategoryAndProducts());

    iSubCategoryRepository.delete(subCategoryToDelete);

    Optional<SubCategory> subCategorySaved = iSubCategoryRepository.findById(subCategoryToDelete.getSubCategoryId());

    assertThat(subCategorySaved.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Find By sub category Name return present sub category when successful")
  public void findBySubCategoryName_ReturnPresenwSubCategorywWhenSuccessful() {
    SubCategory subCategorySaved = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());

    String name = subCategorySaved.getSubCategoryName();

    Optional<SubCategory> subCategoryOptional = iSubCategoryRepository.findBySubCategoryName(name);

    assertThat(subCategoryOptional.isPresent()).isTrue();
    assertThat(subCategoryOptional.get().getSubCategoryName()).isEqualTo(name);
    assertThat(subCategoryOptional.get().getSubCategoryName()).isEqualTo(subCategorySaved.getSubCategoryName());
  }

  @Test
  @DisplayName("Find By sub category Name return empty sub category when name is null")
  public void findBySubCategoryName_ReturnEmptwSubCategorywWhenNameIsNull() {
    Optional<SubCategory> subCategoryOptional = iSubCategoryRepository.findBySubCategoryName(null);

    assertThat(subCategoryOptional.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Find By Sub Category Name return empty sub category when names not matches")
  public void findBySubCategoryName_ReturnEmptwSubCategorywWhenNamesNotMatches() {
    SubCategory subCategorySaved = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());

    Optional<SubCategory> subCategoryOptional = iSubCategoryRepository.findBySubCategoryName(subCategorySaved.getSubCategoryName() + "TEST");

    assertThat(subCategoryOptional.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Find By Main Category return a list of sub categories when successful")
  public void findByMainCategory_ReturnListOfSubCategories_WhenSuccessful(){
    SubCategory subCategory = createSubCategory();
    SubCategory subCategory2 = createSubCategory();
    SubCategory subCategory3 = createSubCategory();

    MainCategory mainCategory = iMainCategoryRepository.save(createMainCategory());

    subCategory.setMainCategory(mainCategory);
    subCategory2.setMainCategory(mainCategory);
    subCategory3.setMainCategory(mainCategory);

    iSubCategoryRepository.save(subCategory);
    iSubCategoryRepository.save(subCategory2);
    iSubCategoryRepository.save(subCategory3);

    List<SubCategory> subCategories = iSubCategoryRepository.findByMainCategory(mainCategory);

    assertThat(subCategories).isNotNull();
    assertThat(subCategories.isEmpty()).isFalse();
    assertThat(subCategories.size()).isEqualTo(3);
  }

  @Test
  @DisplayName("Find By Main Category return a empty list of sub categories when no sub category is related to that main category")
  public void findByMainCategory_ReturnEmptyListOfSubCategories_WhenNoSubCategoryIsRelatedToThatMainCategory(){
    SubCategory subCategory = createSubCategory();
    SubCategory subCategory2 = createSubCategory();
    SubCategory subCategory3 = createSubCategory();

    MainCategory mainCategory = iMainCategoryRepository.save(createMainCategory());
    MainCategory mainCategory2 = iMainCategoryRepository.save(createMainCategory());

    subCategory.setMainCategory(mainCategory);
    subCategory2.setMainCategory(mainCategory);
    subCategory3.setMainCategory(mainCategory);

    iSubCategoryRepository.save(subCategory);
    iSubCategoryRepository.save(subCategory2);
    iSubCategoryRepository.save(subCategory3);

    List<SubCategory> subCategories = iSubCategoryRepository.findByMainCategory(mainCategory2);

    assertThat(subCategories).isNotNull();
    assertThat(subCategories.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Find By Main Category return a empty list of sub categories when main category is null")
  public void findByMainCategory_ReturnEmptyListOfSubCategories_WhenMainCategoryIsNull(){
    List<SubCategory> subCategories = iSubCategoryRepository.findByMainCategory(null);

    assertThat(subCategories).isNotNull();
    assertThat(subCategories.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Update sub category name return an integer greater than zero when successful")
  public void updateSubCategoryName_ReturnAnIntegerGreaterThanZero_WhenSuccessful() {
    SubCategory subCategoryToUpdate = iSubCategoryRepository.save(createSubCategoryWithPersistedMainCategory());

    String name = subCategoryToUpdate.getSubCategoryName();

    Integer returnFromUpdateOperation = iSubCategoryRepository.updateSubCategoryName("TEST", subCategoryToUpdate.getSubCategoryId());

    Optional<SubCategory> subCategoryWithUpdatedName = iSubCategoryRepository.findById(subCategoryToUpdate.getSubCategoryId());

    assertThat(returnFromUpdateOperation).isGreaterThan(0);
    assertThat(subCategoryWithUpdatedName).isNotNull();
    assertThat(subCategoryWithUpdatedName.get().getSubCategoryName()).isNotEqualToIgnoringCase(name);
    assertThat(subCategoryWithUpdatedName.get().getSubCategoryName()).isEqualToIgnoringCase("TEST");
  }

  @Test
  @DisplayName("Update sub category name return zero when no sub category has that id")
  public void updateSubCategoryName_ReturnZero_WhenNwSubCategorywasThatId() {
    Integer returnFromUpdateOperation = iSubCategoryRepository.updateSubCategoryName("TEST", -1L);

    assertThat(returnFromUpdateOperation).isNotNull();
    assertThat(returnFromUpdateOperation).isEqualTo(0);
  }

  @Test
  @DisplayName("Update sub category name return zero when sub category id is null")
  public void updateSubCategoryName_ReturnZero_WhewSubCategorywdIsNull() {
    Integer returnFromUpdateOperation = iSubCategoryRepository.updateSubCategoryName("TEST", null);

    assertThat(returnFromUpdateOperation).isNotNull();
    assertThat(returnFromUpdateOperation).isEqualTo(0);
  }

  private SubCategory createSubCategoryWithPersistedMainCategory(){
    SubCategory subCategory = createSubCategory();

    MainCategory mainCategory = iMainCategoryRepository.save(createMainCategory());

    subCategory.setMainCategory(mainCategory);

    return subCategory;
  }

  private SubCategory createSubCategoryWithPersistedMainCategoryAndProducts(){
    SubCategory subCategory = createSubCategoryWithPersistedMainCategory();

    Product product = iProductRepository.save(createProduct());
    Product product2 = iProductRepository.save(createProduct());
    Product product3 = iProductRepository.save(createProduct());
    Product product4 = iProductRepository.save(createProduct());

    subCategory.setProducts(Set.of(product, product2, product3, product4));

    return subCategory;
  }
}
