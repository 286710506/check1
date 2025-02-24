package com.textcheck;

/**
 * 文本相似度计算器接口
 */
public interface SimilarityCalculator {
    /**
     * 计算两段文本的相似度
     * @param text1 第一段文本
     * @param text2 第二段文本
     * @return 相似度（0-1之间的值，1表示完全相同，0表示完全不同）
     */
    double calculate(String text1, String text2);
}