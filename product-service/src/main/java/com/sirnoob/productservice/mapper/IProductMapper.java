package com.sirnoob.productservice.mapper;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductResponse;

import io.r2dbc.spi.Row;

public interface IProductMapper {

	public ProductResponse mapToProductResponse(Row row);
	
	public ProductInvoiceResponse mapToInvoiceResponse(Row row);
}
