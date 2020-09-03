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

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.service.IProductService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/products")
public class ProductRestController {

	private final IProductService iProductService;

	@PostMapping("/createproduct")
	public ResponseEntity<Mono<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest productRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(iProductService.createProduct(productRequest));
	}

	@DeleteMapping("/deleteproduct/{productBarCode}")
	public ResponseEntity<Mono<ProductResponse>> deleteProductByBarCodeBar(@PathVariable Long productBarCode) {
		return ResponseEntity.ok().body(iProductService.deleteProduct(productBarCode));
	}

	@PutMapping("/updateproduct")
	public ResponseEntity<Mono<ProductResponse>> updateProduct(@Valid @RequestBody ProductRequest productRequest) {
		return ResponseEntity.ok().body(iProductService.updateProduct(productRequest));
	}

	@PutMapping("/updateproductstock/{productBarCode}")
	public ResponseEntity<Mono<ProductResponse>> updateProductStock(@PathVariable Long productBarCode,
			@RequestParam(name = "quantity", required = true) Integer quantity) {
		return ResponseEntity.ok().body(iProductService.updateStock(productBarCode, quantity));
	}
	
	@PutMapping("/suspendproduct/{productBarCode}")
	public ResponseEntity<Mono<ProductResponse>> suspendProductStock(@PathVariable Long productBarCode) {
		return ResponseEntity.ok().body(iProductService.suspendProduct(productBarCode));
	}

	@GetMapping("/getallproducts")
	public ResponseEntity<Flux<ProductResponse>> getAllProducts() {
		return ResponseEntity.ok().body(iProductService.getAllProducts());
	}

	@GetMapping("/findproductbyid/{id}")
	public ResponseEntity<Mono<ProductInvoiceResponse>> findProductById(@PathVariable Long id) {
		return ResponseEntity.ok().body(iProductService.getProductInvoiceResponseById(id));
	}

	@GetMapping("/findproductbyname/")
	public ResponseEntity<Flux<ProductResponse>> findProductByName(
			@RequestParam(name = "product_name", required = true) String productName) {
		return ResponseEntity.ok().body(iProductService.getProductByName(productName));
	}

	@GetMapping("/findproductbybarcode/{productBarCode}")
	public ResponseEntity<Mono<ProductResponse>> findProductByproductBarCode(@PathVariable Long productBarCode) {
		return ResponseEntity.ok().body(iProductService.getProductByproductBarCode(productBarCode));
	}

	@GetMapping("/findproductbycategory/{id}")
	public ResponseEntity<Flux<ProductResponse>> findProductByCategory(@PathVariable Long id) {
		return ResponseEntity.ok().body(iProductService.getProductByMainCategory(id));
	}
}
