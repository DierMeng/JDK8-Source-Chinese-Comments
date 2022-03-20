package learn.basic.data.type;

import java.util.Stack;

/**
 * 二叉树的前序遍历
 *
 * 根 左 右
 *
 * 可以使用递归操作
 * 可以迭代
 *
 * @ClassName: Test
 * @author: Glorze
 * @since: 2020/10/29 20:33
 */
public class Test {

    public void preOrder(TreeNode root) {
        if (null == root) {
            return;
        }
        // Stack 描述这个过程 根 左 右
        // 入栈的话 根 右 左
        // 判断当前栈是否为空 进行出栈，出栈先放右节点 再放左
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode tr = stack.pop();
            System.out.println(tr.val + "");
            if (null != root.right) {
                stack.push(root.right);
            }
            if (null != root.left) {
                stack.push(root.left);
            }
        }

    }


    static class TreeNode {
        int val;
        TreeNode left; // 左子树
        TreeNode right; // 右子树

        public TreeNode(int val) {
            this.val = val;
        }
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(5);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(4);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(7);
        new Test().preOrder(root);


    }
}
