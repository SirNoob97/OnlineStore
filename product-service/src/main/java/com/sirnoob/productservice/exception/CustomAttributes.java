package com.sirnoob.productservice.exception;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CustomAttributes extends DefaultErrorAttributes{

	@Override
	public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
		Map<String, Object> errorsAttributes = super.getErrorAttributes(request, options);
		Throwable throwable = getError(request);
		
		if (throwable instanceof WebExchangeBindException) {
			ResponseStatusException responseStatusException = (ResponseStatusException) throwable;
			errorsAttributes.put("message", findValidationDefaultMessage(responseStatusException.getMessage()));
			
			return errorsAttributes;
		}
		
		if (throwable instanceof ResponseStatusException) {
			ResponseStatusException responseStatusException = (ResponseStatusException) throwable;
			errorsAttributes.put("message",  responseStatusException.getMessage());
			
			return errorsAttributes;
		}
		
		return errorsAttributes;
	}
	
//	He usado este metodo para mostrar el mensaje de error en las validaciones cuando hay un mal formato en los datos al crear y actualizar
//	ya que no puedo obtener un binding result de las validaciones dejare esto asi hasta que averigue como manejarlas sin esa interface
	
	private String findValidationDefaultMessage(String message) {
		int begin = message.lastIndexOf("default message [");
		int end = message.lastIndexOf("]]");
		
		return message.substring((begin + 17), (end));
	}
}
