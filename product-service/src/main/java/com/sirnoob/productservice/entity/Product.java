package  com.sirnoob.productservice.entity;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "products")
public class Product {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "product_id", updatable = false)
  private Long productId;
    
  @Column(name = "product_bar_code", nullable = false, unique = true)
  private Long productBarCode;

  @Column(name = "product_name", nullable = false, unique = true, length = 30)
  private String productName;

  @Lob
  private String productDescription;
  
  @Column(name = "product_stock", nullable = false)
  private Integer productStock;
  
  @Column(name = "product_price", nullable = false)
  private Double productPrice;

  @Column(name = "create_at")
  private LocalDate createAt;

  @Column(name = "product_status")
  private String productStatus;
 

  @ManyToOne(fetch =FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private MainCategory mainCategory;

  @ManyToMany
  @JoinTable(name = "sub_categories_products", 
              joinColumns = { @JoinColumn(name = "fk_product") }, 
              inverseJoinColumns = { @JoinColumn(name = "fk_sub_category") })
  private Set<SubCategory> subCategories;
}
