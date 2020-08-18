package learn.leetcode.stack;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * 给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串，判断字符串是否有效。
 * 有效字符串需满足：
 *  左括号必须用相同类型的右括号闭合。
 *  左括号必须以正确的顺序闭合。
 * 注意空字符串可被认为是有效字符串。
 *
 *  示例 1:
 *
 *  输入: "()"
 * 输出: true
 *
 *
 *  示例 2:
 *
 *  输入: "()[]{}"
 * 输出: true
 *
 *
 *  示例 3:
 *
 *  输入: "(]"
 * 输出: false
 *
 *
 *  示例 4:
 *
 *  输入: "([)]"
 * 输出: false
 *
 *
 *  示例 5:
 *
 *  输入: "{[]}"
 * 输出: true
 *  Related Topics 栈 字符串
 */
public class TwenTy {

    private static Map<Character, Character> bracketsMap;

    public TwenTy() {
        this.bracketsMap = new HashMap<Character, Character>();
        bracketsMap.put(')', '(');
        bracketsMap.put(']', '[');
        bracketsMap.put('}', '{');
    }

    /**
     * 官方题解思路:
     *  一个有效的括号字符串,最小单位一定是「()、[]、{}」的一种。
     *  遍历字符串，开括号进行入栈操作，闭括号进行判断操作，当遇到闭括号，栈顶弹出与当前闭括号进行匹配，如果匹配就继续执行，不匹配则不是有效的括号
     *  最后进行栈的判空操作
     *
     *  时间复杂度：O(n)，因为我们一次只遍历给定的字符串中的一个字符并在栈上进行 O(1)O(1) 的推入和弹出操作。
     *  空间复杂度：O(n)，当我们将所有的开括号都推到栈上时以及在最糟糕的情况下，我们最终要把所有括号推到栈上。例如 ((((((((((。
     */
    public boolean isValid(String s) {
        Stack<Character> stack = new Stack<Character>();
        char[] charArr = s.toCharArray();
        for (char ch : charArr) {
            if (bracketsMap.containsKey(ch)) {
                if (!stack.isEmpty()) {
                    Character c = stack.pop();
                    if (c != bracketsMap.get(ch)) {
                        return false;
                    }
                }else {
                    return false;
                }
            } else {
                stack.push(ch);
            }

        }
        return stack.isEmpty();
    }


    public static void main(String[] args) {
        System.out.println(new TwenTy().isValid("[]"));
    }
}
