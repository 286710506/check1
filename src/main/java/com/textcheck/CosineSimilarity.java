package com.textcheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 基于余弦相似度的文本相似度计算实现
 */
public class CosineSimilarity implements SimilarityCalculator {

    @Override
    public double calculate(String text1, String text2) {
        if (text1 == null || text2 == null) {
            return 0.0;
        }
        
        // 处理空字符串的情况
        if (text1.isEmpty() && text2.isEmpty()) {
            return 1.0;
        }
        if (text1.isEmpty() || text2.isEmpty()) {
            return 0.0;
        }

        // 将文本转换为词频向量
        Map<String, Integer> vector1 = getTermFrequencyVector(text1);
        Map<String, Integer> vector2 = getTermFrequencyVector(text2);

        // 如果任一向量为空，则相似度为0
        if (vector1.isEmpty() || vector2.isEmpty()) {
            return 0.0;
        }

        // 计算分子（点积）
        double dotProduct = 0.0;
        Set<String> allTerms = new HashSet<>(vector1.keySet());
        allTerms.addAll(vector2.keySet());

        for (String term : allTerms) {
            int freq1 = vector1.getOrDefault(term, 0);
            int freq2 = vector2.getOrDefault(term, 0);
            dotProduct += freq1 * freq2;
        }

        // 计算分母（向量模长的乘积）
        double norm1 = calculateNorm(vector1);
        double norm2 = calculateNorm(vector2);

        // 避免除以零
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (norm1 * norm2);
    }

    private Map<String, Integer> getTermFrequencyVector(String text) {
        Map<String, Integer> vector = new HashMap<>();
        
        // 预处理文本：移除标点符号，转换为小写
        String processedText = text.toLowerCase()
                                 .replaceAll("[\\p{P}\\s]+", " ")
                                 .trim();
        
        // 处理中文和其他字符
        List<String> terms = new ArrayList<>();
        StringBuilder currentTerm = new StringBuilder();
        
        for (char c : processedText.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                if (currentTerm.length() > 0) {
                    terms.add(currentTerm.toString());
                    currentTerm.setLength(0);
                }
            } else if (isChinese(c)) {
                // 对于中文字符，每个字单独作为一个词
                if (currentTerm.length() > 0) {
                    terms.add(currentTerm.toString());
                    currentTerm.setLength(0);
                }
                terms.add(String.valueOf(c));
            } else {
                currentTerm.append(c);
            }
        }
        
        // 添加最后一个词
        if (currentTerm.length() > 0) {
            terms.add(currentTerm.toString());
        }
        
        // 构建词频向量
        for (String term : terms) {
            if (!term.isEmpty()) {
                vector.merge(term, 1, Integer::sum);
            }
        }
        
        return vector;
    }

    private boolean isChinese(char c) {
        return Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS;
    }

    private double calculateNorm(Map<String, Integer> vector) {
        double sumOfSquares = vector.values().stream()
                                  .mapToInt(Integer::intValue)
                                  .map(count -> count * count)
                                  .sum();
        return Math.sqrt(sumOfSquares);
    }
}