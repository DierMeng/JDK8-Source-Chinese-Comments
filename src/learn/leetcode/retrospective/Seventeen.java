package learn.leetcode.retrospective;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 给定一个仅包含数字 2-9 的字符串，返回所有它能表示的字母组合。 
 * 
 *  给出数字到字母的映射如下（与电话按键相同）。注意 1 不对应任何字母。 
 * 
 *  
 * 
 *  示例: 
 * 
 *  输入："23"
 * 输出：["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"].
 *  
 * 
 *  说明: 
 * 尽管上面的答案是按字典序排列的，但是你可以任意选择答案输出的顺序。 
 *  Related Topics 字符串 回溯算法 
 */
public class Seventeen {
    Map<String, String> phone = new HashMap<String, String>() {{
        put("2", "abc");
        put("3", "def");
        put("4", "ghi");
        put("5", "jkl");
        put("6", "mno");
        put("7", "pqrs");
        put("8", "tuv");
        put("9", "wxyz");
    }};

    List<String> result = new ArrayList<>();

    public List<String> letterCombinations(String digits) {
        if (digits.length() != 0) {
            backtrack("", digits);
        }
        return result;
    }

    public void backtrack(String combination, String nextDigits) {
        if (nextDigits.length() == 0) {
            result.add(combination);
        } else {
            String digit = nextDigits.substring(0, 1);
            String letters = phone.get(digit);
            for (int i = 0; i < letters.length(); i++) {
                String letter = phone.get(digit).substring(i, i + 1);
                backtrack(combination + letter, nextDigits.substring(1));
            }
        }
    }
}
