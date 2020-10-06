package  com.sirnoob.productservice.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
  @Setter(AccessLevel.NONE)
  private Long productId;

  @Column(name = "product_bar_code", nullable = false, unique = true)
  private Long productBarCode;

  @Column(name = "product_name", nullable = false, unique = true, length = 130)
  private String productName;

  @Lob
  @Column(name = "product_description")
  private String productDescription;

  @Column(name = "product_stock", nullable = false)
  private Integer productStock;

  @Column(name = "product_price", nullable = false)
  private Double productPrice;


  @Column(name = "created_date", insertable = false, updatable = false)
  private LocalDate createDate;

  @Column(name = "last_modified_date", insertable = false)
  private LocalDateTime lastModifiedDate;


  @Column(name = "product_status")
  private String productStatus;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  @ManyToOne(fetch =FetchType.LAZY)
  @JoinColumn(name = "main_category_id")
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
