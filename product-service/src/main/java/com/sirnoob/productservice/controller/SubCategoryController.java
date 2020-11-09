package com.sirnoob.productservice.controller;

import java.util.Set;

import javax.validation.Valid;

import com.sirnoob.productservice.dto.SubCategoryRequest;
import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.service.IMainCategoryService;
import com.sirnoob.productservice.service.ISubCategoryService;

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
@RequestMapping("/sub-categories")
public class SubCategoryController {

  private static final MediaType JSON = MediaType.APPLICATION_JSON;

  private final IMainCategoryService iMainCategoryService;
  private final ISubCategoryService iSubCategoryService;

  @PostMapping
  public ResponseEntity<SubCategoryResponse> createSubCategory(@Valid @RequestBody SubCategoryRequest subCategoryRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).contentType(JSON)
      .body(iSubCategoryService.createSubCategory(subCategoryRequest.getSubCategoryName(),
        iMainCategoryService.getMainCategoryByName(subCategoryRequest.getMainCategoryName())));
  }

  @PutMapping("/{subCategoryId}")
  public ResponseEntity<Void> updateSubCategoryName(@PathVariable Long subCategoryId,
                                                    @RequestParam(required = true) String subCategoryName) {
    iSubCategoryService.updateSubCategoryName(subCategoryId, subCategoryName);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{subCategoryId}")
  public ResponseEntity<Void> deleteSubCategory(@PathVariable Long subCategoryId) {
    iSubCategoryService.deleteSubCategory(subCategoryId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<Set<String>> getAllSubCategories(Pageable pageable) {
    return ResponseEntity.ok().contentType(JSON).body(iSubCategoryService.getAllSubCategories(pageable));
  }

  @GetMapping("/{subCategoryName}")
  public ResponseEntity<SubCategoryResponse> getSubCategoryResponseByName(@PathVariable String subCategoryName) {
    return ResponseEntity.ok().contentType(JSON).body(iSubCategoryService.getSubCategoryResponseByName(subCategoryName));
  }
}
