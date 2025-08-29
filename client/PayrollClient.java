package client;

import common.Employee;
import common.EmployeeService;
import common.PayrollService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.border.EmptyBorder;

/**
 * PayrollClient is the main client application for the Payroll System.
 * Provides GUI for login, registration, dashboard, payroll, and reports.
 */
public class PayrollClient {
    private EmployeeService employeeService;
    private PayrollService payrollService;

    public PayrollClient() {
        System.out.println("[CLIENT] Employee.class loaded from: " + common.Employee.class.getProtectionDomain().getCodeSource().getLocation());
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Failed to set system look and feel");
        }
        try {
            // 纯RMI连接，完全禁用SSL
            Registry registry = LocateRegistry.getRegistry("172.20.10.2", 1099);
            System.out.println("Connected to RMI registry (Pure RMI, No SSL)");
            employeeService = (EmployeeService) registry.lookup("EmployeeService");
            System.out.println("EmployeeService lookup success");
            payrollService = (PayrollService) registry.lookup("PayrollService");
            System.out.println("PayrollService lookup success");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to connect to server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        showLoginFrame();
    }

    /**
     * Shows the login window and handles user login.
     */
    private void showLoginFrame() {
        JFrame frame = new JFrame("Payroll System - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Payroll System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(16, 0, 8, 0));
        frame.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(new EmptyBorder(10, 30, 10, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(16);
        usernameField.setPreferredSize(new Dimension(220, 28));
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(16);
        passwordField.setPreferredSize(new Dimension(220, 28));
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        loginBtn.setPreferredSize(new Dimension(120, 32));
        registerBtn.setPreferredSize(new Dimension(120, 32));
        Font btnFont = new Font("Segoe UI", Font.PLAIN, 15);
        loginBtn.setFont(btnFont);
        registerBtn.setFont(btnFont);

        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        centerPanel.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        centerPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        centerPanel.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);
        centerPanel.add(btnPanel, gbc);

        frame.add(centerPanel, BorderLayout.CENTER);

        JLabel copyright = new JLabel("© 2025 Payroll System", SwingConstants.CENTER);
        copyright.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyright.setBorder(new EmptyBorder(8, 0, 8, 0));
        frame.add(copyright, BorderLayout.SOUTH);

        loginBtn.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            // 输入校验
            if (username == null || username.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (password == null || password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Password cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            System.out.println("Login button clicked: " + username + " / " + password);
            try {
                Employee emp = employeeService.login(username, password);
                System.out.println("Login result: " + (emp != null ? "success" : "fail"));
                if (emp != null) {
                    JOptionPane.showMessageDialog(frame, "Login successful! Welcome, " + emp.getFirstName());
                    frame.dispose();
                    showDashboard(emp);
                } else {
                    JOptionPane.showMessageDialog(frame, "Login failed!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        registerBtn.addActionListener((ActionEvent e) -> showRegisterFrame());

        frame.setVisible(true);
    }

    /**
     * Shows the registration window for new employees.
     */
    private void showRegisterFrame() {
        JFrame frame = new JFrame("Payroll System - Register");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 350);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Register New Employee", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(16, 0, 8, 0));
        frame.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(new EmptyBorder(10, 30, 10, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(16);
        usernameField.setPreferredSize(new Dimension(220, 28));
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(16);
        passwordField.setPreferredSize(new Dimension(220, 28));
        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField(16);
        firstNameField.setPreferredSize(new Dimension(220, 28));
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField(16);
        lastNameField.setPreferredSize(new Dimension(220, 28));
        JLabel icPassportLabel = new JLabel("IC/Passport:");
        JTextField icPassportField = new JTextField(16);
        icPassportField.setPreferredSize(new Dimension(220, 28));
        JButton submitBtn = new JButton("Submit");
        submitBtn.setPreferredSize(new Dimension(120, 32));
        submitBtn.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        centerPanel.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        centerPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        centerPanel.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        centerPanel.add(firstNameLabel, gbc);
        gbc.gridx = 1;
        centerPanel.add(firstNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        centerPanel.add(lastNameLabel, gbc);
        gbc.gridx = 1;
        centerPanel.add(lastNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 4;
        centerPanel.add(icPassportLabel, gbc);
        gbc.gridx = 1;
        centerPanel.add(icPassportField, gbc);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.add(submitBtn);
        centerPanel.add(btnPanel, gbc);

        frame.add(centerPanel, BorderLayout.CENTER);

        JLabel copyright = new JLabel("© 2025 Payroll System", SwingConstants.CENTER);
        copyright.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyright.setBorder(new EmptyBorder(8, 0, 8, 0));
        frame.add(copyright, BorderLayout.SOUTH);

        submitBtn.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String icPassport = icPassportField.getText();
            // 输入校验
            if (username == null || username.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (password == null || password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Password cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (firstName == null || firstName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "First name cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Last name cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (icPassport == null || icPassport.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "IC/Passport cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Employee emp = new Employee();
            emp.setUsername(username);
            emp.setPassword(password);
            emp.setFirstName(firstName);
            emp.setLastName(lastName);
            emp.setIcPassport(icPassport);
            System.out.println("Register button clicked: " + emp.getUsername());
            try {
                boolean ok = employeeService.register(emp);
                System.out.println("Register result: " + ok);
                if (ok) {
                    JOptionPane.showMessageDialog(frame, "Registration successful!");
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Username already exists! Please enter a different username.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        frame.setVisible(true);
    }

    /**
     * Shows the main dashboard after successful login.
     * @param emp The logged-in Employee
     */
    private void showDashboard(Employee emp) {
        JFrame frame = new JFrame("Payroll System - Dashboard");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);

        // 顶部导航栏
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(33, 150, 243));
        topPanel.setPreferredSize(new Dimension(900, 56));
        JLabel logo = new JLabel("  4b0 Payroll System", SwingConstants.LEFT);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);
        topPanel.add(logo, BorderLayout.WEST);

        // 用户圆形按钮
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 8));
        userPanel.setOpaque(false);
        String userInitial = emp.getUsername().substring(0, 1).toUpperCase();
        JButton userCircle = new JButton(userInitial);
        userCircle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userCircle.setForeground(Color.WHITE);
        userCircle.setBackground(new Color(25, 118, 210));
        userCircle.setFocusPainted(false);
        userCircle.setBorder(BorderFactory.createEmptyBorder());
        userCircle.setPreferredSize(new Dimension(40, 40));
        userCircle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        userCircle.setContentAreaFilled(false);
        userCircle.setOpaque(true);
        userCircle.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        userPanel.add(userCircle);
        JLabel userLabel = new JLabel(emp.getUsername());
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userPanel.add(userLabel);
        topPanel.add(userPanel, BorderLayout.EAST);
        frame.add(topPanel, BorderLayout.NORTH);

        // 侧边栏Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(245, 247, 250));
        sidebar.setPreferredSize(new Dimension(180, 600));
        String[] navs = {"Dashboard", "Payroll", "Reports", "Settings"};
        JButton[] navBtns = new JButton[navs.length];
        for (int i = 0; i < navs.length; i++) {
            navBtns[i] = new JButton(navs[i]);
            navBtns[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            navBtns[i].setMaximumSize(new Dimension(160, 40));
            navBtns[i].setFont(new Font("Segoe UI", Font.PLAIN, 16));
            navBtns[i].setFocusPainted(false);
            navBtns[i].setBackground(i == 0 ? new Color(225, 235, 245) : Color.WHITE);
            navBtns[i].setForeground(new Color(33, 150, 243));
            navBtns[i].setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            int idx = i;
            navBtns[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    navBtns[idx].setBackground(new Color(200, 220, 240));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    navBtns[idx].setBackground(idx == 0 ? new Color(225, 235, 245) : Color.WHITE);
                }
            });
            sidebar.add(Box.createVerticalStrut(10));
            sidebar.add(navBtns[i]);
        }
        sidebar.add(Box.createVerticalGlue());
        frame.add(sidebar, BorderLayout.WEST);

        // 主内容区
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel welcome = new JLabel("Welcome, " + emp.getFirstName() + "!", SwingConstants.LEFT);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(welcome);
        mainPanel.add(Box.createVerticalStrut(30));

        // 卡片区
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 10));
        cardPanel.setOpaque(false);
        cardPanel.add(createCard("Payroll", "View your payslips and salary details"));
        cardPanel.add(createCard("Reports", "Generate and view payroll reports"));
        cardPanel.add(createCard("Settings", "Update your profile and preferences"));
        mainPanel.add(cardPanel);
        mainPanel.add(Box.createVerticalGlue());

        frame.add(mainPanel, BorderLayout.CENTER);

        JLabel copyright = new JLabel("© 2024 Payroll System", SwingConstants.CENTER);
        copyright.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyright.setBorder(new EmptyBorder(8, 0, 8, 0));
        frame.add(copyright, BorderLayout.SOUTH);

        // 用户圆形按钮弹出设置菜单
        JPopupMenu userMenu = new JPopupMenu();
        JMenuItem settingItem = new JMenuItem("Settings");
        JMenuItem logoutItem = new JMenuItem("Logout");
        userMenu.add(settingItem);
        userMenu.add(logoutItem);
        userCircle.addActionListener(e -> userMenu.show(userCircle, 0, userCircle.getHeight()));
        settingItem.addActionListener(e -> showSettingDialog(frame, emp, welcome, userCircle));
        logoutItem.addActionListener(e -> {
            frame.dispose();
            showLoginFrame();
        });

        // Sidebar Settings按钮也可进入设置
        navBtns[3].addActionListener(e -> showSettingDialog(frame, emp, welcome, userCircle));

        // Payroll按钮和卡片名称根据角色动态设置
        String payrollLabel = (emp.getRole() != null && emp.getRole().equalsIgnoreCase("admin")) ? "Generate Payroll" : "Payroll";
        navBtns[1] = new JButton(payrollLabel);
        navBtns[1].setAlignmentX(Component.CENTER_ALIGNMENT);
        navBtns[1].setMaximumSize(new Dimension(160, 40));
        navBtns[1].setFont(new Font("Segoe UI", Font.PLAIN, 16));
        navBtns[1].setFocusPainted(false);
        navBtns[1].setBackground(new Color(225, 235, 245));
        navBtns[1].setForeground(new Color(33, 150, 243));
        navBtns[1].setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        JPanel payrollCard = createCard(payrollLabel, payrollLabel.equals("Generate Payroll") ? "Admin: generate payroll for employees" : "View your payslips and salary details");
        cardPanel.add(payrollCard);

        // Payroll卡片和侧边栏点击事件（区分admin/employee）
        cardPanel.getComponent(0).addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (emp.getRole() != null && emp.getRole().equalsIgnoreCase("admin")) {
                    showAdminPayrollWindow(emp);
                } else {
                    showPayrollWindow(emp);
                }
            }
        });
        navBtns[1].addActionListener(e -> {
            if (emp.getRole() != null && emp.getRole().equalsIgnoreCase("admin")) {
                showAdminPayrollWindow(emp);
            } else {
                showPayrollWindow(emp);
            }
        });

        // Reports卡片和侧边栏点击事件
        cardPanel.getComponent(1).addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showReportWindow(emp);
            }
        });
        navBtns[2].addActionListener(e -> showReportWindow(emp));

        // Employee Management卡片和侧边栏点击事件（仅admin可见）
        if (emp.getRole() != null && emp.getRole().equalsIgnoreCase("admin")) {
            JPanel empCard = createCard("Employee Management", "Add, view, and delete employees");
            cardPanel.add(empCard);
            empCard.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    showEmployeeManagementWindow();
                }
            });
            JButton empBtn = new JButton("Employee Management");
            empBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            empBtn.setMaximumSize(new Dimension(160, 40));
            empBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            empBtn.setFocusPainted(false);
            empBtn.setBackground(Color.WHITE);
            empBtn.setForeground(new Color(33, 150, 243));
            empBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            empBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    empBtn.setBackground(new Color(200, 220, 240));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    empBtn.setBackground(Color.WHITE);
                }
            });
            empBtn.addActionListener(e -> showEmployeeManagementWindow());
            sidebar.add(Box.createVerticalStrut(10));
            sidebar.add(empBtn);
        }

        // 仅admin显示Payroll Management按钮和卡片（放在所有按钮和卡片最后，确保不会被覆盖）
        if (emp.getRole() != null && emp.getRole().equalsIgnoreCase("admin")) {
            JButton payrollMgmtBtn = new JButton("Payroll Management");
            payrollMgmtBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            payrollMgmtBtn.setMaximumSize(new Dimension(160, 40));
            payrollMgmtBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            payrollMgmtBtn.setFocusPainted(false);
            payrollMgmtBtn.setBackground(Color.WHITE);
            payrollMgmtBtn.setForeground(new Color(33, 150, 243));
            payrollMgmtBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            payrollMgmtBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    payrollMgmtBtn.setBackground(new Color(200, 220, 240));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    payrollMgmtBtn.setBackground(Color.WHITE);
                }
            });
            payrollMgmtBtn.addActionListener(e -> showAdminPayrollWindow(emp));
            sidebar.add(Box.createVerticalStrut(10));
            sidebar.add(payrollMgmtBtn);

            JPanel payrollMgmtCard = createCard("Payroll Management", "Admin: generate payroll for employees");
            cardPanel.add(payrollMgmtCard);
            payrollMgmtCard.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    showAdminPayrollWindow(emp);
                }
            });
        }

        // 清空sidebar和cardPanel，避免重复和覆盖
        sidebar.removeAll();
        cardPanel.removeAll();

        // 公共按钮
        JButton dashboardBtn = new JButton("Dashboard");
        dashboardBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        dashboardBtn.setMaximumSize(new Dimension(160, 40));
        dashboardBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dashboardBtn.setFocusPainted(false);
        dashboardBtn.setBackground(Color.WHITE);
        dashboardBtn.setForeground(new Color(33, 150, 243));
        dashboardBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(dashboardBtn);

        // admin端专属
        if (emp.getRole() != null && emp.getRole().equalsIgnoreCase("admin")) {
            JButton payrollMgmtBtn = new JButton("Payroll Management");
            payrollMgmtBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            payrollMgmtBtn.setMaximumSize(new Dimension(160, 40));
            payrollMgmtBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            payrollMgmtBtn.setFocusPainted(false);
            payrollMgmtBtn.setBackground(Color.WHITE);
            payrollMgmtBtn.setForeground(new Color(33, 150, 243));
            payrollMgmtBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            payrollMgmtBtn.addActionListener(e -> showAdminPayrollWindow(emp));
            sidebar.add(Box.createVerticalStrut(10));
            sidebar.add(payrollMgmtBtn);

            JPanel payrollMgmtCard = createCard("Payroll Management", "Admin: generate payroll for employees");
            payrollMgmtCard.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    showAdminPayrollWindow(emp);
                }
            });
            cardPanel.add(payrollMgmtCard);

            // 只为admin显示员工管理
            JButton empBtn = new JButton("Employee Management");
            empBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            empBtn.setMaximumSize(new Dimension(160, 40));
            empBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            empBtn.setFocusPainted(false);
            empBtn.setBackground(Color.WHITE);
            empBtn.setForeground(new Color(33, 150, 243));
            empBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            empBtn.addActionListener(e -> showEmployeeManagementWindow());
            sidebar.add(Box.createVerticalStrut(10));
            sidebar.add(empBtn);

            JPanel empCard = createCard("Employee Management", "Add, view, and delete employees");
            empCard.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    showEmployeeManagementWindow();
                }
            });
            cardPanel.add(empCard);
        } else {
            // employee端
            JButton payrollBtn = new JButton("Payroll");
            payrollBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            payrollBtn.setMaximumSize(new Dimension(160, 40));
            payrollBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            payrollBtn.setFocusPainted(false);
            payrollBtn.setBackground(Color.WHITE);
            payrollBtn.setForeground(new Color(33, 150, 243));
            payrollBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            payrollBtn.addActionListener(e -> showPayrollWindow(emp));
            sidebar.add(Box.createVerticalStrut(10));
            sidebar.add(payrollBtn);

            JPanel empPayrollCard = createCard("Payroll", "View your payslips and salary details");
            empPayrollCard.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    showPayrollWindow(emp);
                }
            });
            cardPanel.add(empPayrollCard);
        }

        // 公共按钮和卡片
        JButton reportsBtn = new JButton("Reports");
        reportsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        reportsBtn.setMaximumSize(new Dimension(160, 40));
        reportsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        reportsBtn.setFocusPainted(false);
        reportsBtn.setBackground(Color.WHITE);
        reportsBtn.setForeground(new Color(33, 150, 243));
        reportsBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        reportsBtn.addActionListener(e -> showReportWindow(emp));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(reportsBtn);

        JPanel reportsCard = createCard("Reports", "Generate and view payroll reports");
        reportsCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showReportWindow(emp);
            }
        });
        cardPanel.add(reportsCard);

        JButton settingsBtn = new JButton("Settings");
        settingsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsBtn.setMaximumSize(new Dimension(160, 40));
        settingsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        settingsBtn.setFocusPainted(false);
        settingsBtn.setBackground(Color.WHITE);
        settingsBtn.setForeground(new Color(33, 150, 243));
        settingsBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        settingsBtn.addActionListener(e -> showSettingDialog(frame, emp, welcome, userCircle));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(settingsBtn);

        JPanel settingsCard = createCard("Settings", "Update your profile and preferences");
        settingsCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showSettingDialog(frame, emp, welcome, userCircle);
            }
        });
        cardPanel.add(settingsCard);

        // 强制刷新界面
        sidebar.revalidate(); sidebar.repaint();
        cardPanel.revalidate(); cardPanel.repaint();

        frame.setVisible(true);
    }

    /**
     * Creates a card component for the dashboard.
     * @param title Card title
     * @param desc Card description
     * @return JPanel representing the card
     */
    private JPanel createCard(String title, String desc) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(220, 110));
        card.setBackground(new Color(245, 247, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 235, 245), 1, true),
                new EmptyBorder(16, 16, 16, 16)));
        card.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(33, 150, 243));
        JLabel descLabel = new JLabel("<html><body style='width:180px'>" + desc + "</body></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(80, 80, 80));
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(225, 235, 245));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(245, 247, 250));
            }
        });
        return card;
    }

    /**
     * Shows the user settings dialog for updating profile.
     * @param parent Parent JFrame
     * @param emp Employee object
     * @param welcome Welcome label to update
     * @param userBtn User button to update
     */
    private void showSettingDialog(JFrame parent, Employee emp, JLabel welcome, JButton userBtn) {
        JDialog dialog = new JDialog(parent, "User Settings", true);
        dialog.setSize(500, 300); // 增大窗口宽度
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 30, 10, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField(emp.getFirstName(), 16);
        firstNameField.setPreferredSize(new Dimension(350, 28)); // 增大输入框宽度
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField(emp.getLastName(), 16);
        lastNameField.setPreferredSize(new Dimension(350, 28)); // 增大输入框宽度
        JLabel icPassportLabel = new JLabel("IC/Passport:");
        JTextField icPassportField = new JTextField(emp.getIcPassport(), 16);
        icPassportField.setPreferredSize(new Dimension(350, 28)); // 增大输入框宽度
        JLabel passwordLabel = new JLabel("New Password:");
        JPasswordField passwordField = new JPasswordField(16);
        passwordField.setPreferredSize(new Dimension(350, 28)); // 增大输入框宽度
        JButton saveBtn = new JButton("Save");
        saveBtn.setPreferredSize(new Dimension(120, 32));
        saveBtn.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(firstNameLabel, gbc);
        gbc.gridx = 1;
        panel.add(firstNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lastNameLabel, gbc);
        gbc.gridx = 1;
        panel.add(lastNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(icPassportLabel, gbc);
        gbc.gridx = 1;
        panel.add(icPassportField, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.add(saveBtn);
        panel.add(btnPanel, gbc);

        dialog.add(panel, BorderLayout.CENTER);

        saveBtn.addActionListener(ev -> {
            emp.setFirstName(firstNameField.getText());
            emp.setLastName(lastNameField.getText());
            emp.setIcPassport(icPassportField.getText());
            String newPwd = new String(passwordField.getPassword());
            if (!newPwd.isEmpty()) {
                emp.setPassword(newPwd);
            }
            try {
                boolean ok = employeeService.updateProfile(emp);
                if (ok) {
                    JOptionPane.showMessageDialog(dialog, "Profile updated successfully!");
                    welcome.setText("Welcome, " + emp.getFirstName() + "!");
                    userBtn.setText(emp.getUsername());
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Update failed!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    /**
     * Shows the payroll records window for the employee.
     * @param emp Employee object
     */
    private void showPayrollWindow(Employee emp) {
        JFrame frame = new JFrame("Payroll Records");
        frame.setSize(650, 420);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Payroll Records for " + emp.getFirstName(), SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topPanel.add(title, BorderLayout.WEST);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnPanel.add(refreshBtn);
        // 仅admin显示“Generate Current Month Payroll”按钮
        if (emp.getRole() != null && emp.getRole().equalsIgnoreCase("admin")) {
            JButton calcBtn = new JButton("Generate Current Month Payroll");
            calcBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btnPanel.add(calcBtn);
            calcBtn.addActionListener(e -> {
                String period = java.time.YearMonth.now().toString();
                try {
                    common.PayrollRecord rec = payrollService.calculatePayroll(emp.getId(), period);
                    javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel)((JTable)((JScrollPane)frame.getContentPane().getComponent(1)).getViewport().getView()).getModel();
                    model.addRow(new Object[]{rec.getPeriod(), rec.getGrossPay(), rec.getDeductions(), rec.getNetPay()});
                    JOptionPane.showMessageDialog(frame, "Payroll generated for " + period, "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
        topPanel.add(btnPanel, BorderLayout.EAST);
        frame.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Period", "Gross Pay", "Deductions", "Net Pay"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.setSelectionBackground(new Color(225, 235, 245));
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        Runnable loadRecords = () -> {
            model.setRowCount(0);
            try {
                java.util.List<common.PayrollRecord> records = payrollService.getPayrollRecords(emp.getId());
                for (common.PayrollRecord rec : records) {
                    model.addRow(new Object[]{rec.getPeriod(), rec.getGrossPay(), rec.getDeductions(), rec.getNetPay()});
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error loading payroll records: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        loadRecords.run();

        refreshBtn.addActionListener(e -> {
            loadRecords.run();
            JOptionPane.showMessageDialog(frame, "Payroll records refreshed.", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        frame.setVisible(true);
    }

    /**
     * Shows the payroll reports window.
     * @param emp Employee object
     */
    private void showReportWindow(Employee emp) {
        JFrame frame = new JFrame("Payroll Reports");
        frame.setSize(750, 420);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel();
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topPanel.add(title, BorderLayout.WEST);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topPanel.add(refreshBtn, BorderLayout.EAST);
        frame.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Employee", "Period", "Base Salary", "Allowance", "Gross Pay", "Deductions", "Net Pay"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.setSelectionBackground(new Color(225, 235, 245));
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        Runnable loadRecords = () -> {
            model.setRowCount(0);
            try {
                if (emp.getRole() != null && emp.getRole().equalsIgnoreCase("admin")) {
                    title.setText("All Employees Payroll Report");
                    java.util.List<common.PayrollRecord> records = payrollService.getAllPayrollRecords();
                    for (common.PayrollRecord rec : records) {
                        model.addRow(new Object[]{
                            rec.getEmployeeId(),
                            rec.getPeriod(),
                            rec.getBaseSalary(),
                            rec.getAllowance(),
                            rec.getGrossPay(),
                            rec.getDeductions(),
                            rec.getNetPay()
                        });
                    }
                } else {
                    title.setText("My Payroll Report");
                    java.util.List<common.PayrollRecord> records = payrollService.getPayrollRecords(emp.getId());
                    for (common.PayrollRecord rec : records) {
                        model.addRow(new Object[]{
                            emp.getUsername(),
                            rec.getPeriod(),
                            rec.getBaseSalary(),
                            rec.getAllowance(),
                            rec.getGrossPay(),
                            rec.getDeductions(),
                            rec.getNetPay()
                        });
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error loading payroll records: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        loadRecords.run();

        refreshBtn.addActionListener(e -> {
            loadRecords.run();
            JOptionPane.showMessageDialog(frame, "Report refreshed.", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        // 所有用户都可下载自己的工资单CSV
        JButton csvBtn = new JButton("Download CSV");
        csvBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topPanel.add(csvBtn, BorderLayout.CENTER);
        csvBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("payroll_report.csv"));
            int option = fileChooser.showSaveDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                try (java.io.PrintWriter pw = new java.io.PrintWriter(file, "UTF-8")) {
                    // 写表头
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        pw.print(model.getColumnName(i));
                        if (i < model.getColumnCount() - 1) pw.print(",");
                    }
                    pw.println();
                    // 写数据
                    for (int row = 0; row < model.getRowCount(); row++) {
                        for (int col = 0; col < model.getColumnCount(); col++) {
                            pw.print(model.getValueAt(row, col));
                            if (col < model.getColumnCount() - 1) pw.print(",");
                        }
                        pw.println();
                    }
                    JOptionPane.showMessageDialog(frame, "CSV exported successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error exporting CSV: " + ex.getMessage());
                }
            }
        });

        frame.setVisible(true);
    }

    /**
     * Shows the employee management window (admin only).
     */
    private void showEmployeeManagementWindow() {
        JFrame frame = new JFrame("Employee Management");
        frame.setSize(800, 480);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("All Employees", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topPanel.add(title, BorderLayout.WEST);
        JButton addBtn = new JButton("Add Employee");
        addBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton delBtn = new JButton("Delete Selected");
        delBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnPanel.add(addBtn);
        btnPanel.add(delBtn);
        topPanel.add(btnPanel, BorderLayout.EAST);
        frame.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Username", "First Name", "Last Name", "IC/Passport", "Role"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.setSelectionBackground(new Color(225, 235, 245));
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        Runnable loadEmployees = () -> {
            model.setRowCount(0);
            try {
                final java.util.List<common.Employee> employees = employeeService.getAllEmployees();
                for (common.Employee emp : employees) {
                    model.addRow(new Object[]{emp.getId(), emp.getUsername(), emp.getFirstName(), emp.getLastName(), emp.getIcPassport(), emp.getRole()});
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error loading employees: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        loadEmployees.run();

        addBtn.addActionListener(e -> {
            JDialog dialog = new JDialog(frame, "Add Employee", true);
            dialog.setSize(350, 350);
            dialog.setLocationRelativeTo(frame);
            dialog.setLayout(new BorderLayout());
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new EmptyBorder(10, 30, 10, 30));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            JLabel userLabel = new JLabel("Username:");
            JTextField usernameField = new JTextField(16);
            JLabel passLabel = new JLabel("Password:");
            JPasswordField passwordField = new JPasswordField(16);
            JLabel firstNameLabel = new JLabel("First Name:");
            JTextField firstNameField = new JTextField(16);
            JLabel lastNameLabel = new JLabel("Last Name:");
            JTextField lastNameField = new JTextField(16);
            JLabel icPassportLabel = new JLabel("IC/Passport:");
            JTextField icPassportField = new JTextField(16);
            JLabel roleLabel = new JLabel("Role:");
            JComboBox<String> roleBox = new JComboBox<>(new String[]{"employee", "admin"});
            JButton submitBtn = new JButton("Submit");
            submitBtn.setPreferredSize(new Dimension(120, 32));
            submitBtn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(userLabel, gbc);
            gbc.gridx = 1;
            panel.add(usernameField, gbc);
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(passLabel, gbc);
            gbc.gridx = 1;
            panel.add(passwordField, gbc);
            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(firstNameLabel, gbc);
            gbc.gridx = 1;
            panel.add(firstNameField, gbc);
            gbc.gridx = 0; gbc.gridy = 3;
            panel.add(lastNameLabel, gbc);
            gbc.gridx = 1;
            panel.add(lastNameField, gbc);
            gbc.gridx = 0; gbc.gridy = 4;
            panel.add(icPassportLabel, gbc);
            gbc.gridx = 1;
            panel.add(icPassportField, gbc);
            gbc.gridx = 0; gbc.gridy = 5;
            panel.add(roleLabel, gbc);
            gbc.gridx = 1;
            panel.add(roleBox, gbc);
            gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
            JPanel btnPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            btnPanel2.add(submitBtn);
            panel.add(btnPanel2, gbc);
            dialog.add(panel, BorderLayout.CENTER);
            submitBtn.addActionListener(ev -> {
                common.Employee emp = new common.Employee();
                emp.setUsername(usernameField.getText());
                emp.setPassword(new String(passwordField.getPassword()));
                emp.setFirstName(firstNameField.getText());
                emp.setLastName(lastNameField.getText());
                emp.setIcPassport(icPassportField.getText());
                
                // 修复：确保admin1用户设置为admin角色
                String selectedRole = (String) roleBox.getSelectedItem();
                if (usernameField.getText().equals("admin1")) {
                    selectedRole = "admin";
                }
                emp.setRole(selectedRole);
                
                try {
                    boolean ok = employeeService.register(emp);
                    if (ok) {
                        JOptionPane.showMessageDialog(dialog, "Employee added successfully!");
                        dialog.dispose();
                        loadEmployees.run();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Add failed! Username may exist.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                }
            });
            dialog.setVisible(true);
        });

        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Please select an employee to delete.");
                return;
            }
            int id = (int) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure to delete employee ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    boolean ok = employeeService.deleteEmployee(id);
                    if (ok) {
                        JOptionPane.showMessageDialog(frame, "Employee deleted.");
                        loadEmployees.run();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Delete failed!");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });

        frame.setVisible(true);
    }

    /**
     * Shows the admin payroll generation window.
     * @param emp Admin Employee object
     */
    private void showAdminPayrollWindow(Employee emp) {
        JFrame frame = new JFrame("Admin Payroll Generation");
        frame.setSize(650, 450);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel empLabel = new JLabel("Select Employee:");
        JComboBox<String> empBox = new JComboBox<>();
        final java.util.List<common.Employee> employees;
        try {
            employees = employeeService.getAllEmployees();
            for (common.Employee e : employees) {
                empBox.addItem(e.getUsername() + " (" + e.getFirstName() + ")");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error loading employees: " + ex.getMessage());
            return;
        }

        JLabel grossLabel = new JLabel("Gross Pay:");
        JTextField grossField = new JTextField(12);
        JLabel netLabel = new JLabel("Net Pay (after EPF):");
        JTextField netField = new JTextField(12);
        netField.setEditable(false);
        JButton sendBtn = new JButton("Send Payroll");
        sendBtn.setPreferredSize(new Dimension(140, 32));
        sendBtn.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(empLabel, gbc);
        gbc.gridx = 1;
        panel.add(empBox, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(grossLabel, gbc);
        gbc.gridx = 1;
        panel.add(grossField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(netLabel, gbc);
        gbc.gridx = 1;
        panel.add(netField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.add(sendBtn);
        panel.add(btnPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        frame.add(scrollPane, BorderLayout.CENTER);

        // 更详细的工资单生成窗口
        JLabel baseLabel = new JLabel("Base Salary:");
        JTextField baseField = new JTextField(12);
        JLabel overtimeHoursLabel = new JLabel("Overtime Hours:");
        JTextField overtimeHoursField = new JTextField(12);
        JLabel overtimeRateLabel = new JLabel("Overtime Rate:");
        JTextField overtimeRateField = new JTextField(12);
        JLabel bonusLabel = new JLabel("Bonus:");
        JTextField bonusField = new JTextField(12);
        JLabel allowanceLabel = new JLabel("Allowance:");
        JTextField allowanceField = new JTextField(12);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(baseLabel, gbc);
        gbc.gridx = 1;
        panel.add(baseField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(overtimeHoursLabel, gbc);
        gbc.gridx = 1;
        panel.add(overtimeHoursField, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(overtimeRateLabel, gbc);
        gbc.gridx = 1;
        panel.add(overtimeRateField, gbc);
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(bonusLabel, gbc);
        gbc.gridx = 1;
        panel.add(bonusField, gbc);
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(allowanceLabel, gbc);
        gbc.gridx = 1;
        panel.add(allowanceField, gbc);
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(grossLabel, gbc);
        gbc.gridx = 1;
        panel.add(grossField, gbc);
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(netLabel, gbc);
        gbc.gridx = 1;
        panel.add(netField, gbc);
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);
        // 自动计算gross pay和net pay
        javax.swing.event.DocumentListener autoCalc = new javax.swing.event.DocumentListener() {
            void update() {
                double base = parseOrZero(baseField.getText());
                double overtimeHours = parseOrZero(overtimeHoursField.getText());
                double overtimeRate = parseOrZero(overtimeRateField.getText());
                double bonus = parseOrZero(bonusField.getText());
                double allowance = parseOrZero(allowanceField.getText());
                
                // 修复：正确计算总工资，包含所有组件
                double overtimePay = overtimeHours * overtimeRate;
                double gross = base + overtimePay + bonus + allowance;
                grossField.setText(String.format("%.2f", gross));
                
                // 修复：正确计算净工资，使用EPF 11%扣除
                double net = gross - (gross * 0.11);
                netField.setText(String.format("%.2f", net));
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        };
        baseField.getDocument().addDocumentListener(autoCalc);
        overtimeHoursField.getDocument().addDocumentListener(autoCalc);
        overtimeRateField.getDocument().addDocumentListener(autoCalc);
        bonusField.getDocument().addDocumentListener(autoCalc);
        allowanceField.getDocument().addDocumentListener(autoCalc);

        sendBtn.addActionListener(e -> {
            int idx = empBox.getSelectedIndex();
            if (idx == -1) {
                JOptionPane.showMessageDialog(frame, "Please select an employee.");
                return;
            }
            common.Employee selectedEmp = employees.get(idx);
            // 使用新的解析方法，保留负数值用于校验
            double baseSalary = parseDoubleOrZero(baseField.getText());
            double overtimeHours = parseDoubleOrZero(overtimeHoursField.getText());
            double overtimeRate = parseDoubleOrZero(overtimeRateField.getText());
            double bonus = parseDoubleOrZero(bonusField.getText());
            double allowance = parseDoubleOrZero(allowanceField.getText());
            
            // 添加调试信息
            System.out.println("[CLIENT] Parsed values:");
            System.out.println("[CLIENT] Base Salary: " + baseSalary);
            System.out.println("[CLIENT] Overtime Hours: " + overtimeHours);
            System.out.println("[CLIENT] Overtime Rate: " + overtimeRate);
            System.out.println("[CLIENT] Bonus: " + bonus);
            System.out.println("[CLIENT] Allowance: " + allowance);
            
            // 客户端校验：不允许任何负数
            if (baseSalary < 0 || overtimeHours < 0 || overtimeRate < 0 || bonus < 0 || allowance < 0) {
                System.out.println("[CLIENT] Negative value detected! Blocking payroll generation.");
                JOptionPane.showMessageDialog(frame, "Amounts cannot be negative.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String period = java.time.YearMonth.now().toString();
            try {
                common.PayrollRecord rec = payrollService.calculatePayroll(
                    selectedEmp.getId(), period, baseSalary, overtimeHours, overtimeRate, bonus, allowance);
                
                // 添加调试信息
                System.out.println("[CLIENT] Received payroll record from server:");
                System.out.println("[CLIENT] Base Salary: " + rec.getBaseSalary());
                System.out.println("[CLIENT] Overtime Hours: " + rec.getOvertimeHours());
                System.out.println("[CLIENT] Overtime Rate: " + rec.getOvertimeRate());
                System.out.println("[CLIENT] Bonus: " + rec.getBonus());
                System.out.println("[CLIENT] Allowance: " + rec.getAllowance());
                System.out.println("[CLIENT] Gross Pay: " + rec.getGrossPay());
                System.out.println("[CLIENT] Deductions: " + rec.getDeductions());
                System.out.println("[CLIENT] Net Pay: " + rec.getNetPay());
                
                JOptionPane.showMessageDialog(frame, "Payroll sent to " + selectedEmp.getUsername() + "!\nNet Pay: " + rec.getNetPay());
                frame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        frame.setVisible(true);
    }

    /**
     * Utility method to parse a string to double, returns 0.0 if invalid.
     * @param text Input string
     * @return Parsed double value or 0.0
     */
    private double parseOrZero(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Utility method to parse a string to double, returns 0.0 if invalid.
     * This method preserves negative values for validation purposes.
     * @param text Input string
     * @return Parsed double value or 0.0
     */
    private double parseDoubleOrZero(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PayrollClient::new);
    }
} 