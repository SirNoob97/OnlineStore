package com.sirnoob.productservice.repository;

import com.sirnoob.productservice.entity.SubCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISubCategoryRepository extends JpaRepository<SubCategory, Long>{

  public SubCategory findBySubCategoryName(String subCategoryName);

  public List<SubCategory> findByMainCategoryMainCategoryId(Long mainCategoryId);
}
