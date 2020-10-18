package learn.leetcode.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * 根据一棵树的前序遍历与中序遍历构造二叉树。 
 * 
 *  注意: 
 * 你可以假设树中没有重复的元素。 
 * 
 *  例如，给出 
 * 
 *  前序遍历 preorder = [3,9,20,15,7]
 * 中序遍历 inorder = [9,3,15,20,7] 
 * 
 *  返回如下的二叉树： 
 * 
 *      3
 *    / \
 *   9  20
 *     /  \
 *    15   7 
 *  Related Topics 树 深度优先搜索 数组 
 */
public class OneHundredFive {

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        // 把前序遍历的值和中序遍历的值放到 list 中
        List<Integer> preOrderList = new ArrayList<>();
        List<Integer> inOrderList = new ArrayList<>();
        for (int i = 0; i < preorder.length; i++) {
            preOrderList.add(preorder[i]);
            inOrderList.add(inorder[i]);
        }
        return helper(preOrderList, inOrderList);
    }

    public TreeNode buildTree2(int[] preorder, int[] inorder) {
        if (preorder.length == 0) {
            return null;
        }
        Stack<TreeNode> s = new Stack<>();
        // 前序的第一个其实就是根节点
        TreeNode root = new TreeNode(preorder[0]);
        TreeNode current = root;
        for (int i = 1, j = 0; i < preorder.length; i++) {
            // 第一种情况
            // 前序遍历挨着的两个值比如 m 和 n，n 是 m 左子树节点的值。
            if (current.val != inorder[j]) {
                current.left = new TreeNode(preorder[i]);
                s.push(current);
                current = current.left;
            } else {
                // 第二种情况
                // n 是 m 右子树节点的值或者是 m 某个祖先节点的右节点的值。
                j++;
                // 找到合适的current，然后确定他的右节点
                while (!s.empty() && s.peek().val == inorder[j]) {
                    current = s.pop();
                    j++;
                }
                // 给 cur 添加右节点
                current = current.right = new TreeNode(preorder[i]);
            }
        }
        return root;
    }

    private TreeNode helper(List<Integer> preOrderList, List<Integer> inOrderList) {
        if (0 == inOrderList.size()) {
            return null;
        }
        // 前序遍历的第一个值就是根节点
        int rootValue = preOrderList.remove(0);
        // 创建根结点
        TreeNode root = new TreeNode(rootValue);
        // 查看根节点在中序遍历中的位置，然后再把中序遍历的数组劈两半，前面部分是根节点左子树的所有值，后面部分是根节点右子树的所有值
        int middle = inOrderList.indexOf(rootValue);
        // [0，mid) 是左子树的所有值，inorderList.subList(0, mid) 表示截取 inorderList 的值，截取的范围是[0，mid)，包含 0 不包含 mid。
        root.left = helper(preOrderList, inOrderList.subList(0, middle));
        // [mid+1，inorderList.size()) 是右子树的所有值，inorderList.subList(mid + 1, inorderList.size()) 表示截取 inorderList 的值，截取的范围是 [mid+1，inorderList.size())，包含 mid+1 不包含 inorderList.size()。
        root.right = helper(preOrderList, inOrderList.subList(middle + 1, inOrderList.size()));
        return root;
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
