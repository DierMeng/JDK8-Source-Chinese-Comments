package learn.leetcode.heap;

import java.util.*;

/**
 * 给定一个非空的整数数组，返回其中出现频率前 k 高的元素。 
 * 
 *  
 * 
 *  示例 1: 
 * 
 *  输入: nums = [1,1,1,2,2,3], k = 2
 * 输出: [1,2]
 *  
 * 
 *  示例 2: 
 * 
 *  输入: nums = [1], k = 1
 * 输出: [1] 
 * 
 *  
 * 
 *  提示： 
 * 
 *  
 *  你可以假设给定的 k 总是合理的，且 1 ≤ k ≤ 数组中不相同的元素的个数。 
 *  你的算法的时间复杂度必须优于 O(n log n) , n 是数组的大小。 
 *  题目数据保证答案唯一，换句话说，数组中前 k 个高频元素的集合是唯一的。 
 *  你可以按任意顺序返回答案。 
 *  
 *  Related Topics 堆 哈希表 
 */
public class ThreeHundredFortySeven {
    /**
     * 借助 哈希表 来建立数字和其出现次数的映射，遍历一遍数组统计元素的频率
     * 维护一个元素数目为 k 的最小堆
     * 每次都将新的元素与堆顶元素（堆中频率最小的元素）进行比较
     * 如果新的元素的频率比堆顶端的元素大，则弹出堆顶端的元素，将新的元素添加进堆中
     * 最终，堆中的 k 个元素即为前 k 个高频元素
     */
    public int[] topKFrequent(int[] nums, int k) {
        int[] res = new int[k];
        Map<Integer, Integer> map = new HashMap<>(nums.length);
        for (int num : nums) {
            if (map.containsKey(num)) {
                map.put(num, map.get(num) + 1);
            } else {
                map.put(num, 1);
            }
        }
        Queue<Integer> priorityQueue = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return map.get(o1) - map.get(o2);
            }
        });
        for (Integer key : map.keySet()) {
            if (priorityQueue.size() < k) {
                priorityQueue.add(key);
            } else if (map.get(key) > map.get(priorityQueue.peek())) {
                priorityQueue.remove();
                priorityQueue.add(key);
            }
        }
        List<Integer> result = new ArrayList<>();
        while (!priorityQueue.isEmpty()) {
            result.add(priorityQueue.remove());
        }
        for (int i = 0; i < result.size(); i++) {
            res[i] = result.get(i);
        }
        return res;
    }
}
