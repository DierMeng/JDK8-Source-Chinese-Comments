package learn.leetcode.linkedlist;

/**
 * 反转一个单链表。 
 * 
 *  示例: 
 * 
 *  输入: 1->2->3->4->5->NULL
 * 输出: 5->4->3->2->1->NULL 
 * 
 *  进阶: 
 * 你可以迭代或递归地反转链表。你能否用两种方法解决这道题？ 
 *  Related Topics 链表 
 */
public class TwoHundredSix {
    public ListNode reverseList(ListNode head) {
        ListNode pre = null;
        ListNode current = head;
        ListNode temp = null;
        while (current != null) {
            temp = current.next;
            current.next = pre;
            pre = current;
            current = temp;
        }
        return pre;
    }

    public ListNode reverseList2(ListNode head) {
        //递归终止条件是当前为空，或者下一个节点为空
        if(head==null || head.next==null) {
            return head;
        }
        //这里的cur就是最后一个节点
        ListNode cur = reverseList(head.next);
        //这里请配合动画演示理解
        //如果链表是 1->2->3->4->5，那么此时的cur就是5
        //而head是4，head的下一个是5，下下一个是空
        //所以head.next.next 就是5->4
        head.next.next = head;
        //防止链表循环，需要将head.next设置为空
        head.next = null;
        //每层递归函数都返回cur，也就是最后一个节点
        return cur;
    }

    public class ListNode {
        int val;
        ListNode next;
        ListNode(int x) {
            val = x;
        }
    }
}
