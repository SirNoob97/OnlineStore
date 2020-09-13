package com.sirnoob.productservice.controller;

import java.util.Set;

import javax.validation.Valid;

import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.service.IMainCategoryService;

import org.springframework.http.HttpStatus;
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

  private final IMainCategoryService iMainCategoryService;

  @PostMapping
  public ResponseEntity<String> createMainCategory(@Valid @RequestBody MainCategory mainCategory){
    return ResponseEntity.status(HttpStatus.CREATED).body(iMainCategoryService.createMainCategory(mainCategory));
  }

  @PutMapping("/{mainCategoryId}")
  public ResponseEntity<String> updateMainCategory(@PathVariable Long mainCategoryId, @RequestParam(required = true) String mainCategoryName){
    return iMainCategoryService.updateMainCategoryName(mainCategoryId, mainCategoryName) > 0 ? ResponseEntity.noContent().build()
                                                                                            : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category NOT FOUND with id " + mainCategoryId);
  }

  @DeleteMapping("/{mainCategoryId}")
  public ResponseEntity<Void> deleteMainCategry(@PathVariable Long mainCategoryId){
    iMainCategoryService.deleteMainCategry(mainCategoryId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{mainCategoryName}")
  public ResponseEntity<MainCategory> getMainCategoryByName(@PathVariable String mainCategoryName){
    return ResponseEntity.ok().body(iMainCategoryService.getMainCategoryByName(mainCategoryName));
  }

  @GetMapping
  public ResponseEntity<Set<String>> getAllMainCategory(@RequestParam(defaultValue = "0") int page){
    return ResponseEntity.ok().body(iMainCategoryService.getAllMainCategory(page));
  }
}
