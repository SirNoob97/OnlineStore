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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
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

  @Column(name = "product_name", nullable = false, unique = true, length = 130)
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

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JoinColumn(name = "category_id")
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  @ManyToOne(fetch =FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private MainCategory mainCategory;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonIgnoreProperties("products")
  @ManyToMany
  @JoinTable(name = "sub_categories_products",
              joinColumns = { @JoinColumn(name = "fk_product") },
              inverseJoinColumns = { @JoinColumn(name = "fk_sub_category") })
  private Set<SubCategory> subCategories;
}
