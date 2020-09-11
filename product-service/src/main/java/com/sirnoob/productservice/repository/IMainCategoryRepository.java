package com.sirnoob.productservice.repository;

import java.util.Optional;

import com.sirnoob.productservice.entity.MainCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMainCategoryRepository extends JpaRepository<MainCategory, Long>{
  
  public Optional<MainCategory> findByMainCategoryName(String categoryName);
}
