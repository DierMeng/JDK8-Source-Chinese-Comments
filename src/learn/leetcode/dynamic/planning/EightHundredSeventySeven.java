package learn.leetcode.dynamic.planning;

/**
 * 亚历克斯和李用几堆石子在做游戏。偶数堆石子排成一行，每堆都有正整数颗石子 piles[i] 。
 *
 *  游戏以谁手中的石子最多来决出胜负。石子的总数是奇数，所以没有平局。
 *
 *  亚历克斯和李轮流进行，亚历克斯先开始。 每回合，玩家从行的开始或结束处取走整堆石头。 这种情况一直持续到没有更多的石子堆为止，此时手中石子最多的玩家获胜。
 *
 *
 *  假设亚历克斯和李都发挥出最佳水平，当亚历克斯赢得比赛时返回 true ，当李赢得比赛时返回 false 。
 *
 *
 *
 *  示例：
 *
 *  输入：[5,3,4,5]
 * 输出：true
 * 解释：
 * 亚历克斯先开始，只能拿前 5 颗或后 5 颗石子 。
 * 假设他取了前 5 颗，这一行就变成了 [3,4,5] 。
 * 如果李拿走前 3 颗，那么剩下的是 [4,5]，亚历克斯拿走后 5 颗赢得 10 分。
 * 如果李拿走后 5 颗，那么剩下的是 [3,4]，亚历克斯拿走后 4 颗赢得 9 分。
 * 这表明，取前 5 颗石子对亚历克斯来说是一个胜利的举动，所以我们返回 true 。
 *
 *
 *
 *
 *  提示：
 *
 *
 *  2 <= piles.length <= 500
 *  piles.length 是偶数。
 *  1 <= piles[i] <= 500
 *  sum(piles) 是奇数。
 *
 *  Related Topics 极小化极大 数学 动态规划
 */
public class EightHundredSeventySeven {

    class Pair {
        int first;
        int second;

        public Pair(int first, int second) {
            this.first = first;
            this.second = second;
        }
    }

    public boolean stoneGame(int[] piles) {
        int length = piles.length;
        // 初始化 dp 数组
        Pair[][] dp = new Pair[length][length];
        for (int i =0; i < length; i++) {
            for (int j = i; j < length; j++) {
                dp[i][j] = new Pair(0, 0);
            }
        }
        // base case
        for (int i = 0; i < length;) {
            dp[i][i].first = piles[i];
            dp[i][i].second = 0;
        }
        for (int l = 2; l < length; l++) {
            for (int i = 0; i <= length - l; i++) {
                int j = l + i - 1;
                // 先手选择最左边或最右边的分数
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[] piles = {1,2,3,4,5,6,7,8,9,10};
        for (int l = 2; l <= piles.length; l++) {
            for (int i = 0; i <= piles.length - l; i++) {
                System.out.println(piles[i]+"--"+(piles[l+i-1]));
            }
        }
    }



}
