package learn.data.structure.tree;

import java.util.Stack;

/**
 * 二叉树的后序遍历
 *
 * @ClassName: PostOrder
 * @author: Glorze
 * @since: 2020/10/29 13:18
 */
public class PostOrder {

    public static void postOrder(TreeNode head) {
        if (null == head) {
            return;
        }
        postOrder(head.left);
        postOrder(head.right);
        System.out.println(head.val);
    }

    public static void postOrder2(TreeNode head) {
        if (null == head) {
            return;
        }
        Stack<TreeNode> stack1 = new Stack<>();
        Stack<TreeNode> stack2 = new Stack<>();
        stack1.push(head);
        while (!stack1.isEmpty()) {
            TreeNode node = stack1.pop();
            stack2.push(node);
            if (null != node.left) {
                stack1.push(node.left);
            }
            if (null != node.right) {
                stack1.push(node.right);
            }
        }
        while (!stack2.isEmpty()) {
            System.out.println(stack2.pop().val);
        }
    }
}
