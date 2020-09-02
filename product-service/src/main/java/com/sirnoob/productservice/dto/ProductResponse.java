package com.sirnoob.productservice.dto;

import java.time.LocalDate;

import com.sirnoob.productservice.entity.MainCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

	
	private Long productId;
	private Integer productNumber;
	private String productName;
	private String productDescription;
	private Integer productStock;
	private Double productPrice;
	private LocalDate createAt;
	private String productStatus;
	private MainCategory mainCategory;

}
