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
        // 默认用1000作为示例
        return calculatePayroll(employeeId, period, 1000.0);
    }

    @Override
    public PayrollRecord calculatePayroll(int employeeId, String period, double grossPay) throws RemoteException {
        double deductions = grossPay * 0.11;
        double netPay = grossPay - deductions;
        PayrollRecord record = new PayrollRecord();
        record.setEmployeeId(employeeId);
        record.setPeriod(period);
        record.setGrossPay(grossPay);
        record.setDeductions(deductions);
        record.setNetPay(netPay);
        payrollDAO.insert(record);
        return record;
    }

    @Override
    public PayrollRecord calculatePayroll(int employeeId, String period, double baseSalary, double overtimeHours, double overtimeRate, double bonus, double allowance) throws RemoteException {
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
        return record;
    }

    @Override
    public List<PayrollRecord> getPayrollRecords(int employeeId) throws RemoteException {
        return payrollDAO.getByEmployeeId(employeeId);
    }

    @Override
    public List<PayrollRecord> getAllPayrollRecords() throws RemoteException {
        // 这里只做最小实现，假设有 getAll 方法
        return payrollDAO.getAll();
    }
} 