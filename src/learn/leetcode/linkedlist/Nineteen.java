package learn.leetcode.linkedlist;

/**
 * 给定一个链表，删除链表的倒数第 n 个节点，并且返回链表的头结点。 
 * 
 *  示例： 
 * 
 *  给定一个链表: 1->2->3->4->5, 和 n = 2.
 * 
 * 当删除了倒数第二个节点后，链表变为 1->2->3->5.
 *  
 * 
 *  说明： 
 * 
 *  给定的 n 保证是有效的。 
 * 
 *  进阶： 
 * 
 *  你能尝试使用一趟扫描实现吗？ 
 *  Related Topics 链表 双指针 
 */
public class Nineteen {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode pre = new ListNode(0);
        pre.next = head;
        ListNode start = pre;
        ListNode end = pre;
        while (n != 0) {
            start = start.next;
            n--;
        }
        while (start.next != null) {
            start = start.next;
            end = end.next;
        }
        end.next = end.next.next;
        return pre.next;
    }

    public class ListNode {
        int val;
        ListNode next;
        ListNode(int x) {
            val = x;
        }
    }
}
