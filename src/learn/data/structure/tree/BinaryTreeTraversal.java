package learn.data.structure.tree;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * 构建二叉树及二叉树的深度优先遍历
 *  1.前序遍历 根节点、左子树、右子树
 *  2.中序遍历 左子树、根节点、右子树
 *  3.后序遍历 左子树、右子树、根节点
 * 广度优先遍历
 *  层序遍历
 *      借助队列，按照层序输出
 *
 *  源码来自《漫画算法》，侵删
 */
public class BinaryTreeTraversal {

    /**
     * 二叉树的构建
     * @Title: createBinaryTree
     * @param inputList
     * @return learn.data.structure.tree.BinaryTreeTraversal.TreeNode
     * @author: Glorze
     * @since: 2020/4/13 21:41
     */
    public static TreeNode createBinaryTree(LinkedList<Integer> inputList) {
        TreeNode node = null;
        if (null == inputList || inputList.isEmpty()) {
            return null;
        }

        Integer data = inputList.removeFirst();
        if (null != data) {
            node = new TreeNode(data);
            node.leftChild = createBinaryTree(inputList);
            node.rightChild = createBinaryTree(inputList);
        }
        return node;
    }

    /**
     * 前序遍历（递归）
     * @Title: preOrderTraveral
     * @param node
     * @return void
     * @author: Glorze
     * @since: 2020/4/13 21:44
     */
    public static void preOrderTraveral(TreeNode node) {
        if (null == node) {
            return;
        }
        System.out.println(node.data);
        preOrderTraveral(node.leftChild);
        preOrderTraveral(node.rightChild);
    }

    /**
     * 前序遍历（栈）
     * @Title: preOrderTraveralWithStack
     * @param node
     * @return void
     * @author: Glorze
     * @since: 2020/4/13 21:58
     */
    public static void preOrderTraveralWithStack(TreeNode node) {
        Stack<TreeNode> stack = new Stack<>();
        while (null != node || !stack.isEmpty()) {
            // 迭代访问节点的左孩子，入栈
            while (null != node) {
                System.out.println(node.data);
                stack.push(node);
                node = node.leftChild;
            }
            // 如果没有左孩子，弹出栈顶节点，访问节点的右孩子
            if (!stack.isEmpty()) {
                node = stack.pop();
                node = node.rightChild;
            }
        }

    }

    /**
     * 中序遍历
     * @Title: inOrderTraveral
     * @param node
     * @return void
     * @author: Glorze
     * @since: 2020/4/13 21:46
     */
    public static void inOrderTraveral(TreeNode node) {
        if (null == node) {
            return;
        }
        inOrderTraveral(node.leftChild);
        System.out.println(node.data);
        inOrderTraveral(node.rightChild);
    }

    /**
     * 后序遍历
     * @Title: postOrderTraveral
     * @param node
     * @return void
     * @author: Glorze
     * @since: 2020/4/13 22:10
     */
    public static void postOrderTraveral(TreeNode node) {
        if (null == node) {
            return;
        }
        postOrderTraveral(node.leftChild);
        postOrderTraveral(node.rightChild);
        System.out.println(node.data);
    }

    /**
     * 层序遍历
     * @Title: levelOrderTraveral
     * @param node
     * @return void
     * @author: Glorze
     * @since: 2020/4/13 22:15
     */
    public static void levelOrderTraveral(TreeNode node) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(node);
        while (!queue.isEmpty()) {
            TreeNode treeNode = queue.poll();
            System.out.println(treeNode.data);
            if (null != treeNode.leftChild) {
                queue.offer(treeNode.leftChild);
            }
            if (null != treeNode.rightChild) {
                queue.offer(treeNode.rightChild);
            }
        }
    }

    public static void main(String[] args) {
        LinkedList<Integer> inputList = new LinkedList<>(Arrays.asList(new Integer[]{8, 18, 56, null, 17, 96, null, 4}));
        TreeNode node = createBinaryTree(inputList);
        System.out.println("前序遍历（递归）：");
        preOrderTraveral(node);
        System.out.println("前序遍历（栈）：");
        preOrderTraveralWithStack(node);
        System.out.println("中序遍历：");
        inOrderTraveral(node);
        System.out.println("前序遍历：");
        postOrderTraveral(node);
        System.out.println("层序遍历：");
        levelOrderTraveral(node);
    }

    public static class TreeNode {

        /**
         * 树值
         */
        int data;

        /**
         * 左节点
         */
        TreeNode leftChild;

        /**
         * 右节点
         */
        TreeNode rightChild;

        public TreeNode(int data) {
            this.data = data;
        }
    }
}
