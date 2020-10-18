package learn.leetcode.sword.finger.offer;

/**
 * 请设计一个函数，用来判断在一个矩阵中是否存在一条包含某字符串所有字符的路径。路径可以从矩阵中的任意一格开始，每一步可以在矩阵中向左、右、上、下移动一格。如果
 * 一条路径经过了矩阵的某一格，那么该路径不能再次进入该格子。例如，在下面的3×4的矩阵中包含一条字符串“bfce”的路径（路径中的字母用加粗标出）。 
 * 
 *  [["a","b","c","e"], 
 * ["s","f","c","s"], 
 * ["a","d","e","e"]] 
 * 
 *  但矩阵中不包含字符串“abfb”的路径，因为字符串的第一个字符b占据了矩阵中的第一行第二个格子之后，路径不能再次进入这个格子。 
 * 
 *  
 * 
 *  示例 1： 
 * 
 *  输入：board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "A
 * BCCED"
 * 输出：true
 *  
 * 
 *  示例 2： 
 * 
 *  输入：board = [["a","b"],["c","d"]], word = "abcd"
 * 输出：false
 *  
 * 
 *  提示： 
 * 
 *  
 *  1 <= board.length <= 200 
 *  1 <= board[i].length <= 200 
 *  
 * 
 *  注意：本题与主站 79 题相同：https:leetcode-cn.com/problems/word-search/ 
 *  Related Topics 深度优先搜索 
 */
public class Twelve {
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
