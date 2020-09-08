package learn.leetcode.retrospective;

import java.util.*;

/**
 * 给定一个可包含重复数字的序列，返回所有不重复的全排列。 
 * 
 *  示例: 
 * 
 *  输入: [1,1,2]
 * 输出:
 * [
 *   [1,1,2],
 *   [1,2,1],
 *   [2,1,1]
 * ] 
 *  Related Topics 回溯算法 
 */
public class FortySeven {
    public List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if (0 == nums.length) {
            return result;
        }
        Arrays.sort(nums);
        boolean[] used = new boolean[nums.length];
        Deque<Integer> path = new ArrayDeque<>(nums.length);
        dfs(nums, nums.length, 0, path, used, result);
        return result;
    }

    private void dfs(int[] nums, int length, int depth, Deque<Integer> path, boolean[] used, List<List<Integer>> result) {
        if (depth == length) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = 0; i < length; i++) {
            // 剪枝条件：i > 0 是为了保证 nums[i - 1] 有意义
            // 写 !used[i - 1] 是因为 nums[i - 1] 在深度优先遍历的过程中刚刚被撤销选择
            if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) {
                continue;
            }
            if (!used[i]) {
                path.addLast(nums[i]);
                used[i] = true;
                System.out.println("递归之前 => " + path);
                dfs(nums, length, depth + 1, path, used, result);
                used[i] = false;
                path.removeLast();
                System.out.println("递归之后 => " + path);
            }
        }
    }
}
