public class TestNegativeValidation {
    public static void main(String[] args) {
        // 测试 parseDoubleOrZero 方法的行为
        String[] testValues = {"-100", "100", "0", "-50.5", "abc", ""};
        
        for (String value : testValues) {
            double result = parseDoubleOrZero(value);
            System.out.println("Input: '" + value + "' -> Result: " + result + " (negative: " + (result < 0) + ")");
        }
        
        // 测试负数校验逻辑
        double baseSalary = -100;
        double overtimeHours = 0;
        double overtimeRate = 0;
        double bonus = 0;
        double allowance = 0;
        
        System.out.println("\nTesting negative validation:");
        System.out.println("Base Salary: " + baseSalary + " (negative: " + (baseSalary < 0) + ")");
        System.out.println("Overtime Hours: " + overtimeHours + " (negative: " + (overtimeHours < 0) + ")");
        System.out.println("Overtime Rate: " + overtimeRate + " (negative: " + (overtimeRate < 0) + ")");
        System.out.println("Bonus: " + bonus + " (negative: " + (bonus < 0) + ")");
        System.out.println("Allowance: " + allowance + " (negative: " + (allowance < 0) + ")");
        
        if (baseSalary < 0 || overtimeHours < 0 || overtimeRate < 0 || bonus < 0 || allowance < 0) {
            System.out.println("RESULT: Negative value detected! Should block payroll generation.");
        } else {
            System.out.println("RESULT: All values are non-negative. Payroll generation allowed.");
        }
    }
    
    private static double parseDoubleOrZero(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}

