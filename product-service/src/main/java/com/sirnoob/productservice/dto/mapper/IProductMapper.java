package com.sirnoob.productservice.dto.mapper;

import com.sirnoob.productservice.dto.template.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.template.ProductResponse;

import io.r2dbc.spi.Row;

public interface IProductMapper {

	public ProductResponse mapToProductResponse(Row row);
	
	public ProductInvoiceResponse mapToInvoiceResponse(Row row);
}
