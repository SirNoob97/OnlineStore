package com.sirnoob.productservice.repository;

import java.util.List;
import java.util.Optional;

import com.sirnoob.productservice.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long>{

  public Optional<Product> findByProductName(String productName);

  public Optional<Product> findByProductBarCode(Long productBarCode);

  public List<Product> findByMainCategoryMainCategoryId(Long categoryId);
}
