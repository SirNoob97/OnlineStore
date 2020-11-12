package com.sirnoob.shoppingservice.client;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.sirnoob.shoppingservice.model.Product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", fallback = ProductHystrixFallback.class)
public interface IProductClient{

  //@HystrixCommand(commandProperties = {
    //@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
      //@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
      //@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
      //@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000")
  //}, threadPoolKey = "productInfoPool", threadPoolProperties = {
      //@HystrixProperty(name = "coreSize", value = "20"),
      //@HystrixProperty(name = "mazQueueSize", value = "10")
  //})
  @GetMapping("/products/invoices")
  public ResponseEntity<Product> getProductForInvoice(@RequestParam(required = true) Long productBarCode, @RequestParam(required = true) String productName);



  @HystrixCommand(commandProperties = {
    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
      @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
      @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
      @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000")
  }, threadPoolKey = "productUpdatePool", threadPoolProperties = {
      @HystrixProperty(name = "coreSize", value = "20"),
      @HystrixProperty(name = "mazQueueSize", value = "10")
  })
  @PutMapping("/products/{productBarCode}/stock")
  public ResponseEntity<Void> updateProductStock(@PathVariable Long productBarCode, @RequestParam(required = true) Integer quantity);
}
