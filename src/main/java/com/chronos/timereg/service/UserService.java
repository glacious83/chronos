package com.chronos.timereg.service;

import com.chronos.timereg.dto.UserRequest;
import com.chronos.timereg.model.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User createUser(UserRequest userRequest);
    User updateUser(Long id, UserRequest userRequest);
    void deleteUser(Long id);

    User getUserByEmployeeId(String username);
}
