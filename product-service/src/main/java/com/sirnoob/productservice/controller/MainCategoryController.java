package com.sirnoob.productservice.controller;

import javax.validation.Valid;

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

import com.sirnoob.productservice.dto.MainCategory;
import com.sirnoob.productservice.service.IMainCategoryService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/maincategories")
public class MainCategoryController {

	private final IMainCategoryService iMainCategoryService;
	
	@PostMapping("/createmaincategory/")
	public ResponseEntity<Mono<MainCategory>> createMainCategory(@RequestParam(name = "mainCategoryName", required = true) String mainCategoryName){
		return ResponseEntity.status(HttpStatus.CREATED).body(iMainCategoryService.createMainCategory(mainCategoryName));
	}
	
	@PutMapping("/updatemaincategory")
	public ResponseEntity<Mono<MainCategory>> updateMainCategory(@Valid @RequestBody MainCategory mainCategory){
		return ResponseEntity.ok().body(iMainCategoryService.updateMainCategory(mainCategory));
	}
	
	@DeleteMapping("/deletemaincategory/{mainCategoryId}")
	public ResponseEntity<Mono<MainCategory>> deleteMainCategory(@PathVariable Long mainCategoryId){
		return ResponseEntity.ok().body(iMainCategoryService.deleteMainCategory(mainCategoryId));
	}
	
	@GetMapping("/findmaincategorybyname/")
	public ResponseEntity<Mono<MainCategory>> findMainCategoryByName(@RequestParam(name = "mainCategoryName", required = true) String mainCategoryName){
		return ResponseEntity.ok().body(iMainCategoryService.findMainCategoryByName(mainCategoryName));
	}
	
	@GetMapping("/findallmaincategories")
	public ResponseEntity<Flux<MainCategory>> findAllMainCategories(){
		return ResponseEntity.ok().body(iMainCategoryService.findAllMainCategories());
	}
}
