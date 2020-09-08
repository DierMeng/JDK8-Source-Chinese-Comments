package learn.leetcode.retrospective;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 给定一个数组 candidates 和一个目标数 target ，找出 candidates 中所有可以使数字和为 target 的组合。 
 * 
 *  candidates 中的每个数字在每个组合中只能使用一次。 
 * 
 *  说明： 
 * 
 *  
 *  所有数字（包括目标数）都是正整数。 
 *  解集不能包含重复的组合。 
 *  
 * 
 *  示例 1: 
 * 
 *  输入: candidates =[10,1,2,7,6,1,5], target =8,
 * 所求解集为:
 * [
 *   [1, 7],
 *   [1, 2, 5],
 *   [2, 6],
 *   [1, 1, 6]
 * ]
 *  
 * 
 *  示例 2: 
 * 
 *  输入: candidates =[2,5,2,1,2], target =5,
 * 所求解集为:
 * [
 *  [1,2,2],
 *  [5]
 * ] 
 *  Related Topics 数组 回溯算法 
 */
public class Forty {
    List<List<Integer>> lists = new ArrayList<>();

    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        if (candidates == null || candidates.length == 0 || target < 0) {
            return lists;
        }
        Arrays.sort(candidates);
        List<Integer> list = new ArrayList<>();
        process(0, candidates, target, list);
        return lists;
    }

    private void process(int start, int[] candidates, int target, List<Integer> list) {
        if (target < 0) {
            return;
        }
        if (target == 0) {
            lists.add(new ArrayList<>(list));
        } else {
            for (int i = start; i < candidates.length; i++) {
                // 回溯
                if (i > start && candidates[i] == candidates[i - 1]) {
                    continue;
                }

                list.add(candidates[i]);
                // 递归
                process(i + 1, candidates, target - candidates[i], list);
                list.remove(list.size() - 1);
            }
        }
    }
}
