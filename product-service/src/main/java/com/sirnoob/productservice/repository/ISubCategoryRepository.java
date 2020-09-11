package com.sirnoob.productservice.repository;

import com.sirnoob.productservice.entity.SubCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ISubCategoryRepository extends JpaRepository<SubCategory, Long>{

  public Optional<SubCategory> findBySubCategoryName(String subCategoryName);

  public List<SubCategory> findByMainCategoryMainCategoryId(Long mainCategoryId);
}
