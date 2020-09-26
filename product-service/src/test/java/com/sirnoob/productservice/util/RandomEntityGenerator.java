package com.sirnoob.productservice.util;

import java.time.LocalDate;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;

public class RandomEntityGenerator {

  public static Product createProduct() {
    //@formatter:off
    return Product.builder()
                  .productId(getRandomLongNumber())
                  .productName(getRandomString())
                  .productBarCode(getRandomLongNumber())
                  .productDescription(getRandomString())
                  .productStock(new Random().nextInt(Integer.MAX_VALUE))
                  .productPrice(getRandomDoubleNumber())
                  .productStatus(getRandomString())
                  .createAt(LocalDate.now())
                  .build();
    //@formatter:on
  }

  public static MainCategory createMainCategory() {
    //@formatter:off
    return MainCategory.builder()
                       .mainCategoryId(getRandomLongNumber())
                       .mainCategoryName(getRandomString())
                       .build();
    //formatter:on
  }

  public static SubCategory createSubCategory(){
    //@formatter:off
    return SubCategory.builder()
                      .subCategoryId(getRandomLongNumber())
                      .subCategoryName(getRandomString())
                      .build();
    //@formatter:on
  }




  /** static values **/

  public static Product createProductWithMainCategoryAndSubCategory() {
    //@formatter:off
    return Product.builder()
                  .productId(1L)
                  .productName("Samsung Galaxy J7 (2016)")
                  .productBarCode(1023045090807L)
                  .productDescription("Color: Black, Camera: 13MP, Resolution: 720 x 1280")
                  .productStock(50)
                  .productPrice(199.99)
                  .productStatus("CREATED")
                  .createAt(LocalDate.now())
                  .mainCategory(createMainCategoryStaticValues())
                  .subCategories(createSubSetCategoryStaticValues())
                  .build();
    //@formatter:on
  }

  public static ProductRequest createProductRequest() {
    String[] subCategoriesNames = {"Sub Category 1", "Sub Category 2"};
    //@formatter:off
    return ProductRequest.builder()
                         .productBarCode(1023045090807L)
                         .productName("Samsung Galaxy J7 (2016)")
                         .productDescription("Color: Black, Camera: 13MP, Resolution: 720 x 1280")
                         .productPrice(199.99)
                         .productStock(50)
                         .mainCategoryName("Main Category")
                         .subCategoriesNames(subCategoriesNames)
                         .build();
    //@formatter:on
  }

  public static ProductListView createProductListView() {
    return new ProductListView("Samsung Galaxy J7 (2016)","Color: Black, Camera: 13MP, Resolution: 720 x 1280" ,199.99);
  }

  public static MainCategory createMainCategoryStaticValues() {
    return MainCategory.builder().mainCategoryId(1L).mainCategoryName("Main Category").build();
  }

  public static Set<SubCategory> createSubSetCategoryStaticValues() {
    return Set.of(SubCategory.builder().subCategoryId(1L).subCategoryName("Sub Category 1").mainCategory(createMainCategoryStaticValues()).build(),
                  SubCategory.builder().subCategoryId(2L).subCategoryName("Sub Category 2").mainCategory(createMainCategoryStaticValues()).build());
  }
  public static Long getRandomLongNumber() {
    Long rn = new Random().nextLong();
    return rn > 0 ? rn : rn * -1;
  }

  public static String getRandomString() {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }

  private static Double getRandomDoubleNumber() {
    return new Random().nextDouble() * (new Random().nextInt(100) + 1);
  }
}
