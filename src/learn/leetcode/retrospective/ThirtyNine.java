package learn.leetcode.retrospective;

import java.util.*;

/**
 * 给定一个无重复元素的数组 candidates 和一个目标数 target ，找出 candidates 中所有可以使数字和为 target 的组合。 
 * 
 *  candidates 中的数字可以无限制重复被选取。 
 * 
 *  说明： 
 * 
 *  
 *  所有数字（包括 target）都是正整数。 
 *  解集不能包含重复的组合。 
 *  
 * 
 *  示例 1： 
 * 
 *  输入：candidates = [2,3,6,7], target = 7,
 * 所求解集为：
 * [
 *   [7],
 *   [2,2,3]
 * ]
 *  
 * 
 *  示例 2： 
 * 
 *  输入：candidates = [2,3,5], target = 8,
 * 所求解集为：
 * [
 *  [2,2,2,2],
 *  [2,3,3],
 *  [3,5]
 * ] 
 * 
 *  
 * 
 *  提示： 
 * 
 *  
 *  1 <= candidates.length <= 30 
 *  1 <= candidates[i] <= 200 
 *  candidate 中的每个元素都是独一无二的。 
 *  1 <= target <= 500 
 *  
 *  Related Topics 数组 回溯算法 
 */
public class ThirtyNine {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        int length = candidates.length;
        List<List<Integer>> result = new ArrayList<>();
        if (length == 0) {
            return result;
        }

        Arrays.sort(candidates);
        Deque<Integer> path = new ArrayDeque<>();
        dfs(candidates, 0, length, target, path, result);
        return result;
    }

    private void dfs(int[] candidates, int begin, int length, int target, Deque<Integer> path, List<List<Integer>> result) {
        if (target == 0) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = begin; i < length; i++) {
            if (target - candidates[i] < 0) {
                break;
            }

            path.addLast(candidates[i]);
            System.out.println("递归之前 => " + path + "，剩余 = " + (target - candidates[i]));

            dfs(candidates, i, length, target - candidates[i], path, result);
            path.removeLast();
            System.out.println("递归之后 => " + path);
        }
    }

    public static void main(String[] args) {
        ThirtyNine solution = new ThirtyNine();
        int[] candidates = new int[]{2, 3, 6, 7};
        int target = 7;
        List<List<Integer>> res = solution.combinationSum(candidates, target);
        System.out.println("输出 => " + res);
    }
}
