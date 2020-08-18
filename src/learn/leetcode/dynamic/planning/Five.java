package learn.leetcode.dynamic.planning;

/**
 * 给定一个字符串 s，找到 s 中最长的回文子串。你可以假设 s 的最大长度为 1000。 
 * 
 *  示例 1： 
 * 
 *  输入: "babad"
 * 输出: "bab"
 * 注意: "aba" 也是一个有效答案。
 *  
 * 
 *  示例 2： 
 * 
 *  输入: "cbbd"
 * 输出: "bb"
 *  
 *  Related Topics 字符串 动态规划 
 */
public class Five {
    public String longestPalindrome(String s) {
        int length = s.length();
        // 特判
        if (length < 2) {
            return s;
        }
        int maxLength = 1;
        int start = 0;
        // 1. 状态定义
        // dp[i][j] 表示s[i...j] 是否是回文串
        // 2. 初始化
        Boolean[][] dp = new Boolean[length][length];
        for (int i = 0; i < length; i++) {
            dp[i][i] = true;
        }
        char[] chars = s.toCharArray();
        // 3. 状态转移
        // 注意：先填左下角
        // 填表规则：先一列一列的填写，再一行一行的填，保证左下方的单元格先进行计算
        for (int j = 1; j < length; j++) {
            for (int i = 0; i < j; i++) {
                // 头尾字符不相等，不是回文串
                if (chars[i] != chars[j]) {
                    dp[i][j] = false;
                } else {
                    // 相等的情况下
                    // 考虑头尾去掉以后没有字符剩余，或者剩下一个字符的时候，肯定是回文串
                    if (j - i < 3) {
                        dp[i][j] = true;
                    } else {
                        // 状态转移
                        dp[i][j] = dp[i+1][j - 1];
                    }
                }
                // 只要dp[i][j] == true 成立，表示s[i...j] 是否是回文串
                // 此时更新记录回文长度和起始位置
                if (dp[i][j] && j - i + 1 > maxLength) {
                    maxLength = j - i + 1;
                    start = i;
                }
            }
        }
        // 4. 返回值
        return s.substring(start, start + maxLength);
    }
}
