package server;

import common.Employee;
import common.EmployeeService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * EmployeeServiceImpl provides RMI-based employee management services.
 * Handles registration, login, profile update, and employee queries.
 */
public class EmployeeServiceImpl extends UnicastRemoteObject implements EmployeeService {
    private EmployeeDAO employeeDAO;

    public EmployeeServiceImpl() throws RemoteException {
        super();
        employeeDAO = new EmployeeDAO();
    }

    /**
     * Registers a new employee in the system.
     * @param employee Employee object to register
     * @return true if registration is successful, false otherwise
     */
    @Override
    public boolean register(Employee employee) throws RemoteException {
        System.out.println("[SERVER] User registration: " + employee.getUsername());
        // 多线程演示：用Thread异步执行插入操作
        final boolean[] result = new boolean[1];
        Thread t = new Thread(() -> {
            result[0] = employeeDAO.insert(employee);
        });
        t.start();
        try {
            t.join(); // 等待线程执行完毕（演示用，实际可用更高级的异步方式）
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("[SERVER] Registration result: " + (result[0] ? "Success" : "Fail"));
        return result[0];
    }

    /**
     * Authenticates an employee by username and password.
     * @param username Employee username
     * @param password Employee password
     * @return Employee object if login is successful, null otherwise
     */
    @Override
    public Employee login(String username, String password) throws RemoteException {
        System.out.println("[SERVER] User login: " + username);
        System.out.println("[SERVER] Employee.class loaded from: " + Employee.class.getProtectionDomain().getCodeSource().getLocation());
        System.out.println("Login try: " + username + " / " + password);
        Employee emp = employeeDAO.getByUsername(username);
        if (emp == null) {
            System.out.println("[SERVER] Login failed: user not found");
        } else {
            System.out.println("DB user: " + emp.getUsername() + " / " + emp.getPassword());
        }
        if (emp != null && emp.getPassword().equals(password)) {
            System.out.println("[SERVER] Login success: " + username);
            return emp;
        }
        System.out.println("[SERVER] Login failed: wrong password");
        return null;
    }

    /**
     * Updates the profile of an employee.
     * @param employee Employee object with updated info
     * @return true if update is successful, false otherwise
     */
    @Override
    public boolean updateProfile(Employee employee) throws RemoteException {
        System.out.println("[SERVER] User update profile: " + employee.getUsername());
        // 这里只做最小实现，假设有 update 方法
        boolean ok = employeeDAO.update(employee);
        System.out.println("[SERVER] Update result: " + (ok ? "Success" : "Fail"));
        return ok;
    }

    /**
     * Retrieves an employee by username.
     * @param username Employee username
     * @return Employee object if found, null otherwise
     */
    @Override
    public Employee getEmployeeByUsername(String username) throws RemoteException {
        System.out.println("[SERVER] Query user: " + username);
        return employeeDAO.getByUsername(username);
    }

    /**
     * Retrieves all employees in the system.
     * @return List of Employee objects
     */
    @Override
    public java.util.List<Employee> getAllEmployees() throws RemoteException {
        System.out.println("[SERVER] Query all employees");
        return employeeDAO.getAll();
    }

    /**
     * Deletes an employee by ID.
     * @param id Employee ID
     * @return true if deletion is successful, false otherwise
     */
    @Override
    public boolean deleteEmployee(int id) throws RemoteException {
        System.out.println("[SERVER] Delete employee: " + id);
        boolean ok = employeeDAO.delete(id);
        System.out.println("[SERVER] Delete result: " + (ok ? "Success" : "Fail"));
        return ok;
    }
} 