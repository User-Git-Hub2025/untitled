package com;

import com.user.entity.User;
import com.user.service.UserService;
import com.user.service.UserServiceImpl;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class login {

    private static final UserService userService = new UserServiceImpl();

    public static void main(String[] args) {
        JFrame frame = new JFrame("登录窗口");
        frame.setSize(320, 220);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JLabel labUser = new JLabel("用户名：");
        labUser.setBounds(20, 25, 70, 25);

        JTextField txtUser = new JTextField();
        txtUser.setBounds(95, 25, 180, 25);

        JLabel labPwd = new JLabel("密码：");
        labPwd.setBounds(20, 65, 70, 25);

        JPasswordField txtPwd = new JPasswordField();
        txtPwd.setBounds(95, 65, 180, 25);

        JButton btnLogin = new JButton("登录");
        btnLogin.setBounds(170, 115, 100, 30);

        JButton btnReset = new JButton("重置");
        btnReset.setBounds(40, 115, 100, 30);

        btnLogin.addActionListener(e -> {
            String username = txtUser.getText().trim();
            String password = new String(txtPwd.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "账号或密码不能为空");
                return;
            }

            try {
                User user = userService.login(username, password);
                if (user == null) {
                    JOptionPane.showMessageDialog(frame, "用户名或密码不正确");
                    return;
                }

                JOptionPane.showMessageDialog(frame, "登录成功");
                frame.dispose();
                new MFrame().setVisible(true);

            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(),
                        "登录失败", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnReset.addActionListener(e -> {
            txtUser.setText("");
            txtPwd.setText("");
        });

        frame.add(labUser);
        frame.add(txtUser);
        frame.add(labPwd);
        frame.add(txtPwd);
        frame.add(btnLogin);
        frame.add(btnReset);

        frame.setVisible(true);
    }
}