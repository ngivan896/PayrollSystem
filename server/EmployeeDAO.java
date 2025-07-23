package server;

import common.Employee;
import java.sql.*;

public class EmployeeDAO {
    public boolean insert(Employee employee) {
        String sql = "INSERT INTO Employee (username, password, firstName, lastName, icPassport, role) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employee.getUsername());
            stmt.setString(2, employee.getPassword());
            stmt.setString(3, employee.getFirstName());
            stmt.setString(4, employee.getLastName());
            stmt.setString(5, employee.getIcPassport());
            stmt.setString(6, employee.getRole());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Employee getByUsername(String username) {
        String sql = "SELECT * FROM Employee WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Employee emp = new Employee();
                emp.setId(rs.getInt("id"));
                emp.setUsername(rs.getString("username"));
                emp.setPassword(rs.getString("password"));
                emp.setFirstName(rs.getString("firstName"));
                emp.setLastName(rs.getString("lastName"));
                emp.setIcPassport(rs.getString("icPassport"));
                emp.setRole(rs.getString("role"));
                return emp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Employee employee) {
        String sql = "UPDATE Employee SET firstName = ?, lastName = ?, icPassport = ?, password = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getIcPassport());
            stmt.setString(4, employee.getPassword());
            stmt.setInt(5, employee.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<Employee> getAll() {
        java.util.List<Employee> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM Employee";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Employee emp = new Employee();
                emp.setId(rs.getInt("id"));
                emp.setUsername(rs.getString("username"));
                emp.setPassword(rs.getString("password"));
                emp.setFirstName(rs.getString("firstName"));
                emp.setLastName(rs.getString("lastName"));
                emp.setIcPassport(rs.getString("icPassport"));
                emp.setRole(rs.getString("role"));
                list.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean delete(int id) {
        String deletePayroll = "DELETE FROM Payroll WHERE employeeId = ?";
        String deleteEmployee = "DELETE FROM Employee WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            // 先删工资单
            try (PreparedStatement stmt1 = conn.prepareStatement(deletePayroll)) {
                stmt1.setInt(1, id);
                stmt1.executeUpdate();
            }
            // 再删员工
            try (PreparedStatement stmt2 = conn.prepareStatement(deleteEmployee)) {
                stmt2.setInt(1, id);
                return stmt2.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 