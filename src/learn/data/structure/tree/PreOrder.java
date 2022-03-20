package learn.data.structure.tree;

import java.util.Stack;

/**
 * 前序遍历 递归、迭代
 *
 * @ClassName: PreOrder
 * @author: Glorze
 * @since: 2020/10/29 12:47
 */
public class PreOrder {

    /**
     * 递归 前序遍历 根左右
     * @Title: preOrder
     * @param head 跟节点
     * @return void
     */
    public static void preOrder(TreeNode head) {
        if (null == head) {
            return;
        }
        System.out.println(head.val);
        preOrder(head.left);
        preOrder(head.right);
    }

    public static void preOrder2(TreeNode head) {
        if (null == head) {
            return;
        }
        Stack<TreeNode> stack = new Stack<>();
        stack.push(head);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            System.out.println(node.val);
            if (null != node.right) {
                stack.push(node.right);
            }
            if (null != node.left) {
                stack.push(node.left);
            }
        }
    }


}
