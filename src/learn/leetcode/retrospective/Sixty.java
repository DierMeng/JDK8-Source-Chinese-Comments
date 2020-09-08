package learn.leetcode.retrospective;

import java.util.ArrayList;
import java.util.List;

/**
 * 给出集合 [1,2,3,…,n]，其所有元素共有 n! 种排列。 
 * 
 *  按大小顺序列出所有排列情况，并一一标记，当 n = 3 时, 所有排列如下： 
 * 
 *  
 *  "123" 
 *  "132" 
 *  "213" 
 *  "231" 
 *  "312" 
 *  "321" 
 *  
 * 
 *  给定 n 和 k，返回第 k 个排列。 
 * 
 *  说明： 
 * 
 *  
 *  给定 n 的范围是 [1, 9]。 
 *  给定 k 的范围是[1, n!]。 
 *  
 * 
 *  示例 1: 
 * 
 *  输入: n = 3, k = 3
 * 输出: "213"
 *  
 * 
 *  示例 2: 
 * 
 *  输入: n = 4, k = 9
 * 输出: "2314"
 *  
 *  Related Topics 数学 回溯算法 
 */
public class Sixty {
    public String getPermutation(int n, int k) {
        // 生成 nums 数组
        int nums[] = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = i + 1;
        }
        // 记录当前的索引的数是否被使用过
        boolean[] used = new boolean[n];
        return dfs(nums, new ArrayList<String>(), used, 0, n, k);
    }

    /**
     * 深度优先搜索
     * @Title: dfs
     * @param nums 源数组
     * @param levelList 每一层的选择
     * @param used 数组的元素是否被使用过
     * @param depth 深度，也就是当前使用的元素的索引
     * @param n 上限值，数组的长度
     * @param k 第 k 个
     * @return java.lang.String
     */
    private String dfs(int[] nums, List<String> levelList, boolean[] used, int depth, int n, int k) {

        // 触发出口条件，开始收集结果集
        if (depth == n) {
            StringBuilder result = new StringBuilder();
            for (String str : levelList) {
                result.append(str);
            }
            return result.toString();
        }
        // 当前的 depth 也就是索引，有多少排列数
        int current = factorial(n - depth - 1);
        for (int i = 0; i < n; i++) {
            // 当前元素被使用过，做剪枝
            if (used[i]) {
                continue;
            }
            // 当前的排列组合数小于 k，说明就算这一层排完了，也到不了第k个，剪枝
            if (current < k) {
                k = k - current;
                continue;
            }
            levelList.add(nums[i] + "");
            used[i] = true;
            return dfs(nums, levelList, used, depth + 1, n, k);
        }
        return null;
    }

    /**
     * 返回一个整数的阶乘
     */
    private int factorial(int n) {
        int result = 1;
        while (n > 0) {
            result = result * (n--);
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("fgdsfdgdfgdfg");
        Sixty sixty = new Sixty();
        String res = sixty.getPermutation(4, 9);
        System.out.println(res);
    }
}
