package com.sirnoob.productservice.validator;

import java.util.List;
import java.util.Set;

import com.sirnoob.productservice.exception.ResourceNotFoundException;

public class CollectionValidator {
		
	public static <T> Set<T> throwExceptionIfSetIsEmpty(Set<T> set, String message){
		if(!set.isEmpty()) return set;
		throw new ResourceNotFoundException(message);
	}

	
	public static <T> List<T> throwExceptionIfListIsEmpty(List<T> list, String message){
		if(!list.isEmpty()) return list;
		throw new ResourceNotFoundException(message);
	}
}
