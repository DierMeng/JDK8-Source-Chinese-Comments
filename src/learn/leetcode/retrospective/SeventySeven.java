package learn.leetcode.retrospective;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * 给定两个整数 n 和 k，返回 1 ... n 中所有可能的 k 个数的组合。 
 * 
 *  示例: 
 * 
 *  输入:n = 4, k = 2
 * 输出:
 * [
 *   [2,4],
 *   [3,4],
 *   [2,3],
 *   [1,2],
 *   [1,3],
 *   [1,4],
 * ] 
 *  Related Topics 回溯算法 
 */
public class SeventySeven {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        if (k <= 0 || n < k) {
            return result;
        }
        Deque<Integer> path = new ArrayDeque<>();
        dfs(n, k, 1, result, path);
        return result;
    }

    private void dfs(int n, int k, int begin, List<List<Integer>> result, Deque<Integer> path) {
        if (k == path.size()) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = begin; i <= n - (k - path.size()) + 1; i++) {
            path.addLast(i);
            dfs(n, k, i + 1, result, path);
            path.removeLast();
        }
    }
}
