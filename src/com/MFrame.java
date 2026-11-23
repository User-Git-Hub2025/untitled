package com;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MFrame extends JFrame {

    public MFrame() {
        setTitle("用户管理系统");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(new UserPanel());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MFrame().setVisible(true));
    }
}