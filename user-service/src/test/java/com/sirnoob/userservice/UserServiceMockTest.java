package com.sirnoob.userservice;

import com.sirnoob.userservice.entity.Address;
import com.sirnoob.userservice.entity.User;
import com.sirnoob.userservice.repository.IAddresRepository;
import com.sirnoob.userservice.repository.IUserRepository;
import com.sirnoob.userservice.service.IUserService;
import com.sirnoob.userservice.service.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class UserServiceMockTest {

  @Mock
  private IUserRepository iUserRepository;
  private IUserService iUserService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    iUserService = new UserServiceImpl(iUserRepository);

    User user = User.builder().userId(4L).userDni("D4561238I")
            .userName("user name")
            .firstName("FIRST NAME")
            .lastName("last name")
            .email("TESTSAVEEMAIL@tse.com")
            .status("CREATED")
            .address(Address.builder().addressId(4L).address("New Address").zipCode(45612).build())
            .build();

    Mockito.when(iUserRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
    Mockito.when(iUserRepository.findByUserDni(user.getUserDni())).thenReturn(Optional.of(user));
    Mockito.when(iUserRepository.findByEmail("UPdateTesT@c.c")).thenReturn(Optional.of(user));
    Mockito.when(iUserRepository.save(user)).thenReturn(user);
  }

  @Test
  public void whenValidateGetUser_thenReturnUser(){
    User user = iUserService.getUser(4L);
    Assertions.assertThat(user.getAddress().getZipCode()).isEqualTo(45612);
  }

  @Test
  public void validateWhenUpdateUser_thenReturnUser(){
    User user = iUserService.updateUser(User.builder().userId(4L)
            .userDni("D4561238I")
            .userName("user name")
            .firstName("FIRST NAME")
            .lastName("Update Test")
            .email("UPdateTesT@c.c")
            .status("CREATED")
            .address(Address.builder().addressId(1L).build())
            .build());
    Assertions.assertThat(user.getLastName().equalsIgnoreCase("Update Test"));
    Assertions.assertThat(user.getEmail().equalsIgnoreCase("UPdateTesT@c.c"));
    Assertions.assertThat(user.getAddress().getAddressId() == 1L);
  }

  @Test
  public void validateWhenDeleteUser_thenReturnUser(){
    User user = iUserService.deleteUser(4L);
    Assertions.assertThat(user.getStatus().equals("DELETED"));
  }

}
