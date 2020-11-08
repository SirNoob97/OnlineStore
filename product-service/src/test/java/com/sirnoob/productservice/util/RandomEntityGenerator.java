package com.sirnoob.productservice.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.dto.ProductView;
import com.sirnoob.productservice.dto.SubCategoryResponse;
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

  public static ProductInvoiceResponse createProductInvoiceResponse() {
    return ProductInvoiceResponse.builder().productName("Product").productPrice(10.95).build();
  }

  public static Product createProductWithMainCategoryAndSubCategory() {
    MainCategory mainCategory = createMainCategoryStaticValues();
    Set<SubCategory> subCategories = new HashSet<>();
    subCategories.add(createSubCategoryStaticValues(1, mainCategory));
    subCategories.add(createSubCategoryStaticValues(2, mainCategory));
    //@formatter:off
    return Product.builder()
                  .productId(1L)
                  .productName("Samsung Galaxy J7 (2016)")
                  .productBarCode(1023045090807L)
                  .productDescription("Color: Black, Camera: 13MP, Resolution: 720 x 1280")
                  .productStock(50)
                  .productPrice(199.99)
                  .productStatus("CREATED")
                  .mainCategory(mainCategory)
                  .subCategories(subCategories)
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

  public static ProductResponse createProductResponseStaticValues(){
    Set<String> subCategories = new HashSet<>();
    subCategories.add("Sub Category 1");
    subCategories.add("Sub Category 2");
    subCategories.add("Sub Category 3");
    //@formatter:off
    return ProductResponse.builder()
                          .productId(100L)
                          .productBarCode(1023045090807L)
                          .productName("Samsung Galaxy J7 (2016)")
                          .productDescription("Color: Black, Camera: 13MP, Resolution: 720 x 1280")
                          .productPrice(199.99)
                          .productStock(50)
                          .productStatus("CREATED")
                          .mainCategoryName("Main Category")
                          .subCategories(subCategories)
                          .build();
    //@formatter:on
  }

  public static ProductResponse createProductResponseStaticValuesForUpdateTest(){
    Set<String> subCategories = new HashSet<>();
    subCategories.add("Sub Category 1");
    subCategories.add("Sub Category 2");
    subCategories.add("Sub Category 3");
    //@formatter:off
    return ProductResponse.builder()
                          .productId(100L)
                          .productBarCode(1023045000000L)
                          .productName("Samsung Galaxy J7 (2000)")
                          .productDescription("Color: Black, Camera: 13MP, Resolution: 720 x 1280")
                          .productPrice(199.99)
                          .productStock(100)
                          .mainCategoryName("Main Category")
                          .subCategories(subCategories)
                          .build();
    //@formatter:on
  }

  public static Product createProductForSubCategoryServiceDeleteTest(){
    Set<SubCategory> subCategories = new HashSet<>();
    subCategories.add(createSubCategoryStaticValues(1, createMainCategoryStaticValues()));
    subCategories.add(createSubCategoryStaticValues(2, createMainCategoryStaticValues()));
    //@formatter:off
    return Product.builder()
                  .productId(100L)
                  .productBarCode(1023045000000L)
                  .productName("Samsung Galaxy J7 (2000)")
                  .productDescription("Color: Black, Camera: 13MP, Resolution: 720 x 1280")
                  .productPrice(199.99)
                  .productStock(100)
                  .mainCategory(createMainCategoryStaticValues())
                  .subCategories(subCategories)
                  .build();
    //@formatter:on
  }

  public static ProductListView createProductListViewStaticValues() {
    return new ProductListView("Samsung Galaxy J7 (2016)","Color: Black, Camera: 13MP, Resolution: 720 x 1280" ,199.99);
  }

  public static ProductView createProductViewStaticValues(){
    Set<String> subCategories = new HashSet<>();
    subCategories.add("Sub Category 1");
    subCategories.add("Sub Category 2");
    subCategories.add("Sub Category 3");
    //@formatter:off
    return ProductView.builder()
                          .productBarCode(1023045090807L)
                          .productName("Samsung Galaxy J7 (2016)")
                          .productDescription("Color: Black, Camera: 13MP, Resolution: 720 x 1280")
                          .productPrice(199.99)
                          .mainCategoryName("Main Category")
                          .subCategories(subCategories)
                          .build();
    //@formatter:on
  }

  public static MainCategory createMainCategoryStaticValues() {
    return MainCategory.builder().mainCategoryId(1L).mainCategoryName("Main Category").build();
  }

  public static SubCategory createSubCategoryStaticValues(int value, MainCategory mainCategory) {
    return SubCategory.builder().subCategoryName("Sub Category " + value).mainCategory(mainCategory).build();
  }

  public static SubCategoryResponse createSubCategoryResponse() {
    //@formatter:off
    return SubCategoryResponse.builder()
                              .subCategoryId(1L)
                              .subCategoryName("Sub Category")
                              .mainCategory("Main Category")
                              .products(Set.of("Product"))
                              .build();
    //@formatter:on
  }

  public static SubCategory createSubCategoryForDeleteTest(){
    Set<Product> products = new HashSet<>();
    products.add(createProductForSubCategoryServiceDeleteTest());
    products.add(createProductForSubCategoryServiceDeleteTest());
    //@formatter:off
    return SubCategory.builder()
                      .subCategoryId(1L)
                      .subCategoryName("Sub Category")
                      .mainCategory(createMainCategoryStaticValues())
                      .products(products)
                      .build();
    //@formatter:on
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
