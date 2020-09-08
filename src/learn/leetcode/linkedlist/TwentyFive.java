package learn.leetcode.linkedlist;

/**
 * 给你一个链表，每 k 个节点一组进行翻转，请你返回翻转后的链表。 
 * 
 *  k 是一个正整数，它的值小于或等于链表的长度。 
 * 
 *  如果节点总数不是 k 的整数倍，那么请将最后剩余的节点保持原有顺序。 
 * 
 *  
 * 
 *  示例： 
 * 
 *  给你这个链表：1->2->3->4->5 
 * 
 *  当 k = 2 时，应当返回: 2->1->4->3->5 
 * 
 *  当 k = 3 时，应当返回: 3->2->1->4->5 
 * 
 *  
 * 
 *  说明： 
 * 
 *  
 *  你的算法只能使用常数的额外空间。 
 *  你不能只是单纯的改变节点内部的值，而是需要实际进行节点交换。 
 *  
 *  Related Topics 链表 
 */
public class TwentyFive {

    public ListNode reverseKGroup(ListNode head, int k) {
        // 定义一个假的节点。
        ListNode dummy = new ListNode(0);
        // 假节点的 next 指向 head。
        // dummy->1->2->3->4->5
        dummy.next = head;
        // 初始化 pre 和 end 都指向 dummy。pre 指每次要翻转的链表的头结点的上一个节点。end 指每次要翻转的链表的尾节点
        ListNode pre = dummy;
        ListNode end = dummy;
        while (end.next != null) {
            // 循环 k 次，找到需要翻转的链表的结尾,这里每次循环要判断 end 是否等于空,因为如果为空，end.next 会报空指针异常。
            // dummy->1->2->3->4->5 若 k 为 2，循环 2 次，end 指向 2
            for (int i = 0; i < k && end != null; i++) {
                end = end.next;
            }
            // 如果 end == null，即需要翻转的链表的节点数小于 k，不执行翻转。
            if (end == null) {
                break;
            }
            // 记录下要翻转链表的头节点
            ListNode start = pre.next;
            // 先记录下 end.next,方便后面链接链表
            ListNode next = end.next;
            // 然后断开链表
            end.next = null;
            // 翻转链表, pre.next 指向翻转后的链表。1->2 变成2->1。 dummy->2->1
            pre.next = reverse(start);
            // 翻转后头节点变到最后。通过 .next 把断开的链表重新链接。
            start.next = next;
            // 将 pre 换成下次要翻转的链表的头结点的上一个节点。即 start
            pre = start;
            // 翻转结束，将 end 置为下次要翻转的链表的头结点的上一个节点。即 start
            end = pre;
        }
        return dummy.next;
    }

    private ListNode reverse(ListNode head) {
        ListNode pre = null;
        ListNode current = head;
        while (current != null) {
            ListNode next = current.next;
            current.next = pre;
            pre = current;
            current = next;
        }
        return pre;
    }

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int x) {
            val = x;
        }
    }

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        System.out.println(new TwentyFive().reverse(head).val);
    }
    
}
