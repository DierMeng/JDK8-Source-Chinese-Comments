package learn.leetcode.binary.search;

/**
 * 编写一个高效的算法来搜索 m x n 矩阵 matrix 中的一个目标值 target。该矩阵具有以下特性：
 *
 *
 *  每行的元素从左到右升序排列。
 *  每列的元素从上到下升序排列。
 *
 *
 *  示例:
 *
 *  现有矩阵 matrix 如下：
 *
 *  [
 *   [1,   4,  7, 11, 15],
 *   [2,   5,  8, 12, 19],
 *   [3,   6,  9, 16, 22],
 *   [10, 13, 14, 17, 24],
 *   [18, 21, 23, 26, 30]
 * ]
 *
 *
 *  给定 target = 5，返回 true。
 *
 *  给定 target = 20，返回 false。
 */
public class TwoHundredForty {
    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0) {
            return false;
        }
        int minRowOrCol = Math.min(matrix.length, matrix[0].length);
        for (int i = 0; i < minRowOrCol; i++) {
            boolean vertical = binarySearch(matrix, target, i, true);
            boolean horizan = binarySearch(matrix, target, i, false);
            if (vertical || horizan) {
                return true;
            }
        }
        return false;
    }

    private boolean binarySearch(int[][] matrix, int target, int start, boolean vertical) {
        int low = start;
        int high = vertical ? matrix[0].length - 1 : matrix.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            // 对列进行二分查找
            if (vertical) {
                if (matrix[start][mid] < target) {
                    low = mid + 1;
                } else if (matrix[start][mid] > target) {
                    high = mid - 1;
                } else if (matrix[start][mid] == target) {
                    return true;
                }
            } else {
                // 对行进行二分查找
                if (matrix[mid][start] < target) {
                    low = mid + 1;
                } else if (matrix[mid][start] > target) {
                    high = mid - 1;
                } else if (matrix[mid][start] == target) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[][] arr = new int[][]{{1,2,3,4,5,6}, {7,8,9,0,1,2}, {3,4,5,6,7,8}};
        System.out.println(arr.length);
        System.out.println(arr[0].length);
    }
}
