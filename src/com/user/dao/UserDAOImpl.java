package com.user.dao;

import com.user.entity.User;
import com.user.util.JdbcHelper;
import com.user.util.JdbcHelper.RowMapper;

import java.util.List;

public class UserDAOImpl implements UserDAO {

    private static final String COLUMNS =
            "id, username, password, real_name, address";

    private final RowMapper<User> userMapper = rs -> {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRealName(rs.getString("real_name"));
        user.setAddress(rs.getString("address"));
        return user;
    };

    @Override
    public int insert(User user) {
        String sql = "insert into tb_user " +
                "(username, password, real_name, address) " +
                "values (?, ?, ?, ?)";

        return JdbcHelper.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getAddress());
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "delete from tb_user where id = ?";
        return JdbcHelper.update(sql, id) > 0;
    }

    @Override
    public boolean update(User user) {
        String sql = "update tb_user set " +
                "username = ?, password = ?, real_name = ?, address = ? " +
                "where id = ?";

        return JdbcHelper.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getAddress(),
                user.getId()) > 0;
    }

    @Override
    public List<User> findALL() {
        String sql = "select " + COLUMNS + " from tb_user order by id";
        return JdbcHelper.query(sql, userMapper);
    }

    @Override
    public User findById(int id) {
        String sql = "select " + COLUMNS + " from tb_user where id = ?";
        return JdbcHelper.queryOne(sql, userMapper, id);
    }

    @Override
    public User findByUsername(String username) {
        String sql = "select " + COLUMNS + " from tb_user where username = ?";
        return JdbcHelper.queryOne(sql, userMapper, username);
    }

    @Override
    public List<User> search(String keyword) {
        String sql = "select " + COLUMNS + " from tb_user " +
                "where username like ? " +
                "or real_name like ? " +
                "or address like ? " +
                "order by id";

        String likeKeyword = "%" + keyword + "%";

        return JdbcHelper.query(sql, userMapper,
                likeKeyword, likeKeyword, likeKeyword);
    }
}
