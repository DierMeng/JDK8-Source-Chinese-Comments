package learn.leetcode.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 给定一个非空二叉树, 返回一个由每层节点平均值组成的数组。 
 * 
 *  
 * 
 *  示例 1： 
 * 
 *  输入：
 *     3
 *    / \
 *   9  20
 *     /  \
 *    15   7
 * 输出：[3, 14.5, 11]
 * 解释：
 * 第 0 层的平均值是 3 ,  第1层是 14.5 , 第2层是 11 。因此返回 [3, 14.5, 11] 。
 *  
 * 
 *  
 * 
 *  提示： 
 * 
 *  
 *  节点值的范围在32位有符号整数范围内。 
 *  
 *  Related Topics 树 
 */
public class SixHundredThirySeven {

    public List<Double> averageOfLevels(TreeNode root) {
        List<Double> result = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            double sum = 0;
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode treeNode = queue.poll();
                sum = sum + treeNode.val;
                TreeNode left = treeNode.left;
                TreeNode right = treeNode.right;
                if (null != left) {
                    queue.offer(left);
                }
                if (null !=right) {
                    queue.offer(right);
                }
            }
            result.add(sum / size);
        }
        return result;
    }

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) {
            val = x;
        }
    }
}
