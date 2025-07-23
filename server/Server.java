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
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Payroll (id INT AUTO_INCREMENT PRIMARY KEY, employeeId INT, period VARCHAR(255), baseSalary DOUBLE, allowance DOUBLE, grossPay DOUBLE, deductions DOUBLE, netPay DOUBLE, FOREIGN KEY (employeeId) REFERENCES Employee(id))");
            }

            // 启动 RMI 注册表
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("EmployeeService", new EmployeeServiceImpl());
            registry.rebind("PayrollService", new PayrollServiceImpl());

            System.out.println("Server started. RMI services bound.");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 