package learn.leetcode.string;

/**
 * 给出一个 32 位的有符号整数，你需要将这个整数中每位上的数字进行反转。 
 * 
 *  示例 1: 
 * 
 *  输入: 123
 * 输出: 321
 *  
 * 
 *  示例 2: 
 * 
 *  输入: -123
 * 输出: -321
 *  
 * 
 *  示例 3: 
 * 
 *  输入: 120
 * 输出: 21
 *  
 * 
 *  注意: 
 * 
 *  假设我们的环境只能存储得下 32 位的有符号整数，则其数值范围为 [−231, 231 − 1]。请根据这个假设，如果反转后整数溢出那么就返回 0。 
 *  Related Topics 数学 
 */
public class Seven {
    public int reverse(int x) {
        int result = 0;
        while (x != 0) {
            // 取最后一位数
            int t = x % 10;
            int newResult = result * 10 + t;
            // 如果溢出，直接返回 0
            if ((newResult - t) / 10 != result) {
                return 0;
            }
            result = newResult;
            // 去掉最后一位数
            x = x / 10;
        }
        return result;
    }
}
