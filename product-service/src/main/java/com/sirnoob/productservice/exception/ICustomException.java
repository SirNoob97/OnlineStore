package com.sirnoob.productservice.exception;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICustomException {
	public <T> Mono<? extends T> monoElementCustomNotFoundException(String message);

	public <T> Flux<? extends T> fluxElementsCustomNotFoundException(String message);
}
