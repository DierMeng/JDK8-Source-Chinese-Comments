package learn.leetcode.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 给你一个二叉树，请你返回其按 层序遍历 得到的节点值。 （即逐层地，从左到右访问所有节点）。 
 * 
 *  
 * 
 *  示例： 
 * 二叉树：[3,9,20,null,null,15,7], 
 * 
 *      3
 *    / \
 *   9  20
 *     /  \
 *    15   7
 *  
 * 
 *  返回其层次遍历结果： 
 * 
 *  [
 *   [3],
 *   [9,20],
 *   [15,7]
 * ]
 *  
 *  Related Topics 树 广度优先搜索 
 */
public class OneHundredTwo {

    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> res = new ArrayList<>();
        Queue<TreeNode> q = new LinkedList<>();
        q.offer(root);
        while (!q.isEmpty()) {
            int size = q.size();
            List<Integer> level = new LinkedList<>();
            for (int i = 0; i < size; ++i) {
                TreeNode cur = q.poll();
                if (cur == null) {
                    continue;
                }
                level.add(cur.val);
                q.offer(cur.left);
                q.offer(cur.right);
            }
            if (!level.isEmpty()) {
                res.add(level);
            }
        }
        return res;
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
