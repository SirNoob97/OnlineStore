package com.sirnoob.productservice.repository;

import com.sirnoob.productservice.entity.SubCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ISubCategoryRepository extends JpaRepository<SubCategory, Long>{

  //@Query("SELECT sc.subCategoryId, sc.subCategoryName FROM SubCategory sc WHERE sc.subCategoryName = :subCategoryName")
  public Optional<SubCategory> findBySubCategoryName(@Param("subCategoryName") String subCategoryName);

  public List<SubCategory> findByMainCategoryMainCategoryId(Long mainCategoryId);
}
