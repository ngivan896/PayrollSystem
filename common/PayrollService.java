package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface PayrollService extends Remote {
    PayrollRecord calculatePayroll(int employeeId, String period) throws RemoteException;
    PayrollRecord calculatePayroll(int employeeId, String period, double grossPay) throws RemoteException;
    PayrollRecord calculatePayroll(int employeeId, String period, double baseSalary, double overtimeHours, double overtimeRate, double bonus, double allowance) throws RemoteException;
    List<PayrollRecord> getPayrollRecords(int employeeId) throws RemoteException;
    List<PayrollRecord> getAllPayrollRecords() throws RemoteException;
} 