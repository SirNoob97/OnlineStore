package com.sirnoob.productservice.repository;

import java.util.List;
import java.util.Optional;

import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.SubCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ISubCategoryRepository extends JpaRepository<SubCategory, Long>{

  public Optional<SubCategory> findBySubCategoryName(String subCategoryName);

  public List<SubCategory> findByMainCategory(MainCategory subCategory);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE SubCategory AS s SET s.subCategoryName = :subCategoryName WHERE s.subCategoryId = :subCategoryId")
  public int updateSubCategoryName(@Param("subCategoryName") String subCategoryName, @Param("subCategoryId") Long subCategoryId);
}
