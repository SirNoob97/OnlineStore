package com.sirnoob.productservice.configuration;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PageableWebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> hMAR) {
    var pHMAR = new PageableHandlerMethodArgumentResolver();
    pHMAR.setFallbackPageable(PageRequest.of(0, 10));
    hMAR.add(pHMAR);

  }
}
