package learn.leetcode.dynamic.planning;

/**
 * 给定一个由正整数组成且不存在重复数字的数组，找出和为给定目标正整数的组合的个数。 
 * 
 *  示例: 
 * 
 *  
 * nums = [1, 2, 3]
 * target = 4
 * 
 * 所有可能的组合为：
 * (1, 1, 1, 1)
 * (1, 1, 2)
 * (1, 2, 1)
 * (1, 3)
 * (2, 1, 1)
 * (2, 2)
 * (3, 1)
 * 
 * 请注意，顺序不同的序列被视作不同的组合。
 * 
 * 因此输出为 7。
 *  
 * 
 *  进阶： 
 * 如果给定的数组中含有负数会怎么样？ 
 * 问题会产生什么变化？ 
 * 我们需要在题目中添加什么限制来允许负数的出现？ 
 * 
 *  致谢： 
 * 特别感谢 @pbrother 添加此问题并创建所有测试用例。 
 *  Related Topics 动态规划 
 */
public class ThreeHundredSeventySeven {

    /**
     * 这里状态定义就是题目要求的，并不难，状态转移方程要动点脑子，也不难：
     * 状态转移方程：dp[i]= dp[i - nums[0]] + dp[i - nums[1]] + dp[i - nums[2]] + ... （当 [] 里面的数 >= 0）
     * 特别注意：dp[0] = 1，表示，如果那个硬币的面值刚刚好等于需要凑出的价值，这个就成为 1 种组合方案
     * 再举一个具体的例子：nums=[1, 3, 4], target=7;
     * dp[7] = dp[6] + dp[4] + dp[3]
     * 即：7 的组合数可以由三部分组成，1 和 dp[6]，3 和 dp[4], 4 和dp[3];
     *
     * 作者：liweiwei1419
     * 链接：https://leetcode-cn.com/problems/combination-sum-iv/solution/dong-tai-gui-hua-python-dai-ma-by-liweiwei1419/
     * 来源：力扣（LeetCode）
     * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     */
    public int combinationSum4(int[] nums, int target) {
        int[] dp = new int[target + 1];
        // 这个值被其它状态参考，设置为 1 是合理的
        dp[0] = 1;
        for (int i = 1; i <= target; i++) {
            for (int num : nums) {
                if (num <= i) {
                    dp[i] = dp[i] + dp[i - num];
                }
            }
        }
        return dp[target];
    }
}
