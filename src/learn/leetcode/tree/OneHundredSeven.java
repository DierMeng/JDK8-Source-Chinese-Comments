package learn.leetcode.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 给定一个二叉树，返回其节点值自底向上的层次遍历。 （即按从叶子节点所在层到根节点所在的层，逐层从左向右遍历） 
 * 
 *  例如： 
 * 给定二叉树 [3,9,20,null,null,15,7], 
 * 
 *      3
 *    / \
 *   9  20
 *     /  \
 *    15   7
 *  
 * 
 *  返回其自底向上的层次遍历为： 
 * 
 *  [
 *   [15,7],
 *   [9,20],
 *   [3]
 * ]
 *  
 *  Related Topics 树 广度优先搜索 
 */
public class OneHundredSeven {

    public List<List<Integer>> levelOrderBottom(TreeNode root) {
        // 返回结果
        List<List<Integer>> levelrder = new LinkedList<>();
        if (null == root) {
            return levelrder;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            List<Integer> level = new ArrayList<>();
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                TreeNode left = node.left;
                TreeNode right = node.right;
                if (null != left) {
                    queue.offer(left);
                }
                if (null != right) {
                    queue.offer(right);
                }
            }
            levelrder.add(0, level);
        }
        return levelrder;
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
