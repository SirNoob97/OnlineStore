package com.sirnoob.productservice.service;

import static com.sirnoob.productservice.util.RandomEntityGenerator.createMainCategoryStaticValues;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createProductRequest;
import static com.sirnoob.productservice.util.RandomEntityGenerator.createSubSetCategoryStaticValues;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;

import java.util.stream.Collectors;

import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.repository.IMainCategoryRepository;
import com.sirnoob.productservice.repository.IProductRepository;
import com.sirnoob.productservice.repository.ISubCategoryRepository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ProductServiceTest {


  @Autowired
  ISubCategoryRepository iSubCategoryRepository;

  @Autowired
  IMainCategoryRepository iMainCategoryRepository;

  @Autowired
  IProductRepository iProductRepository;

  @Autowired
  IProductService iProductService;

  @BeforeEach
  public void setUp(){
    BDDMockito.when(iMainCategoryRepository.save(any(MainCategory.class))).thenReturn(createMainCategoryStaticValues());
    BDDMockito.when(iSubCategoryRepository.saveAll(anySet())).thenReturn(createSubSetCategoryStaticValues().stream().collect(Collectors.toList()));
  }

  @Test
  public void name() {
    iMainCategoryRepository.save(createMainCategoryStaticValues());
    iSubCategoryRepository.saveAll(createSubSetCategoryStaticValues());
    ProductResponse productSaved = iProductService.createProduct(createProductRequest());

    Assertions.assertThat(productSaved).isNotNull();
  }
}
