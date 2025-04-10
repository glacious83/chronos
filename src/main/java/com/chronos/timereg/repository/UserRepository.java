package com.chronos.timereg.repository;

import com.chronos.timereg.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmployeeId(String employeeId);

    Optional<User> findBySap(String sap);

    Optional<User> findByFirstNameAndLastName(String firstName, String surname);

    Optional<User> findByLastName(String managerLastName);
}
