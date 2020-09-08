package learn.leetcode.stack;

import java.util.Stack;

/**
 * 给定一个直方图(也称柱状图)，假设有人从上面源源不断地倒水，最后直方图能存多少水量?直方图的宽度为 1。 
 * 
 *  
 * 
 *  上面是由数组 [0,1,0,2,1,0,1,3,2,1,2,1] 表示的直方图，在这种情况下，可以接 6 个单位的水（蓝色部分表示水）。 感谢 Marco
 * s 贡献此图。 
 * 
 *  示例: 
 * 
 *  输入: [0,1,0,2,1,0,1,3,2,1,2,1]
 * 输出: 6 
 *  Related Topics 栈 数组 双指针 
 */
public class InterviewSeventeenDotTwentyOne {
    public int trap(int[] height) {
        if (null == height) {
            return 0;
        }
        Stack<Integer> stack = new Stack<Integer>();
        int result = 0;
        for (int i = 0; i < height.length; i++) {
            while (!stack.isEmpty() && height[stack.peek()] < height[i]) {
                int currentIndex = stack.pop();
                while (!stack.isEmpty() && height[stack.peek()] == height[currentIndex]) {
                    stack.pop();
                }
                if (!stack.isEmpty()) {
                    int stackTop = stack.peek();
                    result = result + (Math.min(height[stackTop], height[i]) - height[currentIndex]) * (i - stackTop - 1);
                }
            }
            stack.add(i);
        }
        return result;
    }
}
