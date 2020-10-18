package learn.leetcode.retrospective;

/**
 * 给定一个二维网格和一个单词，找出该单词是否存在于网格中。
 *
 *  单词必须按照字母顺序，通过相邻的单元格内的字母构成，其中“相邻”单元格是那些水平相邻或垂直相邻的单元格。同一个单元格内的字母不允许被重复使用。
 *  示例:
 *
 *  board =
 * [
 *   ['A','B','C','E'],
 *   ['S','F','C','S'],
 *   ['A','D','E','E']
 * ]
 *
 * 给定 word = "ABCCED", 返回 true
 * 给定 word = "SEE", 返回 true
 * 给定 word = "ABCB", 返回 false
 *  提示：
 *
 *
 *  board 和 word 中只包含大写和小写英文字母。
 *  1 <= board.length <= 200
 *  1 <= board[i].length <= 200
 *  1 <= word.length <= 10^3
 *
 *  Related Topics 数组 回溯算法
 */
public class SeventyNine {
    public boolean exist(char[][] board, String word) {
        char[] words = word.toCharArray();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (dfs(board, words, i, j, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param board,
     * @param words
     * @param i
     * @param j
     * @param index 查找到字符串 word 的第几个字符
     * @return boolean
     */
    private boolean dfs(char[][] board, char[] words, int i, int j, int index) {

        // 边界的判断，如果越界直接返回false。
        // 如果这个字符不等于 board[i][j]，说明验证这个坐标路径是走不通的，直接返回 false
        if (i >= board.length || i < 0 || j >= board[0].length || j < 0 || board[i][j] != words[index]) {
            return false;
        }
        // 如果 word 的每个字符都查找完了，直接返回 true
        if (index + 1 == words.length) {
            return true;
        }
        // 把当前坐标的值保存下来，为了在最后复原
        char temp = board[i][j];
        // 修改当前坐标的值
        board[i][j] = '.';
        // 递归，沿着当前坐标的上下左右 4 个方向查找
        boolean result = dfs(board, words, i + 1, j, index + 1) // 向右
                || dfs(board, words, i - 1, j, index + 1) // 向左
                || dfs(board, words, i, j + 1, index + 1) // 向下
                || dfs(board, words, i, j - 1, index + 1); // 向上
        // 递归之后再把当前的坐标复原
        board[i][j] = temp;
        return result;
    }
}
