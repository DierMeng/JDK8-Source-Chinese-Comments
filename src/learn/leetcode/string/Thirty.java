package learn.leetcode.string;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 给定一个字符串 s 和一些长度相同的单词 words。找出 s 中恰好可以由 words 中所有单词串联形成的子串的起始位置。 
 * 
 *  注意子串要与 words 中的单词完全匹配，中间不能有其他字符，但不需要考虑 words 中单词串联的顺序。 
 * 
 *  
 * 
 *  示例 1： 
 * 
 *  输入：
 *   s = "barfoothefoobarman",
 *   words = ["foo","bar"]
 * 输出：[0,9]
 * 解释：
 * 从索引 0 和 9 开始的子串分别是 "barfoo" 和 "foobar" 。
 * 输出的顺序不重要, [9,0] 也是有效答案。
 *  
 * 
 *  示例 2： 
 * 
 *  输入：
 *   s = "wordgoodgoodgoodbestword",
 *   words = ["word","good","best","word"]
 * 输出：[]
 *  
 *  Related Topics 哈希表 双指针 字符串 
 */
public class Thirty {
    public List<Integer> findSubstring(String s, String[] words) {
        if(s == null || "".equals(s) || words == null || words.length == 0) {
            return new ArrayList<>();
        }
        // 将每个单词以及出现的频率记录到 map 中
        Map<String, Integer> wordsMap = new HashMap<>();
        for(String str : words) {
            if(wordsMap.containsKey(str)) {
                wordsMap.put(str, wordsMap.get(str) + 1);
            } else {
                wordsMap.put(str, 1);
            }
        }
        List<Integer> res = new ArrayList<>();
        // words 中一个单词的长度，以及 words 的总长度
        int oneordSize = words[0].length();
        int allWordSize = words.length * oneordSize;
        // 遍历整个字符串，注意循环的结束条件
        for(int i = 0; i < s.length() - allWordSize + 1; i++) {
            // 每次取 allWordSize 长度的子串
            String tmp = s.substring(i, i + allWordSize);
            HashMap<String, Integer> d = new HashMap<>(wordsMap);
            // 将子串和临时 map 进行比较
            for(int j = 0; j < tmp.length(); j += oneordSize) {
                // 从子串 tmp 中取出 oneordSize 长度的子串，看是否出现在临时 map 中
                // 如果是就将临时 map 记录的频率 -1，如果不在就跳出循环
                String key = tmp.substring(j, j + oneordSize);
                if(d.containsKey(key)) {
                    d.put(key, d.get(key) - 1);
                    if(d.get(key) == 0) {
                        d.remove(key);
                    }
                } else {
                    break;
                }
            }
            // 当内层循环遍历完后，如果临时 map 为空则表示全部匹配上了
            // 记录数组的下标
            if(d.size() == 0) {
                res.add(i);
            }
        }
        return res;
    }
    
}
