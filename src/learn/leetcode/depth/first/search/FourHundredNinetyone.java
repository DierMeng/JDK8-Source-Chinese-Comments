package learn.leetcode.depth.first.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 给定一个整型数组, 你的任务是找到所有该数组的递增子序列，递增子序列的长度至少是2。 
 * 
 *  示例: 
 * 
 *  
 * 输入: [4, 6, 7, 7]
 * 输出: [[4, 6], [4, 7], [4, 6, 7], [4, 6, 7, 7], [6, 7], [6, 7, 7], [7,7], [4,7,7
 * ]] 
 * 
 *  说明: 
 * 
 *  
 *  给定数组的长度不会超过15。 
 *  数组中的整数范围是 [-100,100]。 
 *  给定数组中可能包含重复数字，相等的数字应该被视为递增的一种情况。 
 *  
 *  Related Topics 深度优先搜索 
 */
public class FourHundredNinetyone {

    // 定义全局变量保存结果
    List<List<Integer>> resultList = new ArrayList<>();

    public List<List<Integer>> findSubsequences(int[] nums) {
        // idx 初始化为 -1，开始 dfs 搜索。
        dfs(nums, -1, new ArrayList<>());
        return resultList;
    }

    /**
     * 定义全局变量 List<List<Integer>> res 保存结果；
     * 定义 dfs(int[] nums, int idx, List<Integer> curList) 进行搜索；
     *  参数 int idx 表示当前搜索到的数组下标，初始化为 -1，每次在 [idx + 1, nums.length - 1] 范围内遍历搜索递增序列的下一个值。
     *  参数 List<Integer> curList 表示当前的递增序列，只要长度大于 1，就拷贝一份存入结果列表 res 中。
     * 每次在 [idx + 1, nums.length - 1] 范围内遍历搜索递增序列的下一个值时，借助 set 进行去重，如果当前的值已经在 set 中了，则终止此路径继续搜索。
     */
    private void dfs(int[] nums, int index, List<Integer> currentList) {
        if (currentList.size() > 1) {
            resultList.add(new ArrayList<>(currentList));
        }
        // 在 [idx + 1, nums.length - 1] 范围内遍历搜索递增序列的下一个值。
        // 借助 set 对 [idx + 1, nums.length - 1] 范围内的数去重。
        Set<Integer> set = new HashSet<>();
        for (int i = index + 1; i < nums.length; i++) {
            // 1. 如果 set 中已经有与 nums[i] 相同的值了，说明加上 nums[i] 后的所有可能的递增序列之前已经被搜过一遍了，因此停止继续搜索。
            if (set.contains(nums[i])) {
                continue;
            }
            set.add(nums[i]);
            // 2. 如果 nums[i] >= nums[idx] 的话，说明出现了新的递增序列，因此继续 dfs 搜索（因为 curList 在这里是复用的，因此别忘了 remove 哦）
            if (index == -1 || nums[i] >= nums[index]) {
                currentList.add(nums[i]);
                dfs(nums, i, currentList);
                currentList.remove(currentList.size() - 1);
            }
        }
    }
}
