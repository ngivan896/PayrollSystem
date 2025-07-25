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
        // 原有实现
        double grossPay = baseSalary + allowance;
        double deductions = grossPay * 0.11;
        double netPay = grossPay - deductions;
        PayrollRecord record = new PayrollRecord();
        record.setEmployeeId(employeeId);
        record.setPeriod(period);
        record.setBaseSalary(baseSalary);
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