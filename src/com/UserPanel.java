package com;

import com.user.entity.User;
import com.user.service.UserService;
import com.user.service.UserServiceImpl;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

public class UserPanel extends JPanel {

    private final UserService userService = new UserServiceImpl();

    private final DefaultTableModel model;
    private final JTable table;
    private final JLabel countLabel = new JLabel("（共 0 条）");
    private final JTextField searchField = new JTextField(16);

    public UserPanel() {
        setLayout(new BorderLayout(8, 8));

        model = new DefaultTableModel(
                new String[]{"ID", "用户名", "姓名", "地址"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(createTopPanel(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(0, 6));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.add(new JLabel("关键字："));
        searchPanel.add(searchField);

        JButton searchBtn = new JButton("查询");
        JButton refreshBtn = new JButton("刷新");
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton addBtn = new JButton("新增用户");
        JButton editBtn = new JButton("编辑用户");
        JButton deleteBtn = new JButton("删除用户");
        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(countLabel);

        searchBtn.addActionListener(e -> doSearch());
        refreshBtn.addActionListener(e -> refreshTable());
        searchField.addActionListener(e -> doSearch());
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteSelected());

        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(actionPanel, BorderLayout.SOUTH);
        return topPanel;
    }

    private void refreshTable() {
        searchField.setText("");
        loadData();
    }

    private void doSearch() {
        try {
            loadTable(userService.search(searchField.getText()));
        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void loadData() {
        try {
            loadTable(userService.findALL());
        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void loadTable(List<User> users) {
        model.setRowCount(0);

        for (User user : users) {
            model.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getRealName(),
                    user.getAddress()
            });
        }

        countLabel.setText("（共 " + users.size() + " 条）");
    }

    private void showAddDialog() {
        //User user = showUserDialog(null, "新增用户");
        User user = showUserDialog(null, "新增用户");
        if (user == null) {
            return;
        }

        try {
            if (userService.addUser(user)) {
                JOptionPane.showMessageDialog(this, "新增成功");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "新增失败", "提示",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void showEditDialog() {
        Integer id = getSelectedUserId();
        if (id == null) {
            return;
        }

        User oldUser;
        try {
            oldUser = userService.findById(id);
        } catch (RuntimeException ex) {
            showError(ex);
            return;
        }

        if (oldUser == null) {
            JOptionPane.showMessageDialog(this, "用户不存在，可能已被删除",
                    "提示", JOptionPane.WARNING_MESSAGE);
            loadData();
            return;
        }

        User user = showUserDialog(oldUser, "编辑用户");
        if (user == null) {
            return;
        }
        user.setId(id);

        try {
            if (userService.updateUser(user)) {
                JOptionPane.showMessageDialog(this, "修改成功");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "修改失败", "提示",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void deleteSelected() {
        Integer id = getSelectedUserId();
        if (id == null) {
            return;
        }

        int row = table.getSelectedRow();
        String username = String.valueOf(model.getValueAt(row, 1));

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定删除用户【" + username + "】吗？",
                "删除确认",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (userService.deleteById(id)) {
                JOptionPane.showMessageDialog(this, "删除成功");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败", "提示",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private Integer getSelectedUserId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先在表格中选中一条用户记录",
                    "提示", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
    }

    private User showUserDialog(User oldUser, String title) {
        JTextField usernameField = new JTextField(
                safe(oldUser == null ? null : oldUser.getUsername()));
        JPasswordField passwordField = new JPasswordField(
                safe(oldUser == null ? null : oldUser.getPassword()));
        JTextField realNameField = new JTextField(
                safe(oldUser == null ? null : oldUser.getRealName()));
        JTextField addressField = new JTextField(
                safe(oldUser == null ? null : oldUser.getAddress()));

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.add(new JLabel("用户名："));
        form.add(usernameField);
        form.add(new JLabel("密码："));
        form.add(passwordField);
        form.add(new JLabel("姓名："));
        form.add(realNameField);
        form.add(new JLabel("地址："));
        form.add(addressField);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        User user = new User();
        user.setUsername(usernameField.getText());
        user.setPassword(new String(passwordField.getPassword()));
        user.setRealName(realNameField.getText());
        user.setAddress(addressField.getText());
        return user;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void showError(RuntimeException ex) {
        String message = ex.getMessage();
        if (message == null || message.trim().isEmpty()) {
            message = ex.getClass().getSimpleName();
        }

        Throwable cause = ex.getCause();
        if (cause != null
                && cause.getMessage() != null
                && !cause.getMessage().trim().isEmpty()) {
            message += "\n\n原因：" + cause.getMessage();
        }

        JOptionPane.showMessageDialog(this, message, "操作失败",
                JOptionPane.ERROR_MESSAGE);
    }
}