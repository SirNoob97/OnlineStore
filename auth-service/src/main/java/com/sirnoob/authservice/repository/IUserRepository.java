package com.sirnoob.authservice.repository;

import com.sirnoob.authservice.entity.User;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface IUserRepository extends ReactiveCrudRepository<User, Long>{

}
