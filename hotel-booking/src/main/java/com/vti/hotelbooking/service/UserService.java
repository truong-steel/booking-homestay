package com.vti.hotelbooking.service;

import com.vti.hotelbooking.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);

    User registerAdmin(User user);

    User registerManager(User user);

    List<User> getUsers();
    void deleteUser(String email);
    User getUser(String email);
    Optional<User> getUserById(Long userId);
}
