package learn.leetcode.dynamic.planning;

/**
 * 给定一个字符串 s ，找到其中最长的回文子序列，并返回该序列的长度。可以假设 s 的最大长度为 1000 。
 *
 *
 *
 *  示例 1:
 * 输入:
 *
 *  "bbbab"
 *
 *
 *  输出:
 *
 *  4
 *
 *
 *  一个可能的最长回文子序列为 "bbbb"。
 *
 *  示例 2:
 * 输入:
 *
 *  "cbbd"
 *
 *
 *  输出:
 *
 *  2
 *
 *
 *  一个可能的最长回文子序列为 "bb"。
 *
 *
 *
 *  提示：
 *
 *
 *  1 <= s.length <= 1000
 *  s 只包含小写英文字母
 *
 *  Related Topics 动态规划
 */
public class FiveHundredSixteen {
    public int longestPalindromeSubseq(String s) {
        int length = s.length();
        int dp[][] = new int[length][length];
        // base case
        for (int i = 0; i < length; i++) {
            dp[i][i] = 1;
        }
        // 状态转移
        for (int i = length -1; i >= 0; i--) {
           for (int j = i + 1; j < length; j++) {
               if (s.charAt(i) == s.charAt(j)) {
                   dp[i][j] = dp[i + 1][j - 1] + 2;
               } else {
                   dp[i][j] = Math.max(dp[i+1][j], dp[i][j-1]);
               }
           }
        }
        return dp[0][length - 1];
    }











}
