package com.sirnoob.userservice.validators;

import java.util.List;
import java.util.Map;

public class ErrorMessage {
  private String code;
  private List<Map<String, String>> messages;

  private ErrorMessage(){}

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public List<Map<String, String>> getMessages() {
    return messages;
  }

  public void setMessages(List<Map<String, String>> messages) {
    this.messages = messages;
  }

  public static Builder builder(){
    return new Builder();
  }

  public static class Builder{

    private String code;
    private List<Map<String, String>> messages;


    private Builder(){}

    public Builder code(String code){
      this.code = code;
      return this;
    }

    public Builder messages(List<Map<String, String>> messages){
      this.messages = messages;
      return this;
    }

    public ErrorMessage build(){
      ErrorMessage errorMessage = new ErrorMessage();

      errorMessage.code = this.code;
      errorMessage.messages = this.messages;

      return errorMessage;
    }
  }
}
