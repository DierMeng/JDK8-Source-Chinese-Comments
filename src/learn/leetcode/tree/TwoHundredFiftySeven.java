package learn.leetcode.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * 给定一个二叉树，返回所有从根节点到叶子节点的路径。 
 * 
 *  说明: 叶子节点是指没有子节点的节点。 
 * 
 *  示例: 
 * 
 *  输入:
 * 
 *    1
 *  /   \
 * 2     3
 *  \
 *   5
 * 
 * 输出: ["1->2->5", "1->3"]
 * 
 * 解释: 所有根节点到叶子节点的路径为: 1->2->5, 1->3 
 *  Related Topics 树 深度优先搜索 
 */
public class TwoHundredFiftySeven {
    public List<String> binaryTreePaths(TreeNode root) {
        List<String> paths = new ArrayList<String>();
        constructPaths(root, "", paths);
        return paths;
    }

    public void constructPaths(TreeNode root, String path, List<String> paths) {
        if (null != root) {
            StringBuffer pathSB = new StringBuffer(path);
            pathSB.append(Integer.toString(root.val));
            if (null == root.left && null == root.right) {
                paths.add(pathSB.toString());
            } else {
                pathSB.append("->");
                constructPaths(root.left, pathSB.toString(), paths);
                constructPaths(root.right, pathSB.toString(), paths);
            }
        }
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
