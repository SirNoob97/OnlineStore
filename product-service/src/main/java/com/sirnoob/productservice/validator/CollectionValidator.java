package com.sirnoob.productservice.validator;

import java.util.Set;

import com.sirnoob.productservice.exception.ResourceNotFoundException;

import org.springframework.data.domain.Page;

public class CollectionValidator {

	public static <T> Set<T> throwExceptionIfSetIsEmpty(Set<T> set, String message){
		if(!set.isEmpty()) return set;
		throw new ResourceNotFoundException(message);
	}

	public static <T> Page<T> throwExceptionIfPageIsEmpty(Page<T> page, String message){
		if(!page.isEmpty()) return page;
		throw new ResourceNotFoundException(message);
	}
}
