package com.sirnoob.productservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CustomException implements ICustomException {

	@Override
	public <T> Mono<? extends T> monoElementCustomNotFoundException(String message) {
		return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, message));
	}

	@Override
	public <T> Flux<? extends T> fluxElementsCustomNotFoundException(String message) {
		return Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, message));
	}

}
