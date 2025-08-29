# 分布式工资系统 (Distributed Payroll System)
## RMI + H2 Database + Multithreading Implementation

---

## 项目概述

### 系统架构
- **客户端**: Java Swing GUI + RMI Client
- **服务端**: RMI Server + H2 Database + Multithreading
- **通信**: Java RMI with TLS/SSL Security
- **数据库**: H2 Embedded Database

### 核心功能
- 员工注册/登录/管理
- 工资单自动计算
- 多用户并发访问
- 安全数据传输

---

## 1. Serialization 实现

### 1.1 RMI中的Serialization
```java
// 所有远程对象必须实现Serializable接口
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String username;
    private String password;
    // ... 其他字段
}
```

### 1.2 为什么需要Serialization？
- **网络传输**: 对象需要在网络上传输
- **RMI要求**: Java RMI要求所有远程对象可序列化
- **数据持久化**: 支持对象存储和恢复

### 1.3 序列化过程
```java
// 客户端发送请求
EmployeeService employeeService = (EmployeeService) registry.lookup("EmployeeService");
Employee result = employeeService.login(username, password); // 自动序列化/反序列化

// 服务端接收并处理
public Employee login(String username, String password) throws RemoteException {
    // 自动反序列化客户端参数
    // 返回结果时自动序列化
}
```

---

## 2. Multithreading 实现

### 2.1 服务端多线程架构
```java
// Server.java - RMI自动处理多线程
Registry registry = LocateRegistry.createRegistry(1099);
registry.rebind("EmployeeService", new EmployeeServiceImpl());
registry.rebind("PayrollService", new PayrollServiceImpl());
```

### 2.2 RMI多线程特性
- **自动线程池**: RMI自动为每个客户端请求创建线程
- **并发处理**: 多个客户端可同时连接
- **线程安全**: 每个请求在独立线程中执行

### 2.3 数据库连接池
```java
// DatabaseConnection.java
public class DatabaseConnection {
    private static final String URL = "jdbc:h2:./payroll;DB_CLOSE_DELAY=-1";
    
    public static Connection getConnection() throws SQLException {
        // 每次请求创建新连接，支持并发访问
        return DriverManager.getConnection(URL, "sa", "sa");
    }
}
```

---

## 3. 关键技术实现

### 3.1 RMI Registry
```java
// 创建安全的RMI注册表
Registry registry = LocateRegistry.createRegistry(
    1099,
    new SslRMIClientSocketFactory(),
    new SslRMIServerSocketFactory()
);
```

### 3.2 远程接口定义
```java
// EmployeeService.java
public interface EmployeeService extends Remote {
    Employee login(String username, String password) throws RemoteException;
    Employee register(String username, String password, String firstName, 
                     String lastName, String icPassport, String role) throws RemoteException;
    // ... 其他方法
}
```

### 3.3 服务实现
```java
// EmployeeServiceImpl.java
public class EmployeeServiceImpl implements EmployeeService {
    @Override
    public Employee login(String username, String password) throws RemoteException {
        // 在独立线程中执行
        try (Connection conn = DatabaseConnection.getConnection()) {
            // 数据库操作
        }
    }
}
```

---

## 4. 并发安全特性

### 4.1 数据库事务
```java
// 使用try-with-resources确保连接正确关闭
try (Connection conn = DatabaseConnection.getConnection();
     PreparedStatement stmt = conn.prepareStatement(sql)) {
    conn.setAutoCommit(false);
    // 执行操作
    conn.commit();
} catch (SQLException e) {
    conn.rollback();
}
```

### 4.2 线程隔离
- 每个RMI请求在独立线程中执行
- 数据库连接独立，避免冲突
- 无共享状态，天然线程安全

---

## 5. 性能优化

### 5.1 连接管理
- 短连接模式：每次请求创建新连接
- 避免连接泄漏
- 支持高并发访问

### 5.2 序列化优化
- 只序列化必要字段
- 使用transient关键字排除敏感数据
- 实现serialVersionUID确保版本兼容

---

## 6. 安全特性

### 6.1 TLS/SSL加密
```java
// 客户端配置
System.setProperty("javax.net.ssl.trustStore", "server.keystore");
System.setProperty("javax.net.ssl.trustStorePassword", "abcd123");

// 服务端配置
System.setProperty("javax.net.ssl.keyStore", "server.keystore");
System.setProperty("javax.net.ssl.keyStorePassword", "abcd123");
```

### 6.2 数据验证
- 客户端输入验证
- 服务端业务逻辑验证
- SQL注入防护

---

## 7. 演示要点

### 7.1 多客户端并发测试
1. 启动服务端
2. 同时运行多个客户端
3. 演示并发登录/注册

### 7.2 网络传输验证
1. 使用Wireshark或类似工具
2. 展示加密的RMI通信
3. 验证序列化数据

### 7.3 数据库并发
1. 同时执行多个数据库操作
2. 展示事务隔离
3. 验证数据一致性

---

## 8. 常见问题解答

### Q: 为什么选择RMI而不是其他技术？
A: RMI提供Java原生支持，自动处理序列化和多线程，开发效率高。

### Q: 如何处理高并发？
A: RMI自动线程池 + 数据库连接池 + 无状态设计。

### Q: 序列化性能如何？
A: Java序列化适合RMI场景，支持对象图完整传输。

### Q: 多线程安全性如何保证？
A: 每个请求独立线程 + 无共享状态 + 数据库事务隔离。

---

## 9. 总结

### 技术亮点
- **自动序列化**: RMI自动处理对象传输
- **内置多线程**: 无需手动管理线程池
- **安全通信**: TLS/SSL加密传输
- **高并发支持**: 天然支持多用户访问

### 应用场景
- 企业内部系统
- 分布式应用
- 需要高安全性的系统
- 多用户并发访问场景

---

## 10. 代码结构

```
PayrollSystem/
├── common/          # 共享接口和模型
├── server/          # RMI服务端实现
├── client/          # Swing客户端
├── h2-2.3.232.jar  # 数据库驱动
└── server.keystore  # SSL证书
```

**核心文件**:
- `Employee.java` - 可序列化员工模型
- `Server.java` - RMI服务端启动
- `EmployeeServiceImpl.java` - 多线程服务实现
- `PayrollClient.java` - 客户端GUI
