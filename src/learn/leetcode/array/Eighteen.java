package learn.leetcode.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 给定一个包含 n 个整数的数组 nums 和一个目标值 target，判断 nums 中是否存在四个元素 a，b，c 和 d ，使得 a + b + c +
 *  d 的值与 target 相等？找出所有满足条件且不重复的四元组。 
 * 
 *  注意： 
 * 
 *  答案中不可以包含重复的四元组。 
 * 
 *  示例： 
 * 
 *  给定数组 nums = [1, 0, -1, 0, -2, 2]，和 target = 0。
 * 
 * 满足要求的四元组集合为：
 * [
 *   [-1,  0, 0, 1],
 *   [-2, -1, 1, 2],
 *   [-2,  0, 0, 2]
 * ]
 *  
 *  Related Topics 数组 哈希表 双指针 
 */
public class Eighteen {
    public List<List<Integer>> fourSum(int[] nums, int target) {
        int len = nums.length;
        List<List<Integer>> ans = new ArrayList<>();
        if (len < 4) {
            return ans;
        }
        // 对数组排序，方便遍历和去重。
        Arrays.sort(nums);
        // 当目标值比数组最小四数和还小或者比数组最大四数和还大，表示没有符合条件的四个数。
        if (target < nums[0] + nums[1] + nums[2] + nums[3]
                || target > nums[len - 1] + nums[len - 2] + nums[len - 3] + nums[len - 4]) {
            return ans;
        }
        // 循环固定第一个数，然后循环遍历选择另外三个数。
        for (int i = 0; i < len - 3; i++) {
            // 相同的第一个数只固定一次，避免重复运算。
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            // 如果 target 小于当前循环能得到的最小和，则跳出循环。
            if (target < nums[i] + nums[i + 1] + nums[i + 2] + nums[i + 3]) {
                break;
            }
            // 如果 target 大于此轮循环的最大和，则继续循环下一个比较大的第一位数。
            if (target > nums[i] + nums[len - 1] + nums[len - 2] + nums[len - 3]) {
                continue;
            }
            // 循环固定第一个数，根据双指针从它之后去选择另外两个数。
            for (int j = i + 1; j < len - 2; j++) {
                // 相同的第一个数只固定一次，避免重复运算。
                if (j > i + 1 && nums[j] == nums[j - 1]) {
                    continue;
                }
                // 如果 target 小于当前循环能得到的最小四数和，则跳出循环。
                if (target < nums[i] + nums[j] + nums[j + 1] + nums[j + 2]) {
                    break;
                }
                // 如果 target 大于此轮循环的最大和，则继续循环下一个比较大的第二位数。
                if (target > nums[i] + nums[j] + nums[len - 1] + nums[len - 2]) {
                    continue;
                }
                int L = j + 1, R = len - 1;
                while (L < R) {
                    int sum = nums[i] + nums[j] + nums[L] + nums[R];
                    // 根据四数之和与 target 大小的比较来移动两指针。
                    if (sum == target) {
                        ans.add(Arrays.asList(nums[i], nums[j], nums[L], nums[R]));
                        L++;
                        while (L < R && nums[L - 1] == nums[L]) {
                            L++;
                        }
                        R--;
                        while (L < R && nums[R + 1] == nums[R]) {
                            R--;
                        }
                    } else if (sum > target) {
                        R--;
                        while (L < R && nums[R + 1] == nums[R]) {
                            R--;
                        }
                    } else {
                        L++;
                        while (L < R && nums[L - 1] == nums[L]) {
                            L++;
                        }
                    }
                }
            }
        }
        return ans;
    }
}
