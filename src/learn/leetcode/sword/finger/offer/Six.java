package learn.leetcode.sword.finger.offer;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 输入一个链表的头节点，从尾到头反过来返回每个节点的值（用数组返回）。 
 * 
 *  
 * 
 *  示例 1： 
 * 
 *  输入：head = [1,3,2]
 * 输出：[2,3,1] 
 * 
 *  
 * 
 *  限制： 
 * 
 *  0 <= 链表长度 <= 10000 
 *  Related Topics 链表 
 */
public class Six {

    public int[] reversePrint(ListNode head) {
        LinkedList<Integer> stack = new LinkedList<>();
        while (null != head) {
            stack.addLast(head.val);
            head = head.next;
        }
        int[] result = new int[stack.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = stack.removeLast();
        }
        return result;
    }

    public class ListNode {
        int val;
        ListNode next;
        ListNode(int x) {
            val = x;
        }
    }
}
