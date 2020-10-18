package learn.leetcode.array;

/**
 * 这里有 n 个航班，它们分别从 1 到 n 进行编号。 
 * 
 *  我们这儿有一份航班预订表，表中第 i 条预订记录 bookings[i] = [i, j, k] 意味着我们在从 i 到 j 的每个航班上预订了 k 个座
 * 位。 
 * 
 *  请你返回一个长度为 n 的数组 answer，按航班编号顺序返回每个航班上预订的座位数。 
 * 
 *  
 * 
 *  示例： 
 * 
 *  输入：bookings = [[1,2,10],[2,3,20],[2,5,25]], n = 5
 * 输出：[10,55,45,25,25]
 *  
 * 
 *  
 * 
 *  提示： 
 * 
 *  
 *  1 <= bookings.length <= 20000 
 *  1 <= bookings[i][0] <= bookings[i][1] <= n <= 20000 
 *  1 <= bookings[i][2] <= 10000 
 *  
 *  Related Topics 数组 数学 
 */
public class OneThousandOneHundredNine {

    /**
     * 初始航班预订数量数组 answer = [0,0,0,0,0]，差分数组d = [0,0,0,0,0]
     * 当遍历到bookings[0] = [1,2,10]的时候，差分数组第1位加10，第3位减10，变成d = [10,0,-10,0,0]
     * 同理，当遍历到bookings[1] = [2,3,20]的时候，差分数组变成d = [10,20,-10,-20,0]
     * 当遍历到bookings[2] = [2,5,25]的时候，差分数组变成d = [10,45,-10,-20,0]，第6位要减25，我们也不需要了
     * 最后计算answer数组的值，answer[0] = d[0] = 10，answer[1] = d[1] + answer[0] = 45 + 10 = 55，answer[2] = d[2] + answer[1] = -10 + 55 = 45
     * 最最后发现，只申请一个数组表示d[]和answer[]就可以了
     */
    public int[] corpFlightBookings(int[][] bookings, int n) {
        int[] answer = new int[5];
        // 遍历 bookings 计算航班 i + 1 对航班 i 变化的预定数
        for (int[] b : bookings) {
            answer[b[0] - 1] += b[2];
            // 防止数组越界
            if (b[1] < n) {
                // 减少的预定数量
                answer[b[1]] -= b[2];
            }
        }
        for (int i = 1; i < n; i++) {
            answer[i] += answer[i-1];
        }
        return answer;
    }
}
