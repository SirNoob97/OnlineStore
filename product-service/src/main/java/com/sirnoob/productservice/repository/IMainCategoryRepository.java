package com.sirnoob.productservice.repository;

import java.util.Optional;

import com.sirnoob.productservice.entity.MainCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IMainCategoryRepository extends JpaRepository<MainCategory, Long>{

  public Optional<MainCategory> findByMainCategoryName(String categoryName);

  @Modifying(clearAutomatically = true)
  @Query(value = "UPDATE main_categories SET category_name = :categoryName WHERE category_id = :categoryId", nativeQuery = true)
  public int updateMainCategoryName(@Param("categoryName") String categoryName, @Param("categoryId") Long categoryId);
}
