package com.sirnoob.productservice.validator;

import java.util.List;
import java.util.Set;

import com.sirnoob.productservice.exception.ResourceNotFoundException;

public class CollectionValidator {
	
	private static final String NOPRODUCTSFOUND = "No Products Found";
	
	public static <T> Set<T> throwExceptionIfSetIsEmpty(Set<T> set){
		if(!set.isEmpty()) return set;
		throw new ResourceNotFoundException(NOPRODUCTSFOUND);
	}
	
	public static <T> List<T> throwExceptionIfListIsEmpty(List<T> list){
		if(!list.isEmpty()) return list;
		throw new ResourceNotFoundException(NOPRODUCTSFOUND);
	}
}
