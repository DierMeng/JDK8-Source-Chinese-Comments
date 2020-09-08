package learn.leetcode.string;

/**
 * 给定一个字符串，你需要反转字符串中每个单词的字符顺序，同时仍保留空格和单词的初始顺序。 
 * 
 *  
 * 
 *  示例： 
 * 
 *  输入："Let's take LeetCode contest"
 * 输出："s'teL ekat edoCteeL tsetnoc"
 *  
 * 
 *  
 * 
 *  提示： 
 * 
 *  
 *  在字符串中，每个单词由单个空格分隔，并且字符串中不会有任何额外的空格。 
 *  
 *  Related Topics 字符串 
 */
public class FiveHundredFiftySeven {
    public String reverseWords(String s) {
        StringBuffer result = new StringBuffer();
        int length = s.length();
        int i = 0;
        while (i < length) {
            int start = i;
            while (i < length && s.charAt(i) != ' ') {
                i++;
            }
            for (int p = start; p < i; p++) {
                result.append(s.charAt(start + i - 1 - p));
            }
            while (i < length && s.charAt(i) == ' ') {
                i++;
                result.append(' ');
            }
        }
        return result.toString();
    }
}
