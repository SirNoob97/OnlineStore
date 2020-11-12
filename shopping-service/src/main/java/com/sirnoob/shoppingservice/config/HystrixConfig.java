package com.sirnoob.shoppingservice.config;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HystrixConfig {

  @Bean
  public ServletRegistrationBean<HystrixMetricsStreamServlet> getServlet() {
    HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
    ServletRegistrationBean<HystrixMetricsStreamServlet> registrationBean = new ServletRegistrationBean<HystrixMetricsStreamServlet>(
        streamServlet);

    registrationBean.setLoadOnStartup(1);
    registrationBean.addUrlMappings("/actuator/hystrix.stream");
    registrationBean.setName("hystrix.stream");

    return registrationBean;
  }
}
