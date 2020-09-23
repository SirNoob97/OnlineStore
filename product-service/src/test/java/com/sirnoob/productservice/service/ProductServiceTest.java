package com.sirnoob.productservice.service;

import java.util.List;

import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.repository.IProductRepository;
import com.sirnoob.productservice.util.RandomEntityGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {
  
  @Mock
  private IProductService iProductService;
  @Mock
  private IProductRepository iProductRepository;

  @BeforeEach
  public void setUp(){

    PageImpl<Product> productPage = new PageImpl<>(List.of(RandomEntityGenerator.createProduct()));
  }
}
