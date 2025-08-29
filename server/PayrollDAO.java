package server;

import common.PayrollRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO {
    public boolean insert(PayrollRecord record) {
        String sql = "INSERT INTO Payroll (employeeId, period, baseSalary, overtimeHours, overtimeRate, bonus, allowance, grossPay, deductions, netPay) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, record.getEmployeeId());
            stmt.setString(2, record.getPeriod());
            stmt.setDouble(3, record.getBaseSalary());
            stmt.setDouble(4, record.getOvertimeHours());
            stmt.setDouble(5, record.getOvertimeRate());
            stmt.setDouble(6, record.getBonus());
            stmt.setDouble(7, record.getAllowance());
            stmt.setDouble(8, record.getGrossPay());
            stmt.setDouble(9, record.getDeductions());
            stmt.setDouble(10, record.getNetPay());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<PayrollRecord> getByEmployeeId(int employeeId) {
        List<PayrollRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM Payroll WHERE employeeId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PayrollRecord record = new PayrollRecord();
                record.setId(rs.getInt("id"));
                record.setEmployeeId(rs.getInt("employeeId"));
                record.setPeriod(rs.getString("period"));
                record.setBaseSalary(rs.getDouble("baseSalary"));
                record.setOvertimeHours(rs.getDouble("overtimeHours"));
                record.setOvertimeRate(rs.getDouble("overtimeRate"));
                record.setBonus(rs.getDouble("bonus"));
                record.setAllowance(rs.getDouble("allowance"));
                record.setGrossPay(rs.getDouble("grossPay"));
                record.setDeductions(rs.getDouble("deductions"));
                record.setNetPay(rs.getDouble("netPay"));
                list.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<PayrollRecord> getAll() {
        List<PayrollRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM Payroll";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PayrollRecord record = new PayrollRecord();
                record.setId(rs.getInt("id"));
                record.setEmployeeId(rs.getInt("employeeId"));
                record.setPeriod(rs.getString("period"));
                record.setBaseSalary(rs.getDouble("baseSalary"));
                record.setOvertimeHours(rs.getDouble("overtimeHours"));
                record.setOvertimeRate(rs.getDouble("overtimeRate"));
                record.setBonus(rs.getDouble("bonus"));
                record.setAllowance(rs.getDouble("allowance"));
                record.setGrossPay(rs.getDouble("grossPay"));
                record.setDeductions(rs.getDouble("deductions"));
                record.setNetPay(rs.getDouble("netPay"));
                list.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
} 