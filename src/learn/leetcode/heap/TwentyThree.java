package learn.leetcode.heap;

import java.util.*;

/**
 * 合并 k 个排序链表，返回合并后的排序链表。请分析和描述算法的复杂度。
 *
 * 示例:
 *
 * 输入:
 *
 * [1->4->5,1->3->4,2->6]
 *
 * 输出: 1->1->2->3->4->4->5->6
 *
 * Related Topics 堆 链表 分治算法
 *
 */
public class TwentyThree {

    /**
     * 利用最小二叉堆做排序
     * 创建堆的时候声明最小堆,然后遍历多个链表入堆,输出就是排序好的
     *
     * 空间复杂度：O(N)
     */
    public ListNode mergeKLists(ListNode[] lists) {
        PriorityQueue<ListNode> listNodePriorityQueue = new PriorityQueue<>(new Comparator<ListNode>() {
            @Override
            public int compare(ListNode o1, ListNode o2) {
                return o1.val - o2.val;
            }
        });
        for (ListNode ln : lists) {
            while (null != ln) {
                listNodePriorityQueue.add(ln);
                ln = ln.next;
            }

        }
        // 声明一个哑节点作为头节点
        ListNode dummy = new ListNode(-1);
        // 这个头节点用来输出后面已经排序好的节点
        ListNode head = dummy;
        while (!listNodePriorityQueue.isEmpty()) {
            // 移除并返问队列头部的元素 如果队列为空，则返回null
            dummy.next = listNodePriorityQueue.poll();
            dummy = dummy.next;
        }
        // 防止链表死循环或者为空
        dummy.next = null;
        return head.next;
    }

    /**
     * 对最小二叉堆做排序空间复杂度的优化
     *
     * 之前的思路是将 K 个链表全部放入到最小二叉堆当中
     * 现在思路是堆中仅维护 K 个元素
     *
     * 空间复杂度：O(N) 优化到 O(K)
     *
     * K 就是 lists.length
     */
    public ListNode mergeKLists2(ListNode[] lists) {
        PriorityQueue<ListNode> listNodePriorityQueue = new PriorityQueue<>(new Comparator<ListNode>() {
            @Override
            public int compare(ListNode o1, ListNode o2) {
                return o1.val - o2.val;
            }
        });
        // 声明一个哑节点
        ListNode dummy = new ListNode(-1);
        ListNode current = dummy;
        for (ListNode ln : lists) {
            // 每个链表的头节点
            ListNode head = ln;
            if (null != head) {
                // 将每个链表的头节点放入到队列中去
                listNodePriorityQueue.add(head);
            }
        }
        // 从队列中获取节点
        // 如果队列中的节点还有下一个节点,那么就将这个下一个节点放入到堆中
        while (listNodePriorityQueue.size() > 0) {
            ListNode node = listNodePriorityQueue.poll();
            current.next = node;
            current = current.next;
            if (null != node.next) {
                listNodePriorityQueue.add(node.next);
            }
        }
        current.next = null;
        return dummy.next;
    }

    /**
     * 两两排序的思路
     *
     * 将 lists[0] 作为最终合并的链表，然后将 list[0] 和 lists[1] 合并成 lists[0-1]
     * 再将 lists[0-1] 和 lists[2] 合并，如此反复最终 lists[0] 就是最终结果
     */
    public ListNode mergeKLists3(ListNode[] lists) {
        if(lists==null || lists.length==0) {
            return null;
        }
        ListNode result = lists[0];
        for (ListNode ln : lists) {
            result = merge(result, ln);
        }
        return result;
    }

    /**
     * 分治算法
     *
     * 通过 mid 将数组一分为二，并不断缩小规模，当规模为 1 时返回并开始合并
     *
     * 通过合并两个链表，不断增大其规模，整体看就是不断缩小,最后不断扩大的过程
     */
    public ListNode mergeKLists4(ListNode[] lists) {
        if(lists==null || lists.length==0) {
            return null;
        }
        return helper(lists,0,lists.length-1);
    }

    private ListNode helper(ListNode[] lists, int begin, int end) {
        if(begin==end) {
            return lists[begin];
        }
        int mid = begin+(end-begin)/2;
        ListNode left = helper(lists,begin,mid);
        ListNode right = helper(lists,mid+1,end);
        return merge(left,right);
    }

    public ListNode merge(ListNode a, ListNode b) {
        if(a==null || b==null) {
            return (a==null) ? b : a;
        }
        if(a.val<=b.val) {
            a.next = merge(a.next,b);
            return a;
        } else {
            b.next = merge(a,b.next);
            return b;
        }
    }

    public class ListNode {
        int val;
        ListNode next;
        ListNode(int x) { val = x; }
    }
}
