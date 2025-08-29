package server;

import common.PayrollRecord;
import common.PayrollService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class PayrollServiceImpl extends UnicastRemoteObject implements PayrollService {
    private PayrollDAO payrollDAO;

    public PayrollServiceImpl() throws RemoteException {
        super();
        payrollDAO = new PayrollDAO();
    }

    @Override
    public PayrollRecord calculatePayroll(int employeeId, String period) throws RemoteException {
        System.out.println("[SERVER] Calculate payroll: employeeId=" + employeeId + ", period=" + period);
        // 原有实现
        return calculatePayroll(employeeId, period, 1000.0);
    }

    @Override
    public PayrollRecord calculatePayroll(int employeeId, String period, double grossPay) throws RemoteException {
        System.out.println("[SERVER] Calculate payroll (custom grossPay): employeeId=" + employeeId + ", period=" + period + ", grossPay=" + grossPay);
        // 校验：拒绝负数工资
        if (grossPay < 0) {
            throw new RemoteException("Gross pay cannot be negative.");
        }
        // 原有实现
        double deductions = grossPay * 0.11;
        double netPay = grossPay - deductions;
        PayrollRecord record = new PayrollRecord();
        record.setEmployeeId(employeeId);
        record.setPeriod(period);
        record.setGrossPay(grossPay);
        record.setDeductions(deductions);
        record.setNetPay(netPay);
        payrollDAO.insert(record);
        System.out.println("[SERVER] Payroll calculation result: " + (record != null ? "Success" : "Fail"));
        return record;
    }

    @Override
    public PayrollRecord calculatePayroll(int employeeId, String period, double baseSalary, double overtimeHours, double overtimeRate, double bonus, double allowance) throws RemoteException {
        System.out.println("[SERVER] Calculate payroll (detailed): employeeId=" + employeeId + ", period=" + period + ", baseSalary=" + baseSalary + ", overtimeHours=" + overtimeHours + ", overtimeRate=" + overtimeRate + ", bonus=" + bonus + ", allowance=" + allowance);
        // 校验：所有组成部分必须为非负
        if (baseSalary < 0) throw new RemoteException("Base salary cannot be negative.");
        if (overtimeHours < 0) throw new RemoteException("Overtime hours cannot be negative.");
        if (overtimeRate < 0) throw new RemoteException("Overtime rate cannot be negative.");
        if (bonus < 0) throw new RemoteException("Bonus cannot be negative.");
        if (allowance < 0) throw new RemoteException("Allowance cannot be negative.");

        // 修复：正确计算总工资，包含所有组件
        double overtimePay = overtimeHours * overtimeRate;
        double grossPay = baseSalary + overtimePay + bonus + allowance;
        if (grossPay < 0) {
            throw new RemoteException("Calculated gross pay cannot be negative.");
        }
        double deductions = grossPay * 0.11;
        double netPay = grossPay - deductions;
        
        System.out.println("[SERVER] Calculation details: baseSalary=" + baseSalary + ", overtimePay=" + overtimePay + ", bonus=" + bonus + ", allowance=" + allowance + ", grossPay=" + grossPay + ", deductions=" + deductions + ", netPay=" + netPay);
        
        PayrollRecord record = new PayrollRecord();
        record.setEmployeeId(employeeId);
        record.setPeriod(period);
        record.setBaseSalary(baseSalary);
        record.setOvertimeHours(overtimeHours);
        record.setOvertimeRate(overtimeRate);
        record.setBonus(bonus);
        record.setAllowance(allowance);
        record.setGrossPay(grossPay);
        record.setDeductions(deductions);
        record.setNetPay(netPay);
        payrollDAO.insert(record);
        System.out.println("[SERVER] Payroll calculation result: " + (record != null ? "Success" : "Fail"));
        return record;
    }

    @Override
    public List<PayrollRecord> getPayrollRecords(int employeeId) throws RemoteException {
        System.out.println("[SERVER] Query payroll records for employeeId=" + employeeId);
        return payrollDAO.getByEmployeeId(employeeId);
    }

    @Override
    public List<PayrollRecord> getAllPayrollRecords() throws RemoteException {
        System.out.println("[SERVER] Query all payroll records");
        return payrollDAO.getAll();
    }
} 