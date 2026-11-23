package com.user.service;

import com.user.entity.User;

import java.util.List;

public interface UserService {

    List<User> findALL();

    List<User> search(String keyword);

    User findById(int id);

    boolean addUser(User user);

    boolean updateUser(User user);

    boolean deleteById(int id);

    User login(String username, String password);
}