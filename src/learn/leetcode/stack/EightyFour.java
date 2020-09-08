package learn.leetcode.stack;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 给定 n 个非负整数，用来表示柱状图中各个柱子的高度。每个柱子彼此相邻，且宽度为 1 。 
 * 
 *  求在该柱状图中，能够勾勒出来的矩形的最大面积。 
 * 
 *  
 * 
 *  
 * 
 *  以上是柱状图的示例，其中每个柱子的宽度为 1，给定的高度为 [2,1,5,6,2,3]。 
 * 
 *  
 * 
 *  
 * 
 *  图中阴影部分为所能勾勒出的最大矩形面积，其面积为 10 个单位。 
 * 
 *  
 * 
 *  示例: 
 * 
 *  输入: [2,1,5,6,2,3]
 * 输出: 10 
 *  Related Topics 栈 数组 
 */
public class EightyFour {
    public int largestRectangleArea(int[] heights) {
        int[] temp = new int[heights.length + 2];
        System.arraycopy(heights, 0, temp, 1, heights.length);
        Deque<Integer> stack = new ArrayDeque<>();
        int area = 0;
        for (int i = 0; i < temp.length; i++) {
            while (!stack.isEmpty() && temp[i] < temp[stack.peek()]) {
                int h = temp[stack.pop()];
                area = Math.max(area, (i - stack.peek() - 1) * h);
            }
            stack.push(i);
        }
        return area;
    }
}
