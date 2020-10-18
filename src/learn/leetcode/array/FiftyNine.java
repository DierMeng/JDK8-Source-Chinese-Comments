package learn.leetcode.array;

import java.util.ArrayList;
import java.util.List;

/**
 * 给定一个正整数 n，生成一个包含 1 到 n2 所有元素，且元素按顺时针顺序螺旋排列的正方形矩阵。 
 * 
 *  示例: 
 * 
 *  输入: 3
 * 输出:
 * [
 *  [ 1, 2, 3 ],
 *  [ 8, 9, 4 ],
 *  [ 7, 6, 5 ]
 * ] 
 *  Related Topics 数组 
 */
public class FiftyNine {
    public int[][] generateMatrix(int n) {
        int left = 0;
        int right = n - 1;
        int top = 0;
        int bottom = n - 1;
        int[][] mat = new int[n][n];
        int num = 1;
        int target = n * n;
        while(num <= target){
            for(int i = left; i <= right; i++) {
                mat[top][i] = num++; // left to right.
            }
            top++;
            for(int i = top; i <= bottom; i++) {
                mat[i][right] = num++; // top to bottom.
            }
            right--;
            for(int i = right; i >= left; i--) {
                mat[bottom][i] = num++; // right to left.
            }
            bottom--;
            for(int i = bottom; i >= top; i--) {
                mat[i][left] = num++; // bottom to top.
            }
            left++;
        }
        return mat;
    }

}
