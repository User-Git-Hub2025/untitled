package com.user.service;

import com.user.entity.User;
import com.user.dao.UserDAO;
import com.user.dao.UserDAOImpl;

import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    public List<User> findALL() {
        return userDAO.findALL();
    }

    @Override
    public List<User> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userDAO.findALL();
        }
        return userDAO.search(keyword.trim());
    }

    @Override
    public User findById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("用户ID必须大于0");
        }
        return userDAO.findById(id);
    }

    @Override
    public boolean addUser(User user) {
        normalize(user);
        validate(user);

        if (userDAO.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("用户名已存在：" + user.getUsername());
        }

        return userDAO.insert(user) > 0;
    }

    @Override
    public boolean updateUser(User user) {
        if (user == null || user.getId() <= 0) {
            throw new IllegalArgumentException("要修改的用户不存在");
        }

        normalize(user);
        validate(user);

        User existing = userDAO.findByUsername(user.getUsername());
        if (existing != null && existing.getId() != user.getId()) {
            throw new IllegalArgumentException("用户名已被其他用户使用：" + user.getUsername());
        }

        return userDAO.update(user);
    }

    @Override
    public boolean deleteById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("要删除的用户ID不正确");
        }
        return userDAO.deleteById(id);
    }

    @Override
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        User user = userDAO.findByUsername(username.trim());
        if (user == null) {
            return null;
        }

        if (!user.getPassword().equals(password)) {
            return null;
        }

        return user;
    }

    private void normalize(User user) {
        if (user == null) {
            return;
        }
        user.setUsername(trim(user.getUsername()));
        user.setPassword(trim(user.getPassword()));
        user.setRealName(trim(user.getRealName()));
        user.setAddress(trim(user.getAddress()));
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private void validate(User user) {
        if (user == null) {
            throw new IllegalArgumentException("用户信息不能为空");
        }

        if (user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
            throw new IllegalArgumentException("用户名长度必须在 3 到 50 个字符之间");
        }

        if (user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("密码长度不能少于 6 位");
        }
        if (user.getPassword().length() > 50) {
            throw new IllegalArgumentException("密码不能超过 50 位");
        }

        if (user.getRealName().isEmpty()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        if (user.getRealName().length() > 50) {
            throw new IllegalArgumentException("姓名不能超过 50 个字符");
        }

        if (user.getAddress().length() > 255) {
            throw new IllegalArgumentException("地址不能超过 255 个字符");
        }
    }
}
