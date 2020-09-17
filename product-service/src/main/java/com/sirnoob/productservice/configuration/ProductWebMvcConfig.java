package com.sirnoob.productservice.configuration;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ProductWebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> handlerMethodArgumentResolvers){
    PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver = new PageableHandlerMethodArgumentResolver();
    pageableHandlerMethodArgumentResolver.setFallbackPageable(PageRequest.of(0, 10));
    handlerMethodArgumentResolvers.add(pageableHandlerMethodArgumentResolver);
  }
}
