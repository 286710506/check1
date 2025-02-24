package com.textcheck;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 基于Jaccard相似度的文本相似度计算实现
 */
public class JaccardSimilarity implements SimilarityCalculator {

    @Override
    public double calculate(String text1, String text2) {
        if (text1 == null || text2 == null) {
            return 0.0;
        }

        // 处理空字符串的特殊情况
        if (text1.isEmpty() && text2.isEmpty()) {
            return 1.0;
        }
        if (text1.isEmpty() || text2.isEmpty()) {
            return 0.0;
        }

        // 将文本转换为字符组和词组的混合集合
        Set<String> set1 = getTokenSets(text1);
        Set<String> set2 = getTokenSets(text2);

        // 如果两个集合都为空，返回1.0
        if (set1.isEmpty() && set2.isEmpty()) {
            return 1.0;
        }

        // 如果其中一个集合为空，返回0.0
        if (set1.isEmpty() || set2.isEmpty()) {
            return 0.0;
        }

        // 计算交集大小
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        double intersectionSize = intersection.size();

        // 计算并集大小
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        double unionSize = union.size();

        // 计算基础Jaccard相似度
        double baseJaccard = intersectionSize / unionSize;

        // 对于长文本，考虑字符级别的相似度
        if (text1.length() > 10 || text2.length() > 10) {
            // 计算字符级别的Jaccard相似度
            Set<Character> charSet1 = getCharacterSet(text1);
            Set<Character> charSet2 = getCharacterSet(text2);
            
            Set<Character> charIntersection = new HashSet<>(charSet1);
            charIntersection.retainAll(charSet2);
            
            Set<Character> charUnion = new HashSet<>(charSet1);
            charUnion.addAll(charSet2);
            
            double charJaccard = (double) charIntersection.size() / charUnion.size();
            
            // 综合考虑词级别和字符级别的相似度
            return (baseJaccard * 0.6 + charJaccard * 0.4);
        }

        return baseJaccard;
    }

    /**
     * 将文本转换为标记集合，包括单字和词组
     */
    private Set<String> getTokenSets(String text) {
        Set<String> tokens = new HashSet<>();
        List<String> words = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();

        // 预处理文本
        text = text.toLowerCase().trim();

        // 分词处理
        for (char c : text.toCharArray()) {
            if (isChinese(c)) {
                // 处理之前累积的非中文词
                if (currentWord.length() > 0) {
                    words.add(currentWord.toString());
                    currentWord.setLength(0);
                }
                words.add(String.valueOf(c));
            } else if (Character.isSpaceChar(c)) {
                if (currentWord.length() > 0) {
                    words.add(currentWord.toString());
                    currentWord.setLength(0);
                }
            } else if (Character.isLetterOrDigit(c)) {
                currentWord.append(c);
            }
        }

        if (currentWord.length() > 0) {
            words.add(currentWord.toString());
        }

        // 添加单字标记
        tokens.addAll(words);

        // 添加相邻字符组合（生成2-gram）
        for (int i = 0; i < words.size() - 1; i++) {
            tokens.add(words.get(i) + words.get(i + 1));
        }

        return tokens;
    }

    /**
     * 获取文本的字符集合
     */
    private Set<Character> getCharacterSet(String text) {
        Set<Character> charSet = new HashSet<>();
        for (char c : text.toLowerCase().toCharArray()) {
            if (Character.isLetterOrDigit(c) || isChinese(c)) {
                charSet.add(c);
            }
        }
        return charSet;
    }

    /**
     * 判断是否为中文字符
     */
    private boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B;
    }
}