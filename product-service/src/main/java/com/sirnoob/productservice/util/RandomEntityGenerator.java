package com.sirnoob.productservice.util;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

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
                  .productPrice(new Random().nextDouble() * (new Random().nextInt(100) + 1))
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

  public static Long getRandomLongNumber() {
    Long rn = new Random().nextLong();
    return rn > 0 ? rn : rn * -1;
  }

  public static String getRandomString() {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }
}
