package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Server {
    public static void main(String[] args) {
        try {
            // 初始化数据库表
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Employee (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(255) UNIQUE, password VARCHAR(255), firstName VARCHAR(255), lastName VARCHAR(255), icPassport VARCHAR(255), role VARCHAR(255))");
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Payroll (id INT AUTO_INCREMENT PRIMARY KEY, employeeId INT, period VARCHAR(255), baseSalary DOUBLE, overtimeHours DOUBLE, overtimeRate DOUBLE, bonus DOUBLE, allowance DOUBLE, grossPay DOUBLE, deductions DOUBLE, netPay DOUBLE, FOREIGN KEY (employeeId) REFERENCES Employee(id))");
            }

            // 启动纯RMI注册表，完全禁用SSL
            System.setProperty("java.rmi.server.hostname", "0.0.0.0");
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("EmployeeService", new EmployeeServiceImpl());
            registry.rebind("PayrollService", new PayrollServiceImpl());

            System.out.println("Server started. Pure RMI (No SSL).");
            System.out.println("Listening on port 1099 for all network interfaces.");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 