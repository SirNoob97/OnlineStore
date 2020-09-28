package com.sirnoob.productservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sirnoob.productservice.entity.SubCategory;

@Repository
public interface ISubCategoryRepository extends JpaRepository<SubCategory, Long>{

  public Optional<SubCategory> findBySubCategoryName(String subCategoryName);

  public List<SubCategory> findByMainCategoryMainCategoryId(Long mainCategoryId);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE SubCategory AS s SET s.subCategoryName = :subCategoryName WHERE s.subCategoryId = :subCategoryId")
  public int updateSubCategoryName(@Param("subCategoryName") String subCategoryName, @Param("subCategoryId") Long subCategoryId);
}
