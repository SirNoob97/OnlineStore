package com.sirnoob.productservice.dto.template;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductRequest {

	private Integer productNumber;
	private String productName;
	private String productDescription;
	private Integer productStock;
	private Double productPrice;
	private Long categoryId;
	
	private Instant createAt;
}
