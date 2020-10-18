package learn.leetcode.dynamic.planning;

/**
 * //给定一个整数数组 nums ，找到一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。 
 * //
 * // 示例: 
 * //
 * // 输入: [-2,1,-3,4,-1,2,1,-5,4]
 * //输出: 6
 * //解释:连续子数组[4,-1,2,1] 的和最大，为6。
 * // 
 * //
 * // 进阶: 
 * //
 * // 如果你已经实现复杂度为 O(n) 的解法，尝试使用更为精妙的分治法求解。 
 * // Related Topics 数组 分治算法 动态规划 
 */
public class FiftyThree {
    public int maxSubArray(int[] nums) {
        int answer = nums[0];
        int sum = 0;
        for (int num : nums) {
            if (sum > 0) {
                sum = sum + num;
            } else {
                sum = num;
            }
            answer = Math.max(answer, sum);
        }
        return answer;

        // return maxSubArrayDivideWithBorder(nums, 0, nums.length-1);
    }

    private int maxSubArrayDivideWithBorder(int[] nums, int start, int end) {
        if (start == end) {
            // 只有一个元素，也就是递归的结束情况
            return nums[start];
        }

        // 计算中间值
        int center = (start + end) / 2;
        int leftMax = maxSubArrayDivideWithBorder(nums, start, center); // 计算左侧子序列最大值
        int rightMax = maxSubArrayDivideWithBorder(nums, center + 1, end); // 计算右侧子序列最大值

        // 下面计算横跨两个子序列的最大值

        // 计算包含左侧子序列最后一个元素的子序列最大值
        int leftCrossMax = Integer.MIN_VALUE; // 初始化一个值
        int leftCrossSum = 0;
        for (int i = center ; i >= start ; i --) {
            leftCrossSum += nums[i];
            leftCrossMax = Math.max(leftCrossSum, leftCrossMax);
        }

        // 计算包含右侧子序列最后一个元素的子序列最大值
        int rightCrossMax = nums[center+1];
        int rightCrossSum = 0;
        for (int i = center + 1; i <= end ; i ++) {
            rightCrossSum += nums[i];
            rightCrossMax = Math.max(rightCrossSum, rightCrossMax);
        }

        // 计算跨中心的子序列的最大值
        int crossMax = leftCrossMax + rightCrossMax;

        // 比较三者，返回最大值
        return Math.max(crossMax, Math.max(leftMax, rightMax));
    }
}
