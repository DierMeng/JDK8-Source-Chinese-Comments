package learn.leetcode.retrospective;

import java.util.ArrayList;
import java.util.List;

/**
 * n 皇后问题研究的是如何将 n 个皇后放置在 n×n 的棋盘上，并且使皇后彼此之间不能相互攻击。 
 * 
 *  
 * 
 *  上图为 8 皇后问题的一种解法。 
 * 
 *  给定一个整数 n，返回 n 皇后不同的解决方案的数量。 
 * 
 *  示例: 
 * 
 *  输入: 4
 * 输出: 2
 * 解释: 4 皇后问题存在如下两个不同的解法。
 * [
 * [".Q..",  解法 1
 *  "...Q",
 *  "Q...",
 *  "..Q."],
 * 
 * ["..Q.",  解法 2
 *  "Q...",
 *  "...Q",
 *  ".Q.."]
 * ]
 *  
 * 
 *  
 * 
 *  提示： 
 * 
 *  
 *  皇后，是国际象棋中的棋子，意味着国王的妻子。皇后只做一件事，那就是“吃子”。当她遇见可以吃的棋子时，就迅速冲上去吃掉棋子。当然，她横、竖、斜都可走一或 N
 * -1 步，可进可退。（引用自 百度百科 - 皇后 ） 
 *  
 *  Related Topics 回溯算法 
 */
public class FiftyTwo {
    public int totalNQueens(int n) {
        char[][] chess = new char[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                chess[i][j] = '.';
            }
        }
        List<List<String>> result = new ArrayList<>();
        solve(result, chess, 0);
        return result.size();
    }

    private void solve(List<List<String>> result, char[][] chess, int row) {
        if (row == chess.length) {
            result.add(construct(chess));
        }
        for (int column = 0; column < chess.length; column++) {
            if (valid(chess, row, column)) {
                chess[row][column] = 'Q';
                solve(result, chess, row + 1);
                chess[row][column] = '.';
            }
        }
    }

    /**
     * 判断当前列有没有皇后,因为他是一行一行往下走的，我们只需要检查走过的行数即可，通俗一点就是判断当前坐标位置的上面有没有皇后
     */
    private boolean valid(char[][] chess, int row, int column) {
        for (int i = 0; i < row; i++) {
            if (chess[i][column] == 'Q') {
                return false;
            }
        }
        //判断当前坐标的右上角有没有皇后
        for (int i = row - 1, j = column + 1; i >= 0 && j < chess.length; i--, j++) {
            if (chess[i][j] == 'Q') {
                return false;
            }
        }
        // 判断当前坐标的左上角有没有皇后
        for (int i = row - 1, j = column - 1; i >= 0 && j >= 0; i--, j--) {
            if (chess[i][j] == 'Q') {
                return false;
            }
        }
        return true;
    }

    /**
     * 把数组转为 list
     */
    private List<String> construct(char[][] chess) {
        List<String> path = new ArrayList<>();
        for (int i = 0; i < chess.length; i++) {
            path.add(new String(chess[i]));
        }
        return path;
    }
}
