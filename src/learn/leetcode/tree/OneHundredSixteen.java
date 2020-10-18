package learn.leetcode.tree;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 给定一个完美二叉树，其所有叶子节点都在同一层，每个父节点都有两个子节点。二叉树定义如下： 
 * 
 *  struct Node {
 *   int val;
 *   Node *left;
 *   Node *right;
 *   Node *next;
 * }
 * 
 *  填充它的每个 next 指针，让这个指针指向其下一个右侧节点。如果找不到下一个右侧节点，则将 next 指针设置为 NULL。 
 * 
 *  初始状态下，所有 next 指针都被设置为 NULL。 
 * 
 *  
 * 
 *  示例： 
 * 
 *  
 * 
 *  输入：{"$id":"1","left":{"$id":"2","left":{"$id":"3","left":null,"next":null,"ri
 * ght":null,"val":4},"next":null,"right":{"$id":"4","left":null,"next":null,"right
 * ":null,"val":5},"val":2},"next":null,"right":{"$id":"5","left":{"$id":"6","left"
 * :null,"next":null,"right":null,"val":6},"next":null,"right":{"$id":"7","left":nu
 * ll,"next":null,"right":null,"val":7},"val":3},"val":1}
 * 
 * 输出：{"$id":"1","left":{"$id":"2","left":{"$id":"3","left":null,"next":{"$id":"4
 * ","left":null,"next":{"$id":"5","left":null,"next":{"$id":"6","left":null,"next"
 * :null,"right":null,"val":7},"right":null,"val":6},"right":null,"val":5},"right":
 * null,"val":4},"next":{"$id":"7","left":{"$ref":"5"},"next":null,"right":{"$ref":
 * "6"},"val":3},"right":{"$ref":"4"},"val":2},"next":null,"right":{"$ref":"7"},"va
 * l":1}
 * 
 * 解释：给定二叉树如图 A 所示，你的函数应该填充它的每个 next 指针，以指向其下一个右侧节点，如图 B 所示。
 *  
 * 
 *  
 * 
 *  提示： 
 * 
 *  
 *  你只能使用常量级额外空间。 
 *  使用递归解题也符合要求，本题中递归程序占用的栈空间不算做额外的空间复杂度。 
 *  
 *  Related Topics 树 深度优先搜索 
 */
public class OneHundredSixteen {

    public Node connect(Node root) {
        if (null == root) {
            return root;
        }
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int levelCount = queue.size();
            Node pre = null;
            for (int i = 0; i < levelCount; i++) {
                Node node = queue.poll();
                if (null != pre) {
                    pre.next = node;
                }
                pre = node;
                if (null != node.left) {
                    queue.add(node.left);
                }
                if (null != node.right) {
                    queue.add(node.right);
                }
            }
        }
        return root;
    }

    public Node connect2(Node root) {
        if (null == root) {
            return root;
        }
        Node current = root;
        while (null != current) {
            Node dummy = new Node(0);
            Node pre = dummy;
            while (null != current && null != current.left) {
                pre.next = current.left;
                pre = pre.next;
                pre.next = current.right;
                pre = current.next;
            }
            current = dummy.next;
        }
        return root;
    }

    public Node connect3(Node root) {
        if (null == root) {
            return root;
        }
        Node pre = root;
        Node current = null;
        while (null != pre.left) {
            current = pre;
            while (null != current) {
                current.left.next = current.right;
                if (null != current.next) {
                    current.right.next = current.next.left;
                }
                current = current.next;
            }
            pre = pre.left;
        }
        return root;
    }

    public Node connect4(Node root) {
        dfs(root, null);
        return root;
    }

    private void dfs(Node current, Node next) {
        if (null == current) {
            return;
        }
        current.next = next;
        dfs(current.left, current.right);
        dfs(current.right, current.next == null ? null : current.left.left);
    }

    public void levelOrder(Node tree) {
        if (null == tree) {
            return;
        }
        Queue<Node> queue = new LinkedList<>();
        queue.add(tree);
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (node.left != null) {
                queue.add(node.left);
            }
            if (node.right != null) {
                queue.add(node.right);
            }
        }
    }


    class Node {
        public int val;
        public Node left;
        public Node right;
        public Node next;

        public Node() {}

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, Node _left, Node _right, Node _next) {
            val = _val;
            left = _left;
            right = _right;
            next = _next;
        }
    }
}
