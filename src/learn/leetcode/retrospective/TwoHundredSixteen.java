package learn.leetcode.retrospective;

import java.util.ArrayList;
import java.util.List;

/**
 * 找出所有相加之和为 n 的 k 个数的组合。组合中只允许含有 1 - 9 的正整数，并且每种组合中不存在重复的数字。 
 * 
 *  说明： 
 * 
 *  
 *  所有数字都是正整数。 
 *  解集不能包含重复的组合。 
 *  
 * 
 *  示例 1: 
 * 
 *  输入: k = 3, n = 7
 * 输出: [[1,2,4]]
 *  
 * 
 *  示例 2: 
 * 
 *  输入: k = 3, n = 9
 * 输出: [[1,2,6], [1,3,5], [2,3,4]]
 *  
 *  Related Topics 数组 回溯算法 
 */
public class TwoHundredSixteen {
    public List<List<Integer>> combinationSum3(int k, int n) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        dfs(result, list, k, 1, n);
        return result;
    }

    private void dfs(List<List<Integer>> result, List<Integer> list, int k, int start, int n) {
        if (k == list.size() && n == 0) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = start; i <= 9; i++) {
            list.add(i);
            dfs(result, list, k, i + 1, n - i);
            list.remove(list.size() - 1);
        }
    }
}
