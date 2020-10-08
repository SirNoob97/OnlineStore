package com.sirnoob.authservice.controller;

import com.sirnoob.authservice.entity.User;
import com.sirnoob.authservice.repository.IUserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {


  private final IUserRepository iUserRepository;


  @GetMapping
  public ResponseEntity<Flux<User>> findAllUsers() {
    return ResponseEntity.ok().body(iUserRepository.findAll());
  }
}
