package com.sirnoob.shoppingservice.repository;

import com.sirnoob.shoppingservice.entity.Item;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long>{
}
