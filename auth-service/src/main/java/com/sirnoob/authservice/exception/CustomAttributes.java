package com.sirnoob.authservice.exception;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

@Component
public class CustomAttributes extends DefaultErrorAttributes{

  @Override
  public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
    Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);
    Throwable throwable = getError(request);

    if(throwable instanceof DataIntegrityViolationException){
      DataIntegrityViolationException dve = (DataIntegrityViolationException) throwable;
      errorAttributes.put("message", dve.getMostSpecificCause().getMessage());
      return errorAttributes;
    }

    errorAttributes.put("message", throwable.getMessage());
    return errorAttributes;
  }

}