package com.user.dao;

import com.user.entity.User;

import java.util.List;

public interface UserDAO {

    int insert(User user);

    boolean deleteById(int id);

    boolean update(User user);

    List<User> findALL();

    User findById(int id);

    User findByUsername(String username);

    List<User> search(String keyword);
}
