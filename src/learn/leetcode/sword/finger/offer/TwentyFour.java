package learn.leetcode.sword.finger.offer;

import learn.leetcode.linkedlist.TwoHundredSix;

/**
 * 定义一个函数，输入一个链表的头节点，反转该链表并输出反转后链表的头节点。 
 * 
 *  
 * 
 *  示例: 
 * 
 *  输入: 1->2->3->4->5->NULL
 * 输出: 5->4->3->2->1->NULL 
 * 
 *  
 * 
 *  限制： 
 * 
 *  0 <= 节点个数 <= 5000 
 * 
 *  
 * 
 *  注意：本题与主站 206 题相同：https:leetcode-cn.com/problems/reverse-linked-list/ 
 *  Related Topics 链表 
 */
public class TwentyFour {
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

    public class ListNode {
        int val;
        ListNode next;
        ListNode(int x) {
            val = x;
        }
    }
    
}
