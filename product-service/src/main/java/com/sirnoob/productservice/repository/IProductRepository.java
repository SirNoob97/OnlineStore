package com.sirnoob.productservice.repository;

import java.util.List;
import java.util.Optional;

import com.sirnoob.productservice.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long>{

  public Optional<Product> findByProductName(String productName);

  public Optional<Product> findByProductBarCode(Long productBarCode);

  @Query(value = "SELECT * FROM products WHERE category_id = :categoryId")
  public List<Product> findByMainCategoryId(@Param("categoryId") Long categoryId);

  @Modifying
  @Query(value = "UPDATE products SET product_stock = :stock, product_status = 'UPDATED' WHERE product_bar_code = :productBarCode", nativeQuery = true)
  public void updateProductStockForProductBarCode(@Param("stock") Integer stock, @Param("productBarCode") Long productBarCode);
}
