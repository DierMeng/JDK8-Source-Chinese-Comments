package learn.leetcode.linkedlist;

/**
 * 给定一个链表，旋转链表，将链表每个节点向右移动 k 个位置，其中 k 是非负数。 
 * 
 *  示例 1: 
 * 
 *  输入: 1->2->3->4->5->NULL, k = 2
 * 输出: 4->5->1->2->3->NULL
 * 解释:
 * 向右旋转 1 步: 5->1->2->3->4->NULL
 * 向右旋转 2 步: 4->5->1->2->3->NULL
 *  
 * 
 *  示例 2: 
 * 
 *  输入: 0->1->2->NULL, k = 4
 * 输出: 2->0->1->NULL
 * 解释:
 * 向右旋转 1 步: 2->0->1->NULL
 * 向右旋转 2 步: 1->2->0->NULL
 * 向右旋转 3 步:0->1->2->NULL
 * 向右旋转 4 步:2->0->1->NULL 
 *  Related Topics 链表 双指针 
 */
public class SixtyOne {
    public ListNode rotateRight(ListNode head, int k) {
        if (null == head) {
            return null;
        }
        if (null == head.next) {
            return head;
        }
        int n = 1;
        ListNode oldTail = head;
        // 闭环并且计算链表长度
        for (;oldTail.next != null; n++) {
            oldTail = oldTail.next;
        }
        oldTail.next = head;

        ListNode newTail = head;
        for (int i = 0; i < n - k % n - 1; i++) {
            newTail = newTail.next;
        }
        ListNode newHead = newTail.next;
        // 断开环
        newTail.next = null;
        return newHead;
    }

    public class ListNode {
        int val;
        ListNode next;
        ListNode(int x) {
            val = x;
        }
    }
}
