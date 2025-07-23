package server;

import common.Employee;
import common.EmployeeService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class EmployeeServiceImpl extends UnicastRemoteObject implements EmployeeService {
    private EmployeeDAO employeeDAO;

    public EmployeeServiceImpl() throws RemoteException {
        super();
        employeeDAO = new EmployeeDAO();
    }

    @Override
    public boolean register(Employee employee) throws RemoteException {
        return employeeDAO.insert(employee);
    }

    @Override
    public Employee login(String username, String password) throws RemoteException {
        System.out.println("[SERVER] Employee.class loaded from: " + Employee.class.getProtectionDomain().getCodeSource().getLocation());
        System.out.println("Login try: " + username + " / " + password);
        Employee emp = employeeDAO.getByUsername(username);
        if (emp == null) {
            System.out.println("No user found for username: " + username);
        } else {
            System.out.println("DB user: " + emp.getUsername() + " / " + emp.getPassword());
        }
        if (emp != null && emp.getPassword().equals(password)) {
            return emp;
        }
        return null;
    }

    @Override
    public boolean updateProfile(Employee employee) throws RemoteException {
        // 这里只做最小实现，假设有 update 方法
        return employeeDAO.update(employee);
    }

    @Override
    public Employee getEmployeeByUsername(String username) throws RemoteException {
        return employeeDAO.getByUsername(username);
    }

    @Override
    public java.util.List<Employee> getAllEmployees() throws RemoteException {
        return employeeDAO.getAll();
    }

    @Override
    public boolean deleteEmployee(int id) throws RemoteException {
        return employeeDAO.delete(id);
    }
} 