package learn.leetcode.sort;

import java.util.Arrays;

/**
 * 给出一个无重叠的 ，按照区间起始端点排序的区间列表。 
 * 
 *  在列表中插入一个新的区间，你需要确保列表中的区间仍然有序且不重叠（如果有必要的话，可以合并区间）。 
 * 
 *  
 * 
 *  示例 1： 
 * 
 *  输入：intervals = [[1,3],[6,9]], newInterval = [2,5]
 * 输出：[[1,5],[6,9]]
 *  
 * 
 *  示例 2： 
 * 
 *  输入：intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]
 * 输出：[[1,2],[3,10],[12,16]]
 * 解释：这是因为新的区间 [4,8] 与 [3,5],[6,7],[8,10] 重叠。
 *  
 * 
 *  
 * 
 *  注意：输入类型已在 2019 年 4 月 15 日更改。请重置为默认代码定义以获取新的方法签名。 
 *  Related Topics 排序 数组 
 */
public class FiftySeven {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        if (newInterval.length == 0 ) {
            return intervals;
        }
        if (intervals.length == 0) {
            int[][] result  = Arrays.copyOf(intervals, intervals.length + 1);
            result[0] = newInterval;
            /*for (int i = 0; i < newInterval.length; i++) {
                result[1][i] = newInterval[i];
            }*/
            return result;
        }
        int[][] newArray = Arrays.copyOf(intervals, intervals.length + 1);
        newArray[intervals.length] = newInterval;
        return merge(newArray);
    }

    private int[][] merge(int[][] intervals) {
        // 先按照区间起始位置排序
        Arrays.sort(intervals, (v1, v2) -> v1[0] - v2[0]);
        // 遍历区间
        int[][] res = new int[intervals.length][2];
        int idx = -1;
        for (int[] interval: intervals) {
            // 如果结果数组是空的，或者当前区间的起始位置 > 结果数组中最后区间的终止位置，
            // 则不合并，直接将当前区间加入结果数组。
            if (idx == -1 || interval[0] > res[idx][1]) {
                res[++idx] = interval;
            } else {
                // 反之将当前区间合并至结果数组的最后区间
                res[idx][1] = Math.max(res[idx][1], interval[1]);
            }
        }
        return Arrays.copyOf(res, idx + 1);
    }
}
