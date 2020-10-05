package com.sirnoob.userservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sirnoob.userservice.entity.User;
import com.sirnoob.userservice.service.IUserService;
import com.sirnoob.userservice.validators.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
public class UserRestController {

  private IUserService iUserService;

  @Autowired
  public UserRestController(IUserService iUserService){
    this.iUserService = iUserService;
  }

  @PostMapping("/signup")
  public ResponseEntity<User> createUser(@Valid @RequestBody User user, BindingResult bindingResult){
    if(bindingResult.hasErrors()) new ResponseStatusException(HttpStatus.BAD_REQUEST, this.formatMessage(bindingResult));

    return ResponseEntity.status(HttpStatus.CREATED).body(iUserService.createUser(user));
  }


  private String formatMessage(BindingResult result) {

    List<Map<String, String>> errors = result.getFieldErrors().stream().map(err -> {
      Map<String, String> error = new HashMap<>();
      error.put(err.getField(), err.getDefaultMessage());
      return error;
    }).collect(Collectors.toList());

    ErrorMessage errorMessage = ErrorMessage.builder().code("01").messages(errors).build();

    ObjectMapper mapper = new ObjectMapper();
    String jsonMessage = "";

    try {
      jsonMessage = mapper.writeValueAsString(errorMessage);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return jsonMessage;
  }
}
