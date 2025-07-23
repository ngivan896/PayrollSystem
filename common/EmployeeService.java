package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EmployeeService extends Remote {
    boolean register(Employee employee) throws RemoteException;
    Employee login(String username, String password) throws RemoteException;
    boolean updateProfile(Employee employee) throws RemoteException;
    Employee getEmployeeByUsername(String username) throws RemoteException;
    java.util.List<Employee> getAllEmployees() throws RemoteException;
    boolean deleteEmployee(int id) throws RemoteException;
}
