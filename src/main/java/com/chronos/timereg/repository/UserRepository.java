package com.chronos.timereg.repository;

import com.chronos.timereg.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmployeeId(String employeeId);

    Optional<User> findByFirstNameAndLastName(String firstName, String surname);

    Optional<User> findByLastName(String managerLastName);

    @Query("SELECT u FROM User u WHERE " +
            "(u.email IS NULL OR u.email = '' OR " +
            "u.employeeId IS NULL OR u.employeeId = '' OR " +
            "u.firstName IS NULL OR u.firstName = '' OR " +
            "u.ip IS NULL OR u.ip = '' OR " +
            "u.lastName IS NULL OR u.lastName = '' OR " +
            "u.phone IS NULL OR u.phone = '' OR " +
            "u.sapId IS NULL OR u.sapId = '' OR " +
            "u.title IS NULL OR u.title = '' OR " +
            "u.vm IS NULL OR u.vm = '' OR " +
            "u.company IS NULL OR " +
            "u.department IS NULL OR " +
            "u.location IS NULL OR " +
            "u.approved IS false OR " +
            "u.responsibleManager IS NULL) AND " +
            "u.lastName != 'Admin'")
    List<User> findUsersWithMissingFields();

    CharSequence findByEmail(String mail);

    List<User> findByTitle(String role);
}
