package com.sirnoob.productservice.controller;

import java.util.Set;

import javax.validation.Valid;

import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.entity.SubCategory;
import com.sirnoob.productservice.service.ISubCategoryService;

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
@RequestMapping("/sub-categories")
public class SubCategoryController {

  private final ISubCategoryService iSubCategoryService;

  @PostMapping
  public ResponseEntity<String> createSubCategory(@Valid @RequestBody SubCategory subCategory) {
    return ResponseEntity.status(HttpStatus.CREATED).body(iSubCategoryService.createSubCategory(subCategory));
  }

  @PutMapping("/{subCategoryId}")
  public ResponseEntity<String> updateSubCategoryName(@PathVariable Long subCategoryId,
      @RequestParam(required = true) String subCategoryName) {
    return iSubCategoryService.updateSubCategoryName(subCategoryId, subCategoryName) > 0
        ? ResponseEntity.noContent().build()
        : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sub Category NOT FOUND with id " + subCategoryId);
  }

  @DeleteMapping("/{subCategoryId}")
  public ResponseEntity<Void> deleteMainCategry(@PathVariable Long subCategoryId) {
    iSubCategoryService.deleteSubCategory(subCategoryId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<Set<String>> getAllSubCategories(@RequestParam(defaultValue = "0") int page) {
    return ResponseEntity.ok().body(iSubCategoryService.getAllSubCategories(page));
  }

  @GetMapping("/{subCategoryName}")
  public ResponseEntity<SubCategoryResponse> getSubCategoryResponseByName(@PathVariable String subCategoryName) {
    return ResponseEntity.ok().body(iSubCategoryService.getSubCategoryResponseByName(subCategoryName));
  }
}
