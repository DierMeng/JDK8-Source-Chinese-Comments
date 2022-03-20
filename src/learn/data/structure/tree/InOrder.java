package learn.data.structure.tree;

import java.util.Stack;

/**
 * 二叉树的终须遍历 递归 迭代
 *
 * @ClassName: InOrder
 * @author: Glorze
 * @since: 2020/10/29 12:55
 */
public class InOrder {

    /**
     * 递归 左根右
     * @Title: inOrder
     * @param head 头结点
     * @return void
     */
    public static void inOrder(TreeNode head) {
        if (null == head) {
            return;
        }
        inOrder(head.left);
        System.out.println(head.val);
        inOrder(head.right);
    }

    /**
     * 迭代
     * @Title: inOrder2
     * @param head
     * @return void
     */
    public static void inOrder2(TreeNode head) {
        if(null == head) {
            return;
        }
        Stack<TreeNode> stack = new Stack<>();
        while (null != head && !stack.isEmpty()) {
            while (null != head) {
                stack.push(head);
                head = head.left;
            }
            head = stack.pop();
            System.out.println(head.val);
            head = head.right;
        }
    }
}
