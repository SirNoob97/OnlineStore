package com.sirnoob.productservice.repository;

import com.sirnoob.productservice.entity.MainCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMainCategoryRepository extends JpaRepository<MainCategory, Long>{
  
  public MainCategory findByMainCategoryName(String categoryName);
}
