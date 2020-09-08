package learn.leetcode.binary.search;

/**
 * 给定一个按照升序排列的整数数组 nums，和一个目标值 target。找出给定目标值在数组中的开始位置和结束位置。 
 * 
 *  你的算法时间复杂度必须是 O(log n) 级别。 
 * 
 *  如果数组中不存在目标值，返回 [-1, -1]。 
 * 
 *  示例 1: 
 * 
 *  输入: nums = [5,7,7,8,8,10], target = 8
 * 输出: [3,4] 
 * 
 *  示例 2: 
 * 
 *  输入: nums = [5,7,7,8,8,10], target = 6
 * 输出: [-1,-1] 
 *  Related Topics 数组 二分查找 
 */
public class ThirtyFour {

    public int[] searchRange(int[] nums, int target) {
        int res1 = leftBound(nums, target);
        int res2 = rightBound(nums,target);
        return new int[]{res1, res2};
    }

    private int leftBound(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] < target) {
                left = mid + 1;
            } else if (nums[mid] > target) {
                right = mid - 1;
            } else if (nums[mid] == target) {
                // 别返回，锁定左侧边界
                right = mid - 1;
            }
        }
        // 最后要检查 left 越界的情况
        if (left >= nums.length || nums[left] != target)
            return -1;
        return left;
    }

    private int rightBound(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] < target) {
                left = mid + 1;
            } else if (nums[mid] > target) {
                right = mid - 1;
            } else if (nums[mid] == target) {
                // 别返回，锁定右侧边界
                left = mid + 1;
            }
        }
        // 最后要检查 right 越界的情况
        if (right < 0 || nums[right] != target)
            return -1;
        return right;
    }

}
