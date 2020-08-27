package com.sirnoob.userservice.exceptions;

public class AddressNotFoundException extends RuntimeException{
  public AddressNotFoundException(String message){
    super(message);
  }
  public AddressNotFoundException(String message, Exception exception){
    super(message, exception);
  }
}
