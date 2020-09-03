package com.sirnoob.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductInvoiceResponse {

	private Long productBarCode;
	private String productName;
	private String productDescription;
	private Double productPrice;
	private String mainCategory;
}
