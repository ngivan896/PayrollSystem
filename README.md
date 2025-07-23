# Distributed Payroll System (Java RMI + H2)

## 环境要求
- JDK 11 或以上
- H2 数据库 JAR（如 h2-2.3.232.jar）
- Windows 命令行或 PowerShell

## 目录结构
```
Assignment Coding/
  common/    # 公共接口和模型类
  server/    # 服务端实现
  client/    # 客户端（含GUI）
  h2-2.3.232.jar  # H2数据库驱动
```

## 编译步骤
在项目根目录下依次执行：

```sh
javac -encoding UTF-8 common/*.java
javac -encoding UTF-8 -cp ".;common;h2-2.3.232.jar" server/*.java
javac -encoding UTF-8 -cp ".;common" client/*.java
```

## 运行服务端
```sh
java -cp ".;common;server;h2-2.3.232.jar" server.Server
```
- 保持窗口打开，显示 `Server started. RMI services bound.`

## 运行客户端
新开命令行窗口：
```sh
java -cp ".;common;client" client.PayrollClient
```
- 会弹出登录/注册界面

## 数据库说明
- 默认使用 H2 嵌入式数据库，文件为 `payroll.mv.db`，在项目根目录下生成
- 可用 H2 Console 查看数据：
  - 运行 `java -jar h2-2.3.232.jar`
  - JDBC URL: `jdbc:h2:./payroll` 用户名/密码: `sa`/`sa`

## 管理员账号
- 可用 H2 Console 添加 admin 用户：
  ```sql
  INSERT INTO Employee (username, password, firstName, lastName, icPassport, role)
  VALUES ('admin1', 'admin123', 'Admin', 'User', 'A00001', 'admin');
  ```
- 登录 admin1/admin123 可体验管理员功能

## 常见问题
- **端口占用**：如服务端启动报1099端口被占用，查找并结束占用进程：
  ```sh
  netstat -ano | findstr 1099
  taskkill /PID <PID> /F
  ```
- **数据库锁定**：不要同时用 H2 Console 和服务端访问数据库，避免文件锁冲突。
- **class文件冲突**：如RMI方法未被调用，务必清理所有class文件并重新编译。

## 功能说明
- 支持员工注册、登录、个人信息修改、工资单自动生成与展示
- 管理员可管理员工、生成工资单、查看报表
- 工资单自动计算支持自定义gross pay，系统自动扣除11% EPF

---
如有问题请联系开发者或查阅代码注释。 