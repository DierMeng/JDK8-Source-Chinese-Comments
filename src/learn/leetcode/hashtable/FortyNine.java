package learn.leetcode.hashtable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 给定一个字符串数组，将字母异位词组合在一起。字母异位词指字母相同，但排列不同的字符串。 
 * 
 *  示例: 
 * 
 *  输入: ["eat", "tea", "tan", "ate", "nat", "bat"]
 * 输出:
 * [
 *   ["ate","eat","tea"],
 *   ["nat","tan"],
 *   ["bat"]
 * ] 
 * 
 *  说明： 
 *
 *  所有输入均为小写字母。 
 *  不考虑答案输出的顺序。 
 *  
 *  Related Topics 哈希表 字符串 
 */
public class FortyNine {
    public List<List<String>> groupAnagrams(String[] strs) {
        // 考察了哈希函数的基本知识，只要 26 个即可
        // （小写字母ACSII 码 - 97 ）以后和质数的对应规则，这个数组的元素顺序无所谓
        // key 是下标，value 就是数值
        int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101};

        // key 是字符串自定义规则下的哈希值
        Map<Long, List<String>> hashMap = new HashMap<>();
        for (String s : strs) {
            long hashValue = 1;

            char[] charArray = s.toCharArray();
            for (char c : charArray) {
                hashValue = hashValue * primes[c - 'a'];
            }

            // 把单词添加到哈希值相同的分组
            if (hashMap.containsKey(hashValue)) {
                List<String> curList = hashMap.get(hashValue);
                curList.add(s);
            } else {
                List<String> newList = new ArrayList<>();
                newList.add(s);

                hashMap.put(hashValue, newList);
            }
        }
        return new ArrayList<>(hashMap.values());

    }
}
