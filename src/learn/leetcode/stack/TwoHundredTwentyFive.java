package learn.leetcode.stack;

import java.util.LinkedList;

/**
 * 使用队列实现栈的下列操作： 
 * 
 *  
 *  push(x) -- 元素 x 入栈 
 *  pop() -- 移除栈顶元素 
 *  top() -- 获取栈顶元素 
 *  empty() -- 返回栈是否为空 
 *  
 * 
 *  注意: 
 * 
 *  
 *  你只能使用队列的基本操作-- 也就是 push to back, peek/pop from front, size, 和 is empty 这些操作是合
 * 法的。 
 *  你所使用的语言也许不支持队列。 你可以使用 list 或者 deque（双端队列）来模拟一个队列 , 只要是标准的队列操作即可。 
 *  你可以假设所有操作都是有效的（例如, 对一个空的栈不会调用 pop 或者 top 操作）。 
 *  
 *  Related Topics 栈 设计 
 */
public class TwoHundredTwentyFive {

    private LinkedList<Integer> queue1 = new LinkedList<>();
    private LinkedList<Integer> queue2 = new LinkedList<>();


    /** Initialize your data structure here. */
    public TwoHundredTwentyFive() {

    }

    /** Push element x onto stack. */
    public void push(int x) {
        if (queue1.isEmpty()) {
            queue2.offer(x);
        } else {
            queue1.offer(x);
        }
    }

    /** Removes the element on top of the stack and returns that element. */
    public int pop() {
        if (queue1.isEmpty()) {
            while (queue2.size() > 1) {
                queue1.offer(queue2.poll());
            }
            return queue2.poll();
        } else {
            while (queue1.size() > 1) {
                queue2.offer(queue1.poll());
            }
            return queue1.poll();
        }
    }

    /** Get the top element. */
    public int top() {
        int top = 0;
        if (queue1.isEmpty()) {
            while (queue2.size() > 1) {
                queue1.offer(queue2.poll());
            }
            top = queue2.poll();
            queue1.offer(top);
        } else {
            while (queue1.size() > 1) {
                queue2.offer(queue1.poll());
            }
            top = queue1.poll();
            queue2.offer(top);
        }
        return top;
    }

    /** Returns whether the stack is empty. */
    public boolean empty() {
        return queue1.isEmpty() && queue2.isEmpty();
    }
}
