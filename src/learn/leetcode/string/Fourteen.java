package learn.leetcode.string;

/**
 * 编写一个函数来查找字符串数组中的最长公共前缀。 
 * 
 *  如果不存在公共前缀，返回空字符串 ""。 
 * 
 *  示例 1: 
 * 
 *  输入: ["flower","flow","flight"]
 * 输出: "fl"
 *  
 * 
 *  示例 2: 
 * 
 *  输入: ["dog","racecar","car"]
 * 输出: ""
 * 解释: 输入不存在公共前缀。
 *  
 * 
 *  说明: 
 * 
 *  所有输入只包含小写字母 a-z 。 
 *  Related Topics 字符串 
 */
public class Fourteen {

    /**
     * 分治
     */
    public String longestCommonPrefix(String[] strs) {
        if (null == strs || strs.length == 0) {
            return "";
        } else {
            return longestCommonPrefix(strs, 0, strs.length - 1);
        }
    }

    public String longestCommonPrefix(String[] strs, int start, int end) {
        if (start == end) {
            return strs[start];
        } else {
            int middle = (end - start) / 2 + start;
            String lcpLeft = longestCommonPrefix(strs, start, middle);
            String lcpRight = longestCommonPrefix(strs, middle + 1, end);
            return commonPrefix(lcpLeft, lcpRight);
        }
    }

    public String commonPrefix(String lcpLeft, String lcpRight) {
        int minLength = Math.min(lcpLeft.length(), lcpRight.length());
        for (int i = 0; i < minLength; i++) {
            if (lcpLeft.charAt(i) != lcpRight.charAt(i)) {
                return lcpLeft.substring(0, i);
            }
        }
        return lcpLeft.substring(0, minLength);
    }

    /**
     * 二分
     */
    public String longestCommonPrefix2(String[] strs) {
        if (null == strs || strs.length == 0) {
            return "";
        }
        int minLength = Integer.MAX_VALUE;
        for (String str : strs) {
            minLength = Math.min(minLength, str.length());
        }
        int low = 0;
        int high = strs.length;
        while (low < high) {
            int middle = low + (high - low) / 2;
            if (isCommonPrefix(strs, middle)) {
                low = middle;
            } else {
                high = middle - 1;
            }
        }
        return strs[0].substring(0, low);
    }

    public Boolean isCommonPrefix(String[] strs, int length) {
        String str = strs[0].substring(0, length);
        int count = strs.length;
        for (int i = 1; i < count; i++) {
            String tempStr = strs[i];
            for (int j = 0; j < tempStr.length(); j++) {
                if (str.charAt(j) != tempStr.charAt(j)) {
                    return false;
                }
            }
        }
        return true;
    }

}





























