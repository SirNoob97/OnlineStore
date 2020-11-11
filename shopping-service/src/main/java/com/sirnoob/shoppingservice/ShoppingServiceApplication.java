package com.sirnoob.shoppingservice;

import com.sirnoob.shoppingservice.entity.Item;
import com.sirnoob.shoppingservice.model.Product;
import com.sirnoob.shoppingservice.repository.IItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ShoppingServiceApplication {

  @Autowired
  private IItemRepository itemRepository;

  @Bean
  public void test (){
    Product product = Product.builder().productName("test").productPrice(2.50).build();
    Item item = Item.builder().quantity(10).product(product).build();
    
    Item newItem = itemRepository.save(item);
    
    product.setProductPrice(20);
    newItem.setQuantity(6);
    itemRepository.save(newItem);
  }

	public static void main(String[] args) {
		SpringApplication.run(ShoppingServiceApplication.class, args);
	}

}
