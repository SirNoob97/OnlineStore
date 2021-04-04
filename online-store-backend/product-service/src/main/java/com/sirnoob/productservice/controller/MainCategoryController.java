package com.sirnoob.productservice.controller;

import java.util.Set;

import javax.validation.Valid;

import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.service.MainCategoryService;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/main-categories")
public class MainCategoryController {

  private final MainCategoryService mainCategoryService;

  private static final MediaType JSON = MediaType.APPLICATION_JSON;

  @PostMapping
  public ResponseEntity<MainCategory> create(@Valid @RequestBody MainCategory mainCategory){
    return ResponseEntity.status(HttpStatus.CREATED).contentType(JSON).body(mainCategoryService.create(mainCategory));
  }

  @PutMapping("/{mainCategoryId}")
  public ResponseEntity<Void> update(@PathVariable Long mainCategoryId, @RequestParam(required = true) String mainCategoryName){
    mainCategoryService.updateName(mainCategoryId, mainCategoryName);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{mainCategoryId}")
  public ResponseEntity<Void> delete(@PathVariable Long mainCategoryId){
    mainCategoryService.deleteById(mainCategoryId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{mainCategoryName}")
  public ResponseEntity<MainCategory> getByName(@PathVariable String mainCategoryName){
    return ResponseEntity.ok().contentType(JSON).body(mainCategoryService.getByName(mainCategoryName));
  }

  @GetMapping
  public ResponseEntity<Set<String>> getAll(Pageable pageable){
    return ResponseEntity.ok().contentType(JSON).body(mainCategoryService.getAll(pageable));
  }
}
