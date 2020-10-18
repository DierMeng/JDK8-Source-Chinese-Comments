package learn.leetcode.array;

import java.util.Arrays;

/**
 * 给定一个按非递减顺序排序的整数数组 A，返回每个数字的平方组成的新数组，要求也按非递减顺序排序。 
 * 
 *  
 * 
 *  示例 1： 
 * 
 *  输入：[-4,-1,0,3,10]
 * 输出：[0,1,9,16,100]
 *  
 * 
 *  示例 2： 
 * 
 *  输入：[-7,-3,2,3,11]
 * 输出：[4,9,9,49,121]
 *  
 * 
 *  
 * 
 *  提示： 
 * 
 *  
 *  1 <= A.length <= 10000 
 *  -10000 <= A[i] <= 10000 
 *  A 已按非递减顺序排序。 
 *  
 *  Related Topics 数组 双指针
 */
public class NineHundredSeventySeven {
    public int[] sortedSquares(int[] A) {
        if (0 == A.length) {
            return null;
        }
        int length = A.length;
        int[] answer = new int[length];
        for (int i = 0; i < length; i++) {
            answer[i] = A[i] * A[i];
        }
        Arrays.sort(answer);
        return answer;
    }

    public int[] sortedSquares2(int[] A) {
        if (0 == A.length) {
            return null;
        }
        int left = 0;
        int right = A.length - 1;
        int[] answer = new int[A.length];
        int index = A.length - 1;
        while (index >= 0) {
            if (Math.abs(A[left]) > Math.abs(A[right])) {
                answer[index--] = A[left] * A[left];
                left++;
            } else {
                answer[index--] = A[right] * A[right];
                right--;
            }
        }
        return answer;
    }
}
