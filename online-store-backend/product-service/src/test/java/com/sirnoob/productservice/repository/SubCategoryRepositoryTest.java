package com.sirnoob.productservice.repository;

import static com.sirnoob.productservice.util.RandomEntityGenerator.createSubCategory;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createMainCategory;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Set;

import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

@DataJpaTest
class SubCategoryRepositoryTest {

  @Autowired
  private SubCategoryRepository subCategoryRepository;
  @Autowired
  private MainCategoryRepository mainCategoryRepository;
  @Autowired
  private ProductRepository productRepository;


  @Test
  public void save_PersistSubCategory_WhenSuccessful() {
    MainCategory mainCategory = mainCategoryRepository.save(createMainCategory());

    SubCategory subCategory = createSubCategory();

    subCategory.setMainCategory(mainCategory);

    SubCategory subCategorySaved = subCategoryRepository.save(subCategory);

    assertThat(subCategorySaved).isNotNull();
    assertThat(subCategorySaved.getSubCategoryId()).isNotEqualTo(subCategory.getSubCategoryId());
    assertThat(subCategorySaved.getSubCategoryName()).isEqualTo(subCategory.getSubCategoryName());
    assertThat(subCategorySaved.getMainCategory()).isEqualTo(mainCategory);
  }

  @Test
  public void save_ThrowDataIntegrityViolationException_WhenSubCategoryIsEmpty() {
    SubCategory subCategory = new SubCategory();

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> subCategoryRepository.save(subCategory));
  }

  @Test
  public void save_ThrowDataIntegrityViolationException_WhenTheSubCategoryDoesNotHaveAMainCategory() {
    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> subCategoryRepository.save(createSubCategory()));
  }

  @Test
  public void save_ThrowDataIntegrityViolationException_WhenTheMainCategoryIsNull() {
    SubCategory subCategory = createSubCategory();

    subCategory.setMainCategory(null);

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> subCategoryRepository.save(createSubCategory()));
  }

  @Test
  public void save_ThrowInvalidDataAccessApiUsageException_WhenSubCategoryIsNull() {
    assertThatExceptionOfType(InvalidDataAccessApiUsageException.class)
        .isThrownBy(() -> subCategoryRepository.save(null));
  }

  @Test
  public void save_ThrowDataIntegriryViolationExCeption_WhenTheMainCategoryWasNotPersisted() {
    MainCategory mainCategory = createMainCategory();

    SubCategory subCategory = createSubCategory();

    subCategory.setMainCategory(mainCategory);

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> subCategoryRepository.save(subCategory));
  }

  @Test
  public void delete_RemoveSubCategory_WhenSuccessful() {
    SubCategory subCategoryToDelete = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());

    subCategoryRepository.delete(subCategoryToDelete);

    var subCategoryOptional = subCategoryRepository.findById(subCategoryToDelete.getSubCategoryId());

    assertThat(subCategoryToDelete.getSubCategoryId()).isNotNull();
    assertThat(subCategoryOptional.isEmpty()).isTrue();
  }

  @Test
  public void delete_ThrowInvalidDataAccessApiUsageException_WhenSubCategoryIsNull() {
    assertThatExceptionOfType(InvalidDataAccessApiUsageException.class).isThrownBy(() -> subCategoryRepository.delete(null));
  }

  @Test
  public void delete_Throw_WhenASubCategoryRelatedToAProductIsRemoved() {
    SubCategory subCategoryToDelete = subCategoryRepository.save(createSubCategoryWithPersistedMainCategoryAndProducts());

    subCategoryRepository.delete(subCategoryToDelete);

    var subCategorySaved = subCategoryRepository.findById(subCategoryToDelete.getSubCategoryId());

    assertThat(subCategorySaved.isEmpty()).isTrue();
  }

  @Test
  public void findBySubCategoryName_ReturnPresenwSubCategorywWhenSuccessful() {
    SubCategory subCategorySaved = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());

    String name = subCategorySaved.getSubCategoryName();

    var subCategoryOptional = subCategoryRepository.findBySubCategoryName(name);

    assertThat(subCategoryOptional.isPresent()).isTrue();
    assertThat(subCategoryOptional.get().getSubCategoryName()).isEqualTo(name);
    assertThat(subCategoryOptional.get().getSubCategoryName()).isEqualTo(subCategorySaved.getSubCategoryName());
  }

  @Test
  public void findBySubCategoryName_ReturnEmptwSubCategorywWhenNameIsNull() {
    var subCategoryOptional = subCategoryRepository.findBySubCategoryName(null);

    assertThat(subCategoryOptional.isEmpty()).isTrue();
  }

  @Test
  public void findBySubCategoryName_ReturnEmptwSubCategorywWhenNamesNotMatches() {
    SubCategory subCategorySaved = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());

    var subCategoryOptional = subCategoryRepository.findBySubCategoryName(subCategorySaved.getSubCategoryName() + "TEST");

    assertThat(subCategoryOptional.isEmpty()).isTrue();
  }

  @Test
  public void findByMainCategory_ReturnListOfSubCategories_WhenSuccessful(){
    SubCategory subCategory = createSubCategory();
    SubCategory subCategory2 = createSubCategory();
    SubCategory subCategory3 = createSubCategory();

    MainCategory mainCategory = mainCategoryRepository.save(createMainCategory());

    subCategory.setMainCategory(mainCategory);
    subCategory2.setMainCategory(mainCategory);
    subCategory3.setMainCategory(mainCategory);

    subCategoryRepository.save(subCategory);
    subCategoryRepository.save(subCategory2);
    subCategoryRepository.save(subCategory3);

    var subCategories = subCategoryRepository.findByMainCategoryMainCategoryId(mainCategory.getMainCategoryId());

    assertThat(subCategories).isNotNull();
    assertThat(subCategories.isEmpty()).isFalse();
    assertThat(subCategories.size()).isEqualTo(3);
  }

  @Test
  public void findByMainCategory_ReturnEmptyListOfSubCategories_WhenNoSubCategoryIsRelatedToThatMainCategory(){
    SubCategory subCategory = createSubCategory();
    SubCategory subCategory2 = createSubCategory();
    SubCategory subCategory3 = createSubCategory();

    MainCategory mainCategory = mainCategoryRepository.save(createMainCategory());
    MainCategory mainCategory2 = mainCategoryRepository.save(createMainCategory());

    subCategory.setMainCategory(mainCategory);
    subCategory2.setMainCategory(mainCategory);
    subCategory3.setMainCategory(mainCategory);

    subCategoryRepository.save(subCategory);
    subCategoryRepository.save(subCategory2);
    subCategoryRepository.save(subCategory3);

    var subCategories = subCategoryRepository.findByMainCategoryMainCategoryId(mainCategory2.getMainCategoryId());

    assertThat(subCategories).isNotNull();
    assertThat(subCategories.isEmpty()).isTrue();
  }

  @Test
  public void findByMainCategory_ReturnEmptyListOfSubCategories_WhenMainCategoryIsNull(){
    var subCategories = subCategoryRepository.findByMainCategoryMainCategoryId(null);

    assertThat(subCategories).isNotNull();
    assertThat(subCategories.isEmpty()).isTrue();
  }

  @Test
  public void updateSubCategoryName_ReturnAnIntegerGreaterThanZero_WhenSuccessful() {
    SubCategory subCategoryToUpdate = subCategoryRepository.save(createSubCategoryWithPersistedMainCategory());

    String name = subCategoryToUpdate.getSubCategoryName();

    Integer returnFromUpdateOperation = subCategoryRepository.updateName("TEST", subCategoryToUpdate.getSubCategoryId());

    var subCategoryWithUpdatedName = subCategoryRepository.findById(subCategoryToUpdate.getSubCategoryId());

    assertThat(returnFromUpdateOperation).isGreaterThan(0);
    assertThat(subCategoryWithUpdatedName).isNotNull();
    assertThat(subCategoryWithUpdatedName.get().getSubCategoryName()).isNotEqualToIgnoringCase(name);
    assertThat(subCategoryWithUpdatedName.get().getSubCategoryName()).isEqualToIgnoringCase("TEST");
  }

  @Test
  public void updateSubCategoryName_ReturnZero_WhenNwSubCategorywasThatId() {
    Integer returnFromUpdateOperation = subCategoryRepository.updateName("TEST", -1L);

    assertThat(returnFromUpdateOperation).isNotNull();
    assertThat(returnFromUpdateOperation).isEqualTo(0);
  }

  @Test
  public void updateSubCategoryName_ReturnZero_WhewSubCategorywdIsNull() {
    Integer returnFromUpdateOperation = subCategoryRepository.updateName("TEST", null);

    assertThat(returnFromUpdateOperation).isNotNull();
    assertThat(returnFromUpdateOperation).isEqualTo(0);
  }

  private SubCategory createSubCategoryWithPersistedMainCategory(){
    SubCategory subCategory = createSubCategory();

    MainCategory mainCategory = mainCategoryRepository.save(createMainCategory());

    subCategory.setMainCategory(mainCategory);

    return subCategory;
  }

  private SubCategory createSubCategoryWithPersistedMainCategoryAndProducts(){
    SubCategory subCategory = createSubCategoryWithPersistedMainCategory();

    Product product = productRepository.save(createProduct());
    Product product2 = productRepository.save(createProduct());
    Product product3 = productRepository.save(createProduct());
    Product product4 = productRepository.save(createProduct());

    subCategory.setProducts(Set.of(product, product2, product3, product4));

    return subCategory;
  }
}
