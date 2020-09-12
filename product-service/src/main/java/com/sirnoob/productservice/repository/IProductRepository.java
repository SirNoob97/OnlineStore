package com.sirnoob.productservice.repository;

import java.util.List;
import java.util.Optional;

import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long>{

  @Query("SELECT new com.sirnoob.productservice.dto.ProductListView(p.productName, p.productDescription, p.productPrice) FROM Product p")
  public List<ProductListView> getAll(Pageable pageable);

  public Optional<Product> findByProductName(String productName);

  @Query("SELECT new com.sirnoob.productservice.dto.ProductListView(p.productName, p.productDescription, p.productPrice) FROM Product p WHERE p.productName LIKE :productName%")
  public List<ProductListView> listByName(@Param("productName") String productName, Pageable pageable);

  public Optional<Product> findByProductBarCode(Long productBarCode);

  public List<ProductListView> findByMainCategory(MainCategory mainCategory, Pageable pageable);

  @Query("SELECT new com.sirnoob.productservice.dto.ProductListView(p.productName, p.productDescription, p.productPrice) FROM Product p WHERE :subCategory MEMBER p.subCategories")
  public ProductListView findBySubCategory(@Param("subCategory") SubCategory subCategory);

  @Modifying
  @Query(value = "UPDATE products SET product_stock = :stock WHERE product_bar_code = :productBarCode", nativeQuery = true)
  public void updateProductStockByProductBarCode(@Param("stock") Integer stock, @Param("productBarCode") Long productBarCode);
}
