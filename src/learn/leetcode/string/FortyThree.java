package learn.leetcode.string;

/**
 * 给定两个以字符串形式表示的非负整数 num1 和 num2，返回 num1 和 num2 的乘积，它们的乘积也表示为字符串形式。 
 * 
 *  示例 1: 
 * 
 *  输入: num1 = "2", num2 = "3"
 * 输出: "6" 
 * 
 *  示例 2: 
 * 
 *  输入: num1 = "123", num2 = "456"
 * 输出: "56088" 
 * 
 *  说明： 
 * 
 *  
 *  num1 和 num2 的长度小于110。 
 *  num1 和 num2 只包含数字 0-9。 
 *  num1 和 num2 均不以零开头，除非是数字 0 本身。 
 *  不能使用任何标准库的大数类型（比如 BigInteger）或直接将输入转换为整数来处理。 
 *  
 *  Related Topics 数学 字符串 
 */
public class FortyThree {
    public String multiply(String num1, String num2) {
        if (num1.equals("0") || num2.equals("0")) {
            return "0";
        }
        // 保存计算结果
        String result = "0";
        // num2 逐位与 num1 相乘
        for (int i = num2.length() - 1; i >= 0; i--) {
            int carry = 0;
            // 保存 num2 第 i 位数字与 num1 相乘的结果
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < num2.length() - 1 - i; j++) {
                sb.append("0");
            }
            int n2 = num2.charAt(i) - '0';
            // num2 的第 i 位数字 n2 与 num1 相乘
            for (int j = num1.length() - 1; j >= 0 || carry != 0; j--) {
                int n1 = j < 0 ? 0 : num1.charAt(j) - '0';
                int product = (n1 * n2 + carry) % 10;
                sb.append(product);
                carry = (n1 * n2 + carry) / 10;
            }
            // 将当前结果与新计算的结果求和作为新的结果
            result = addStrings(result, sb.reverse().toString());
        }
        return result;
    }

    private String addStrings(String num1, String num2) {
        StringBuilder builder = new StringBuilder();
        int carry = 0;
        for (int i = num1.length() - 1, j = num2.length() - 1; i >= 0 || j >= 0 || carry != 0; i--, j--) {
            int x = i < 0 ? 0 : num1.charAt(i) - '0';
            int y = j < 0 ? 0 : num2.charAt(j) - '0';
            int sum = (x + y + carry) % 10;
            builder.append(sum);
            carry = (x + y + carry) / 10;
        }
        return builder.reverse().toString();
    }

    public static void main(String[] args) {
        System.out.println(new FortyThree().multiply("123", "456"));
    }
}
