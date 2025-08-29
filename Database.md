# Database Management & Multithreading Implementation

> This document explains **how the project manages data (persistence layer)** and **how multithreading is implemented** to handle concurrent requests.  All code shown below is taken from the current code-base and slightly trimmed for clarity.

---

## 1. Database Management

The application uses an **embedded H2 database** (`h2-2.3.232.jar`) so the system is self-contained and runs on any machine without installing an external DBMS.

### 1.1 Database Initialisation
The tables are created automatically when the server starts.  The `Server` class runs DDL statements if tables are missing.

```java
// Server.java – inside main()
try (Connection conn = DatabaseConnection.getConnection();
     Statement stmt  = conn.createStatement()) {
    // employees table
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Employee (\n" +
        "  id INT AUTO_INCREMENT PRIMARY KEY,\n" +
        "  username   VARCHAR(255) UNIQUE,\n" +
        "  password   VARCHAR(255),\n" +
        "  firstName  VARCHAR(255),\n" +
        "  lastName   VARCHAR(255),\n" +
        "  icPassport VARCHAR(255),\n" +
        "  role       VARCHAR(255))");

    // payroll table (FK to Employee)
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Payroll (\n" +
        "  id INT AUTO_INCREMENT PRIMARY KEY,\n" +
        "  employeeId INT,\n" +
        "  period     VARCHAR(255),\n" +
        "  baseSalary DOUBLE,\n" +
        "  allowance  DOUBLE,\n" +
        "  grossPay   DOUBLE,\n" +
        "  deductions DOUBLE,\n" +
        "  netPay     DOUBLE,\n" +
        "  FOREIGN KEY (employeeId) REFERENCES Employee(id))");
}
```

### 1.2 Connection Handling  
`DatabaseConnection` is a tiny helper that centralises **JDBC URL, username and password**.  Every DAO obtains connections through this class, keeping credentials in one place.

```java
// DatabaseConnection.java
public class DatabaseConnection {
    private static final String URL  = "jdbc:h2:file:./payroll";
    private static final String USER = "sa";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
```

### 1.3 DAO Pattern
All SQL logic is isolated in **Data-Access-Objects** so that UI / service layers remain database-agnostic.

**Example – inserting an employee**
```java
// EmployeeDAO.java
public boolean insert(Employee employee) {
    String sql = "INSERT INTO Employee\n" +
                 "(username, password, firstName, lastName, icPassport, role)\n" +
                 "VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, employee.getUsername());
        stmt.setString(2, employee.getPassword());
        stmt.setString(3, employee.getFirstName());
        stmt.setString(4, employee.getLastName());
        stmt.setString(5, employee.getIcPassport());
        stmt.setString(6, employee.getRole());
        return stmt.executeUpdate() > 0; // 1 = success
    } catch (SQLException ex) {
        ex.printStackTrace();
        return false;
    }
}
```

### 1.4 Referential Integrity
The `Payroll` table contains a **foreign-key constraint** pointing to `Employee(id)`.  Deleting an employee therefore requires a two-step operation carried out in `EmployeeDAO.delete(int id)` – first remove payroll rows, then the employee – guaranteeing that **no orphan records remain**.

```java
// EmployeeDAO.delete()
String deletePayroll  = "DELETE FROM Payroll WHERE employeeId = ?";
String deleteEmployee = "DELETE FROM Employee WHERE id = ?";
...
```

### 1.5 Advantages
* No external dependencies – H2 runs in-process.
* Strong encapsulation of SQL – DAO layer.
* Automatic table creation – zero setup for new environments.
* Foreign keys enforce data consistency.

---

## 2. Multithreading Implementation

### 2.1 RMI Built-in Concurrency
Java RMI automatically spawns a dedicated **worker thread** for each remote request.  Therefore the server can already serve multiple clients in parallel.

### 2.2 Custom Threads for Lengthy Tasks
For operations that may take noticeable time (e.g. inserting a new employee after heavy validation) an additional thread is started so that the RMI worker thread returns quickly and keeps the thread pool free.

```java
// EmployeeServiceImpl – user registration (simplified)
public boolean register(Employee employee) throws RemoteException {
    final boolean[] result = new boolean[1];
    Thread t = new Thread(() -> result[0] = employeeDAO.insert(employee));
    t.start();
    try { t.join(); } catch (InterruptedException e) { return false; }
    return result[0];
}
```

### 2.3 Scaling with ExecutorService *(optional upgrade)*
If registration or other tasks become CPU-intensive, switching to a **thread pool** provides better reuse of threads and back-pressure control.

```java
// Example: using a fixed thread pool for DAO writes
private static final ExecutorService EXEC = Executors.newFixedThreadPool(8);

public boolean register(Employee emp) throws RemoteException {
    Future<Boolean> f = EXEC.submit(() -> employeeDAO.insert(emp));
    try { return f.get(); } catch (Exception e) { return false; }
}
```

### 2.4 Thread Safety Considerations
* **DAO objects are stateless** → safe to share across threads.
* H2 JDBC connections are created per method call, ensuring no concurrent access to the same `Connection` object.
* If executor pools are used, always shut them down gracefully on server exit.

---

## 3. Object-Oriented Principles in Use

| Principle        | Example Code & Explanation |
|------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Encapsulation** | `Employee` class keeps all fields `private` and exposes getters/setters only.  Internal state cannot be modified directly from the outside.<br>```java
public class Employee {
  private int id;
  private String username;
  public int getId() {return id;}
  public void setId(int id){this.id=id;}
}
``` |
| **Inheritance**   | `EmployeeDAO` extends `BaseDAO` (utility class) to reuse common JDBC helpers, reducing duplication.<br>```java
public class BaseDAO { /* common db helpers */ }
public class EmployeeDAO extends BaseDAO { /* extra methods */ }
``` |
| **Polymorphism**  | Service layer interacts with `PayrollService` interface; runtime implementation can be swapped without changing caller code.<br>```java
PayrollService svc = new PayrollServiceImpl();
svc.calculatePayroll(1,"2025-08");
``` |
| **Interface-based Design** | Both `EmployeeService` and `PayrollService` are pure interfaces exposed over RMI, decoupling client from server implementation.<br>```java
public interface EmployeeService extends Remote {
  boolean register(Employee e) throws RemoteException;
}
``` |

---

## 4. Summary
* **Database layer** employs H2, DAO pattern, and foreign keys for integrity.
* **Multithreading** relies on RMI’s inherent concurrency plus custom threads for lengthy tasks.
* **OOP principles** ensure clean, maintainable, and extensible architecture.

This document should provide sufficient technical depth for your report on database management and multithreading within the project.
