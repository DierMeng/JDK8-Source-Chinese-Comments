package learn.data.structure.tree;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * 哈夫曼树的构建
 * 时间复杂度：O(nlogn)
 *
 * 源码来自《漫画算法》，侵删
 */
public class HuffmanTree {

    /**
     * 节点输出的承载
     */
    private Node root;

    /**
     * 节点数组
     */
    private Node[] nodes;

    /**
     * 构建哈夫曼树
     *  1.构建森林：把每个叶子节点当做独立的只有根节点的树，形成一个森林，按照权重从小到大按照队列展示
     *  2.选择权重最小的两个节点，生成新的根节点（权重相加）
     *  3.删除已构建的节点，将新的根节点加入到队列
     *  4.再次选择两个权重最小的节点生成新的父节点
     *  5.重复依照上述进行构建，最后去除掉两两节点相加的父节点权重节点，就是一颗哈夫曼树
     * @Title: createHuffman
     * @param weights
     * @return void
     * @author: Glorze
     * @since: 2020/4/12 21:25
     */
    public void createHuffman(int[] weights) {
        // 使用优先队列辅助哈夫曼树的构造
        Queue<Node> nodeQueue = new PriorityQueue<>();
        nodes = new Node[weights.length];
        // 构建森林,入队列
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node(weights[i]);
            nodeQueue.add(nodes[i]);
        }
        // 对第二三步进行循环重复，直到队列只剩一个元素
        while (nodeQueue.size() > 1) {
            // 从队列中选择权重最小的两个节点，生成新的根节点（权重相加）
            Node left = nodeQueue.poll();
            Node right = nodeQueue.poll();
            Node parent = new Node(left.weight + right.weight, left, right);
            nodeQueue.add(parent);
        }
        root = nodeQueue.poll();
    }

    /**
     * 前序遍历打印树
     * @Title: output
     * @param head
     * @return void
     * @author: Glorze
     * @since: 2020/4/12 22:09
     */
    public void output(Node head) {
        if (null == head) {
            return;
        }
        System.out.println(head.weight);
        output(head.leftChild);
        output(head.rightChild);
    }

    public static void main(String[] args) {
        HuffmanTree huffmanTree = new HuffmanTree();
        int[] weights = {1,8,9,18,26,37,49,58};
        huffmanTree.createHuffman(weights);
        huffmanTree.output(huffmanTree.root);
    }

    public static class  Node implements Comparable<Node> {

        /**
         * 节点权重
         */
        int weight;

        /**
         * 左叶子节点
         */
        Node leftChild;

        /**
         * 右叶子节点
         */
        Node rightChild;

        public Node(int weight) {
            this.weight = weight;
        }

        public Node(int weight, Node leftChild, Node rightChild) {
            this.weight = weight;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }

        @Override
        public int compareTo(Node o) {
            return new Integer(this.weight).compareTo(o.weight);
        }
    }

}
