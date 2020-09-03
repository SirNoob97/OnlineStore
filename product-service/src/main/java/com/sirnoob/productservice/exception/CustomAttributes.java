package com.sirnoob.productservice.exception;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CustomAttributes extends DefaultErrorAttributes{

	@Override
	public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
		Map<String, Object> errorsAttributes = super.getErrorAttributes(request, options);
		Throwable throwable = getError(request);
		
		if (throwable instanceof ResponseStatusException) {
			ResponseStatusException responseStatusException = (ResponseStatusException) throwable;
			errorsAttributes.put("message", responseStatusException.getMessage());
			
			return errorsAttributes;
		}
		
		return errorsAttributes;
	}
	

}
