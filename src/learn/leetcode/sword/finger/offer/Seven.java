package learn.leetcode.sword.finger.offer;

import learn.leetcode.tree.OneHundredFive;

import java.util.ArrayList;
import java.util.List;

/**
 * 输入某二叉树的前序遍历和中序遍历的结果，请重建该二叉树。假设输入的前序遍历和中序遍历的结果中都不含重复的数字。 
 * 
 *  
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
 * 
 *  
 * 
 *  限制： 
 * 
 *  0 <= 节点个数 <= 5000 
 * 
 *  
 * 
 *  注意：本题与主站 105 题重复：https:leetcode-cn.com/problems/construct-binary-tree-from-
 * preorder-and-inorder-traversal/ 
 *  Related Topics 树 递归 
 */
public class Seven {

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
