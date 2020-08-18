package learn.leetcode.dynamic.planning;

import java.util.Arrays;

/**
 * 给定一个无序的整数数组，找到其中最长上升子序列的长度。
 *
 *  示例:
 *
 *  输入: [10,9,2,5,3,7,101,18]
 * 输出: 4
 * 解释: 最长的上升子序列是 [2,3,7,101]，它的长度是 4。
 *
 *  说明:
 *
 *
 *  可能会有多种最长上升子序列的组合，你只需要输出对应的长度即可。
 *  你算法的时间复杂度应该为 O(n2) 。
 *
 *
 *  进阶: 你能将算法的时间复杂度降低到 O(n log n) 吗?
 *  Related Topics 二分查找 动态规划
 *
 */
public class ThreeHundred {

    /**
     * 动态规划解法
     *
     * 时间复杂度 O(N^2)
     */
    public int lengthOfLIS(int[] nums) {
        // dp[i] 表⽰以 nums[i] 这个数结尾的最⻓递增⼦序列的⻓度。
        int[] dp = new int[nums.length];
        // base case: dp 数组全都初始化为 1
        // dp 数组应该全部初始化为 1，因为⼦序列最少也要包含⾃⼰，所以⻓度最⼩为 1
        Arrays.fill(dp, 1);
        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[i] > nums[j])
                    dp[i] = Math.max(dp[i], dp[j] + 1);
            }
        }

        int res = 0;
        for (int i = 0; i < dp.length; i++) {
            res = Math.max(res, dp[i]);
        }
        return res;
    }

    /**
     * 二分查找
     */
    public int binaryLengthOfLIS(int[] nums) {
        int[] top = new int[nums.length];
        // 牌堆数初始化为 0
        int piles = 0;
        for (int i = 0; i < nums.length; i++) {
            // 要处理的扑克牌
            int poker = nums[i];

            int left = 0;
            int right = piles;
            while (left < right) {
                int mid = (left + right) / 2;
                if (top[mid] > poker) {
                    right = mid;
                } else if (top[mid] < poker) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }
            // 没找到合适的牌堆，新建一堆
            if (left == piles) {
                piles++;
            }
            // 把这张牌放到牌堆顶
            top[left] = poker;
        }
        // 牌堆数就是 LIS 长度
        return piles;
    }
}
