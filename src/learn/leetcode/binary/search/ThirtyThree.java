package learn.leetcode.binary.search;

/**
 * 假设按照升序排序的数组在预先未知的某个点上进行了旋转。 
 * 
 *  ( 例如，数组 [0,1,2,4,5,6,7] 可能变为 [4,5,6,7,0,1,2] )。 
 * 
 *  搜索一个给定的目标值，如果数组中存在这个目标值，则返回它的索引，否则返回 -1 。 
 * 
 *  你可以假设数组中不存在重复的元素。 
 * 
 *  你的算法时间复杂度必须是 O(log n) 级别。 
 * 
 *  示例 1: 
 * 
 *  输入: nums = [4,5,6,7,0,1,2], target = 0
 * 输出: 4
 *  
 * 
 *  示例 2: 
 * 
 *  输入: nums = [4,5,6,7,0,1,2], target = 3
 * 输出: -1 
 *  Related Topics 数组 二分查找 
 */
public class ThirtyThree {
    public int search(int[] nums, int target) {
        int low = 0;
        int high = nums.length - 1;
        while (low <= high) {
            int middle = low + (high - low) / 2;
            if (nums[middle] == target) {
                return middle;
            }
            // 先根据 nums[mid] 与 nums[lo] 的关系判断 mid 是在左段还是右段
            // 中间的数大于第一个数，说明中间数在左边段升序
            if (nums[middle] >= nums[low]) {
                // 如果目标数在左边升序段中
                if (target >= nums[low] && target <= nums[middle]) {
                    high = middle - 1;
                } else {
                    low = middle + 1;
                }
            } else {
                 if (target > nums[middle] && target <= nums[high]) {
                     low = middle + 1;
                 } else {
                     high = middle - 1;
                 }
            }
        }
        return -1;
    }
}
