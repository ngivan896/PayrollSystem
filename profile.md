# Update Profile & View Payslip Features

This document describes how an employee can **update their personal profile** and **view their payslip(s)** in the Payroll System.

---

## 1. Update Profile

### 1.1 User Flow
1. After logging-in, the user clicks **Settings** (either from the sidebar or the avatar menu).
2. A modal dialog appears allowing the user to edit first-name, last-name, IC/Passport and password.
3. When **Save** is pressed the client sends an `updateProfile()` request to the server.
4. The server validates and persists the changes; the UI updates immediately.

### 1.2 GUI Implementation (Client)
```java
// PayrollClient.java – showSettingDialog()
JDialog dialog = new JDialog(parent, "User Settings", true);
dialog.setSize(500, 300);
...
JTextField firstNameField = new JTextField(emp.getFirstName(), 16);
JPasswordField passwordField = new JPasswordField(16);
...
saveBtn.addActionListener(ev -> {
    emp.setFirstName(firstNameField.getText());
    emp.setLastName(lastNameField.getText());
    emp.setIcPassport(icPassportField.getText());
    String newPwd = new String(passwordField.getPassword());
    if (!newPwd.isEmpty()) emp.setPassword(newPwd);

    boolean ok = employeeService.updateProfile(emp); // RMI call
    if (ok) JOptionPane.showMessageDialog(dialog, "Profile updated successfully!");
});
```

### 1.3 Service Layer (Server)
```java

// EmployeeServiceImpl.java
@Override
public boolean updateProfile(Employee employee) throws RemoteException {
    System.out.println("[SERVER] User update profile: " + employee.getUsername());
    return employeeDAO.update(employee); // DAO persists changes
}
```

### 1.4 Data Persistence (DAO)
```java
// EmployeeDAO.java
public boolean update(Employee e) {
    String sql = "UPDATE Employee SET firstName=?, lastName=?, icPassport=?, password=? WHERE id=?";
    try (Connection c = DatabaseConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setString(1, e.getFirstName());
        ps.setString(2, e.getLastName());
        ps.setString(3, e.getIcPassport());
        ps.setString(4, e.getPassword());
        ps.setInt   (5, e.getId());
        return ps.executeUpdate() > 0;
    }
}
```

### 1.5 Security Considerations
* Passwords are **never logged**; only success/failure is reported.
* All traffic is encrypted via **TLS** so profile data is protected in transit.

---

## 2. View Payslip

### 2.1 User Flow
1. Employees click **Payroll** in the dashboard.
2. A window lists all payslip records (Period, Gross Pay, Deductions, Net Pay).
3. The user can refresh the list or (admins) generate new payslips.

### 2.2 GUI Implementation (Client)
```java
// PayrollClient.java – showPayrollWindow()
DefaultTableModel model = new DefaultTableModel(columns, 0) {
    public boolean isCellEditable(int r, int c) { return false; }
};
Runnable loadRecords = () -> {
    model.setRowCount(0);
    List<PayrollRecord> list = payrollService.getPayrollRecords(emp.getId());
    for (PayrollRecord pr : list) {
        model.addRow(new Object[]{pr.getPeriod(), pr.getGrossPay(), pr.getDeductions(), pr.getNetPay()});
    }
};
```

### 2.3 Service Layer (Server)
```java
// PayrollServiceImpl.java
@Override
public List<PayrollRecord> getPayrollRecords(int employeeId) throws RemoteException {
    System.out.println("[SERVER] Query payroll records for employeeId=" + employeeId);
    return payrollDAO.getByEmployeeId(employeeId);
}
```

### 2.4 Data Persistence (DAO)
```java
// PayrollDAO.java
public List<PayrollRecord> getByEmployeeId(int id) {
    List<PayrollRecord> list = new ArrayList<>();
    String sql = "SELECT * FROM Payroll WHERE employeeId=?";
    try (Connection c = DatabaseConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            PayrollRecord pr = new PayrollRecord();
            pr.setPeriod(rs.getString("period"));
            pr.setGrossPay(rs.getDouble("grossPay"));
            pr.setDeductions(rs.getDouble("deductions"));
            pr.setNetPay(rs.getDouble("netPay"));
            list.add(pr);
        }
    } catch (SQLException ex) { ex.printStackTrace(); }
    return list;
}
```

### 2.5 Exporting Payslips (CSV)
Employees can download their payslips as CSV for personal records.
```java
// PayrollClient.java – CSV export
try (PrintWriter pw = new PrintWriter(file, "UTF-8")) {
    for (int i = 0; i < model.getColumnCount(); i++) pw.print(model.getColumnName(i) + (i<model.getColumnCount()-1?",":""));
    pw.println();
    for (int r = 0; r < model.getRowCount(); r++) {
        for (int c = 0; c < model.getColumnCount(); c++) pw.print(model.getValueAt(r,c) + (c<model.getColumnCount()-1?",":""));
        pw.println();
    }
}
```

---

## 3. Summary
* **Update Profile**: GUI dialog → RMI `updateProfile()` → DAO `UPDATE` SQL.
* **View Payslip**: GUI table → RMI `getPayrollRecords()` → DAO `SELECT` SQL.
* All data transfers are encrypted via TLS, and DAO layer ensures consistent access to the H2 database.
