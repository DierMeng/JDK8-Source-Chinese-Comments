package learn.leetcode.tree;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * 给定一个二叉树，返回它的中序 遍历。 
 * 
 *  示例: 
 * 
 *  输入: [1,null,2,3]
 *    1
 *     \
 *      2
 *     /
 *    3
 * 
 * 输出: [1,3,2] 
 * 
 *  进阶: 递归算法很简单，你可以通过迭代算法完成吗？ 
 *  Related Topics 栈 树 哈希表 
 */
public class NinetyFour {

    /**
     * 递归实现中序遍历
     */
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inorder(root, result);
        return result;
    }

    /**
     * 迭代式隐式栈
     */
    public List<Integer> inorderTraversal2(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Deque<TreeNode> stack = new LinkedList<>();
        while (null != root && !stack.isEmpty()) {
            while (null != root) {
                stack.push(root);
                root = root.left;
            }
            root = stack.pop();
            result.add(root.val);
            root = root.right;
        }
        return result;
    }

    private void inorder(TreeNode root, List<Integer> result) {
        if (null == root) {
            return;
        }
        inorder(root.left, result);
        result.add(root.val);
        inorder(root.right, result);
    }

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }
}
