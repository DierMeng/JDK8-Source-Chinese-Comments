package learn.leetcode.partition;

/**
 * 实现 pow(x, n) ，即计算 x 的 n 次幂函数。 
 * 
 *  示例 1: 
 * 
 *  输入: 2.00000, 10
 * 输出: 1024.00000
 *  
 * 
 *  示例 2: 
 * 
 *  输入: 2.10000, 3
 * 输出: 9.26100
 *  
 * 
 *  示例 3: 
 * 
 *  输入: 2.00000, -2
 * 输出: 0.25000
 * 解释: 2-2 = 1/22 = 1/4 = 0.25 
 * 
 *  说明: 
 * 
 *  
 *  -100.0 < x < 100.0 
 *  n 是 32 位有符号整数，其数值范围是 [−231, 231 − 1] 。 
 *  
 *  Related Topics 数学 二分查找 
 */
public class Fifty {
    public double myPow(double x, int n) {
        if(x == 0.0d) {
            return 0.0d;
        }
        long b = n;
        double result = 1.0;
        if(b < 0) {
            x = 1 / x;
            b = -b;
        }
        while(b > 0) {
            // n&1 （与操作）： 判断 n 二进制最右一位是否为 1
            if((b & 1) == 1) {
                System.out.println("之前：" + result);
                result = result * x;
                System.out.println("之后：" + result);
            }
            x = x * x;
            System.out.println("中间结果：" + x);
            // n>>1 （移位操作）： n 右移一位（可理解为删除最后一位）。
            b = b >> 1;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(new Fifty().myPow(3, 9));
    }
    
}
