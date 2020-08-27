package com.sirnoob.userservice.repository;

import com.sirnoob.userservice.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAddresRepository extends JpaRepository<Address, Long> {
}
