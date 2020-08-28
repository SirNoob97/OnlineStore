package com.sirnoob.userservice;

import com.sirnoob.userservice.entity.Address;
import com.sirnoob.userservice.entity.User;
import com.sirnoob.userservice.repository.IUserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
public class UserRepositoryTest {

  private final IUserRepository iUserRepository;

  @Autowired
  public UserRepositoryTest(IUserRepository iUserRepository) {
    this.iUserRepository = iUserRepository;
  }

  @Test
  public void saveUser_findByDni_andReturnUser() {
    User user = User.builder().userDni("D4561238I")
            .userName("user name")
            .firstName("FIRST NAME")
            .lastName("last name")
            .email("TESTSAVEEMAIL@tse.com")
            .status("CREATED")
            .address(Address.builder().address("New Address").zipCode(45612).build())
            .build();

    iUserRepository.save(user);

    User userDB = iUserRepository.findByUserDni("D4561238I").orElse(null);

    Assertions.assertThat(userDB).isNotNull();
    Assertions.assertThat(userDB.getFirstName()).isEqualToIgnoringCase("FIRST NAME");
    Assertions.assertThat(userDB.getLastName()).isNotEqualTo("LAST NAME");
  }

  @Test
  public void findByEmail_andReturnUser(){
    User user = iUserRepository.findByEmail("TEST@email.com").orElse(null);
    Assertions.assertThat(user).isNotNull();
    Assertions.assertThat(user.getUserName()).isEqualTo("TEST FETCH BY EMAIL");
    Assertions.assertThat(user.getLastName()).isEqualTo("test fetch by email");
  }

  @Test
  public void findByAddress_andReturnUserList(){
    List<User> userList = iUserRepository.findByAddressAddressId(3L);
    Assertions.assertThat(userList).isNotEmpty();
    Assertions.assertThat(userList.size()).isEqualTo(2);

  }
}
