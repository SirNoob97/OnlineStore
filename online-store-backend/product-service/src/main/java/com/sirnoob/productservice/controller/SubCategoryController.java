package com.sirnoob.productservice.controller;

import java.util.Set;

import javax.validation.Valid;

import com.sirnoob.productservice.dto.SubCategoryRequest;
import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.service.MainCategoryService;
import com.sirnoob.productservice.service.SubCategoryService;

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

  private final MainCategoryService mainCategoryService;
  private final SubCategoryService subCategoryService;

  @PostMapping
  public ResponseEntity<SubCategoryResponse> create(@Valid @RequestBody SubCategoryRequest subCategoryRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).contentType(JSON)
      .body(subCategoryService.create(subCategoryRequest.getSubCategoryName(),
        mainCategoryService.getByName(subCategoryRequest.getMainCategoryName())));
  }

  @PutMapping("/{subCategoryId}")
  public ResponseEntity<Void> updateName(@PathVariable Long subCategoryId,
                                                    @RequestParam(required = true) String subCategoryName) {
    subCategoryService.updateName(subCategoryId, subCategoryName);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{subCategoryId}")
  public ResponseEntity<Void> deleteById(@PathVariable Long subCategoryId) {
    subCategoryService.deleteById(subCategoryId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<Set<String>> getAll(Pageable pageable) {
    return ResponseEntity.ok().contentType(JSON).body(subCategoryService.getAll(pageable));
  }

  @GetMapping("/{subCategoryName}")
  public ResponseEntity<SubCategoryResponse> getByName(@PathVariable String subCategoryName) {
    return ResponseEntity.ok().contentType(JSON).body(subCategoryService.getSubCategoryResponseByName(subCategoryName));
  }
}
