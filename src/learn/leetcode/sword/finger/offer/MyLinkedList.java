package com.glorze.leetcode.sword.finger.offer;

/**
 * 面试题18.删除链表的节点
 *  给定单向链表的头指针和一个要删除的节点的值，定义一个函数删除该节点。 返回删除后的链表的头节点。
 *      保证链表中节点的值互不相同
 *  示例:
 *      输入: head = [4,5,1,9], val = 5
 *      输出: [4,1,9]
 *      输入: head = [4,5,1,9], val = 1
 *      输出: [4,5,9]
 * 解题思路:
 *  设置伪节点指向头节点,如果删除的是头节点,直接返回头节点的下一个节点即可.
 *  链表节点的删除: head.next = head.next.next
 *
 * @ClassName: MyLinkedList
 * @author: 高泽
 * @since: 2020/4/14 15:42
 */
public class MyLinkedList {

    public ListNode deleteNode(ListNode head, int val) {
        ListNode fakerNode = new ListNode(0);
        fakerNode.next = head;
        if (head.val == val) {
            return head.next;
        }
        while (null != head && null != head.next) {
            if (val == head.next.val) {
                head.next = head.next.next;
            }
            head = head.next;
        }
        return fakerNode.next;
    }

    public class ListNode {
        int val;
        ListNode next;
        ListNode(int val) {
            this.val = val;
        }
    }

}
