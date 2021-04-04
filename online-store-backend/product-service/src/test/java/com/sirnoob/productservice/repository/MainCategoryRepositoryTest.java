package com.sirnoob.productservice.repository;

import static com.sirnoob.productservice.util.RandomEntityGenerator.createMainCategory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import javax.validation.ConstraintViolationException;

import com.sirnoob.productservice.entity.MainCategory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

@DataJpaTest
class MainCategoryRepositoryTest {

  @Autowired
  private MainCategoryRepository mainCategoryRepository;

  @Test
  public void save_PersistMainCategory_WhenSuccessful() {
    MainCategory mainCategory = createMainCategory();
    MainCategory mainCategorySaved = mainCategoryRepository.save(mainCategory);

    assertThat(mainCategorySaved).isNotNull();
    assertThat(mainCategorySaved.getMainCategoryId()).isNotEqualTo(mainCategory.getMainCategoryId());
    assertThat(mainCategorySaved.getMainCategoryName()).isEqualTo(mainCategory.getMainCategoryName());
  }

  @Test
  public void save_ThrowConstraintViolationException_WhenMainCategoryIsEmpty() {
    MainCategory mainCategory = new MainCategory();

    assertThatExceptionOfType(ConstraintViolationException.class)
        .isThrownBy(() -> mainCategoryRepository.save(mainCategory));
  }

  @Test
  public void save_ThrowInvalidDataAccessApiUsageException_WhenMainCategoryIsNull() {
    assertThatExceptionOfType(InvalidDataAccessApiUsageException.class).isThrownBy(() -> mainCategoryRepository.save(null));
  }

  @Test
  public void delete_RemoveMainCategory_WhenSuccessful() {
    MainCategory mainCategoryToDelete = mainCategoryRepository.save(createMainCategory());
    mainCategoryRepository.delete(mainCategoryToDelete);
    var mainCategoryOptional = mainCategoryRepository.findById(mainCategoryToDelete.getMainCategoryId());

    assertThat(mainCategoryToDelete.getMainCategoryId()).isNotNull();
    assertThat(mainCategoryOptional.isEmpty()).isTrue();
  }

  @Test
  public void delete_ThrowInvalidDataAccessApiUsageException_WhenMainCategoryIsNull() {
    assertThatExceptionOfType(InvalidDataAccessApiUsageException.class).isThrownBy(() -> mainCategoryRepository.delete(null));
  }

  @Test
  public void findByMainCategoryName_ReturnPresentMainCategory_WhenSuccessful() {
    MainCategory mainCategorySaved = mainCategoryRepository.save(createMainCategory());
    String name = mainCategorySaved.getMainCategoryName();
    var mainCategoryOptional = mainCategoryRepository.findByMainCategoryName(name);

    assertThat(mainCategoryOptional.isPresent()).isTrue();
    assertThat(mainCategoryOptional.get().getMainCategoryName()).isEqualTo(name);
    assertThat(mainCategoryOptional.get().getMainCategoryName()).isEqualTo(mainCategorySaved.getMainCategoryName());
  }

  @Test
  public void findByMainCategoryName_ReturnEmptyMainCategory_WhenNameIsNull() {
    var mainCategoryOptional = mainCategoryRepository.findByMainCategoryName(null);

    assertThat(mainCategoryOptional.isEmpty()).isTrue();
  }

  @Test
  public void findByMainCategoryName_ReturnEmptyMainCategory_WhenNamesNotMatches() {
    MainCategory mainCategorySaved = mainCategoryRepository.save(createMainCategory());
    var mainCategoryOptional = mainCategoryRepository.findByMainCategoryName(mainCategorySaved.getMainCategoryName() + "TEST");

    assertThat(mainCategoryOptional.isEmpty()).isTrue();
  }

  @Test
  public void updateMainCategoryName_ReturnAnIntegerGreaterThanZero_WhenSuccessful() {
    MainCategory mainCategoryToUpdate = mainCategoryRepository.save(createMainCategory());
    String name = mainCategoryToUpdate.getMainCategoryName();
    Integer returnFromUpdateOperation = mainCategoryRepository.updateName("TEST", mainCategoryToUpdate.getMainCategoryId());
    var mainCategoryWithUpdatedName = mainCategoryRepository.findById(mainCategoryToUpdate.getMainCategoryId());

    assertThat(returnFromUpdateOperation).isGreaterThan(0);
    assertThat(mainCategoryWithUpdatedName).isNotNull();
    assertThat(mainCategoryWithUpdatedName.get().getMainCategoryName()).isNotEqualToIgnoringCase(name);
    assertThat(mainCategoryWithUpdatedName.get().getMainCategoryName()).isEqualToIgnoringCase("TEST");
  }

  @Test
  public void updateMainCategoryName_ReturnZero_WhenNoMainCategoryHasThatId() {
    Integer returnFromUpdateOperation = mainCategoryRepository.updateName("TEST", -1L);

    assertThat(returnFromUpdateOperation).isNotNull();
    assertThat(returnFromUpdateOperation).isEqualTo(0);
  }

  @Test
  public void updateMainCategoryName_ReturnZero_WhenMainCategoryIdIsNull() {
    Integer returnFromUpdateOperation = mainCategoryRepository.updateName("TEST", null);

    assertThat(returnFromUpdateOperation).isNotNull();
    assertThat(returnFromUpdateOperation).isEqualTo(0);
  }
}
