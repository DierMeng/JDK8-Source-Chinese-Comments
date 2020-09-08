package learn.leetcode.greed;

/**
 * 给定一个非负整数数组，你最初位于数组的第一个位置。 
 * 
 *  数组中的每个元素代表你在该位置可以跳跃的最大长度。 
 * 
 *  你的目标是使用最少的跳跃次数到达数组的最后一个位置。 
 * 
 *  示例: 
 * 
 *  输入: [2,3,1,1,4]
 * 输出: 2
 * 解释: 跳到最后一个位置的最小跳跃数是 2。
 *     从下标为 0 跳到下标为 1 的位置，跳1步，然后跳3步到达数组的最后一个位置。
 *  
 * 
 *  说明: 
 * 
 *  假设你总是可以到达数组的最后一个位置。 
 *  Related Topics 贪心算法 数组 
 */
public class FortyFive {
    public int jump(int[] nums) {
        int length = nums.length;
        int end = 0;
        int maxPosition = 0;
        int steps = 0;
        for (int i = 0; i < length - 1; i++) {
            maxPosition = Math.max(maxPosition, i + nums[i]);
            System.out.println("当前 maxPo 值：" + maxPosition);
            if (i == end) {
                end = maxPosition;
                steps++;
            }
        }
        System.out.println("end 值：" + end + " maxPo 值：" + maxPosition + " steps 值： " + steps);
        return steps;
    }

    public static void main(String[] args) {
        new FortyFive().jump(new int[] {2,3,1,2,4,2,3});
    }
}
