# 数据库重置指南 - 解决工资计算不一致问题

## 问题分析
由于我们修改了Payroll表的结构（添加了overtimeHours、overtimeRate、bonus字段），但旧的数据库文件可能仍然使用旧的表结构，这会导致数据不一致。

## 解决方案

### 方法1: 删除旧数据库文件（推荐）
1. 停止服务端
2. 删除以下文件：
   ```
   payroll.mv.db
   payroll.trace.db
   ```
3. 重新启动服务端，系统会自动创建新的表结构

### 方法2: 手动更新数据库表结构
如果不想删除数据，可以手动执行SQL：

```sql
-- 连接到H2数据库
-- JDBC URL: jdbc:h2:./payroll
-- 用户名: sa
-- 密码: sa

-- 删除旧表
DROP TABLE IF EXISTS Payroll;

-- 创建新表
CREATE TABLE Payroll (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employeeId INT,
    period VARCHAR(255),
    baseSalary DOUBLE,
    overtimeHours DOUBLE,
    overtimeRate DOUBLE,
    bonus DOUBLE,
    allowance DOUBLE,
    grossPay DOUBLE,
    deductions DOUBLE,
    netPay DOUBLE,
    FOREIGN KEY (employeeId) REFERENCES Employee(id)
);
```

## 重置步骤

### 步骤1: 停止服务端
```bash
# 在服务端窗口按 Ctrl+C 停止
```

### 步骤2: 删除数据库文件
```bash
# 在项目根目录执行
rm payroll.mv.db
rm payroll.trace.db
```

### 步骤3: 重新编译项目
```bash
javac -encoding UTF-8 common/*.java
javac -encoding UTF-8 -cp ".;common;h2-2.3.232.jar" server/*.java
javac -encoding UTF-8 -cp ".;common" client/*.java
```

### 步骤4: 重新启动服务端
```bash
java -cp ".;common;server;h2-2.3.232.jar" server.Server
```

### 步骤5: 重新启动客户端
```bash
java -cp ".;common;client" client.PayrollClient
```

## 验证修复

1. 使用管理员账号登录 (admin1/admin123)
2. 进入 "Payroll Mana" 功能
3. 输入测试数据：
   - Base Salary: 1
   - Overtime Hours: 1
   - Overtime Rate: 1
   - Bonus: 1
   - Allowance: 1
4. 验证计算结果：
   - Gross Pay: 4.00
   - Net Pay (after EPF): 3.56
5. 点击 "Send Payroll"
6. 确认Message窗口也显示 "Net Pay: 3.56"

## 预期结果

修复后，两个窗口应该显示完全一致的Net Pay值：
- Admin Payroll Generation窗口: Net Pay (after EPF): 3.56
- Message窗口: Net Pay: 3.56

## 注意事项

- 删除数据库文件会丢失所有现有数据
- 如果需要保留数据，请使用手动更新表结构的方法
- 确保在重置数据库后重新添加测试用户
- 检查控制台输出，确认服务端正确创建了新表结构

