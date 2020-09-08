package learn.leetcode.retrospective;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * 给定一个 没有重复 数字的序列，返回其所有可能的全排列。 
 * 
 *  示例: 
 * 
 *  输入: [1,2,3]
 * 输出:
 * [
 *   [1,2,3],
 *   [1,3,2],
 *   [2,1,3],
 *   [2,3,1],
 *   [3,1,2],
 *   [3,2,1]
 * ] 
 *  Related Topics 回溯算法 
 */
public class FortySix {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if (0 == nums.length) {
            return result;
        }
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
