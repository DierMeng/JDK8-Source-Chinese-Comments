package learn.leetcode.string;

/**
 * 给定一个字符串 s，你可以通过在字符串前面添加字符将其转换为回文串。找到并返回可以用这种方式转换的最短回文串。 
 * 
 *  示例 1: 
 * 
 *  输入: "aacecaaa"
 * 输出: "aaacecaaa"
 *  
 * 
 *  示例 2: 
 * 
 *  输入: "abcd"
 * 输出: "dcbabcd" 
 *  Related Topics 字符串 
 */
public class TwoHundredFourteen {

    /**
     * 先判断整个字符串是不是回文串，如果是的话，就直接将当前字符串返回。不是的话，进行下一步。
     *
     * 判断去掉末尾 1 个字符的字符串是不是回文串，如果是的话，就将末尾的 1 个字符加到原字符串的头部返回。不是的话，进行下一步。
     *
     * 判断去掉末尾 2 个字符的字符串是不是回文串，如果是的话，就将末尾的 2 个字符倒置后加到原字符串的头部返回。不是的话，进行下一步。
     *
     * 判断去掉末尾 3 个字符的字符串是不是回文串，如果是的话，就将末尾的 3 个字符倒置后加到原字符串的头部返回。不是的话，进行下一步。
     *
     *
     * 直到判断去掉末尾的 n - 1 个字符，整个字符串剩下一个字符，把末尾的 n - 1 个字符倒置后加到原字符串的头部返回。
     */
    public String shortestPalindrome1(String s) {
        int end = s.length() - 1;
        // 找到回文串的结尾, 用 end 标记
        for (; end > 0; end--) {
            if (isPalindromic(s, 0, end)) {
                break;
            }
        }
        // 将末尾的几个倒置然后加到原字符串开头
        return new StringBuilder(s.substring(end + 1)).reverse() + s;
    }

    /**
     * 双指针
     */
    public String shortestPalindrome2(String s) {
        int i = 0;
        int j = s.length() - 1;
        while (j >= 0) {
            if (s.charAt(i) == s.charAt(j)) {
                i++;
            }
            j--;
        }
        // 此时代表整个字符串是回文串
        if (i == s.length()) {
            return s;
        }
        // 后缀
        String suffix = s.substring(i);
        // 后缀倒置
        String reverse = new StringBuilder(suffix).reverse().toString();
        // 加到开头
        return reverse + shortestPalindrome2(s.substring(0, i) + suffix);
    }

    /**
     * 将原始字符串逆序，然后比较对应的子串即可判断是否是回文串。
     */
    public String shortestPalindrome3(String s) {
        String reverse = new StringBuilder(s).reverse().toString();
        int n = s.length();
        int i = 0;
        for (; i < n; i++) {
            if (s.substring(0, n- 1).equals(reverse.substring(i))) {
                break;
            }
        }
        return new StringBuilder(s.substring(n - i)).reverse().toString();
    }

    /**
     * 滚动哈希
     */
    public String shortestPalindrome4(String s) {
        int n = s.length();
        int position = -1;
        // 基数
        int bit = 26;
        // 为了方便计算倒置字符串的 hash 值
        int pow = 1;
        char[] c = s.toCharArray();
        int hash1 = 0;
        int hash2 = 0;
        int mod = 1000000;
        String rev = new StringBuilder(s).reverse().toString();
        for (int i = 0; i < n; i++, pow = pow * bit) {
            hash1 = (hash1 * bit + (c[i] - 'a' + 1)) % mod;
            hash2 = (hash2 + (c[i] - 'a' + 1) * pow) % mod;
            if (hash1 == hash2) {
                if (s.substring(0, i + 1).equals(rev.substring(n - i - 1))) {
                    position = i;
                }
            }
        }
        return new StringBuilder(s.substring(position + 1)).reverse().toString();
    }

    /**
     * KMP
     */
    public String shortestPalindrome5(String s) {
        String ss = s + '#' + new StringBuilder(s).reverse();
        int max = getLastNext(ss);
        return new StringBuilder(s.substring(max)).reverse() + s;
    }

    /**
     * 返回 next 数组的最后一个值
     */
    public int getLastNext(String s) {
        int n = s.length();
        char[] c = s.toCharArray();
        int[] next = new int[n + 1];
        next[0] = -1;
        next[1] = 0;
        int k = 0;
        int i = 2;
        while (i <= n) {
            if (k == -1 || c[i - 1] == c[k]) {
                next[i] = k + 1;
                k++;
                i++;
            } else {
                k = next[k];
            }
        }
        return next[n];
    }

    // 判断是否是回文串, 传入字符串的范围
    public boolean isPalindromic(String s, int start, int end) {
        char[] c = s.toCharArray();
        while (start < end) {
            if (c[start] != c[end]) {
                return false;
            }
            start++;
            end--;
        }
        return true;
    }
}
