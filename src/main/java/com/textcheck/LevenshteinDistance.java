package com.textcheck;

/**
 * 基于Levenshtein编辑距离的文本相似度计算实现
 */
public class LevenshteinDistance implements SimilarityCalculator {

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

        // 预处理文本：去除多余空白字符
        text1 = normalizeText(text1);
        text2 = normalizeText(text2);

        int len1 = text1.length();
        int len2 = text2.length();

        // 如果字符串完全相同
        if (text1.equals(text2)) {
            return 1.0;
        }

        // 计算长度比例因子
        double lengthRatio = (double) Math.min(len1, len2) / Math.max(len1, len2);

        // 创建距离矩阵
        double[][] dp = new double[len1 + 1][len2 + 1];

        // 初始化第一行和第一列
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        // 计算编辑距离，考虑字符类型的权重
        for (int i = 1; i <= len1; i++) {
            char c1 = text1.charAt(i - 1);
            for (int j = 1; j <= len2; j++) {
                char c2 = text2.charAt(j - 1);
                
                if (c1 == c2) {
                    // 字符完全相同
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    // 计算替换成本
                    double replaceCost = getReplaceCost(c1, c2);
                    
                    // 计算删除和插入成本
                    double deleteCost = getDeleteCost(c1);
                    double insertCost = getInsertCost(c2);
                    
                    // 取三种操作的最小值
                    dp[i][j] = Math.min(
                        Math.min(
                            dp[i - 1][j] + deleteCost,     // 删除
                            dp[i][j - 1] + insertCost      // 插入
                        ),
                        dp[i - 1][j - 1] + replaceCost     // 替换
                    );
                }
            }
        }

        // 获取最终的编辑距离
        double editDistance = dp[len1][len2];

        // 计算基础相似度
        double maxPossibleDistance = Math.max(len1, len2);
        double baseSimilarity = 1.0 - (editDistance / maxPossibleDistance);

        // 根据文本长度和特性调整相似度
        double adjustedSimilarity;
        if (Math.min(len1, len2) < 5) {
            // 短文本的相似度调整
            adjustedSimilarity = baseSimilarity * (0.7 + 0.3 * lengthRatio);
        } else if (allChinese(text1) && allChinese(text2)) {
            // 纯中文文本的相似度调整
            adjustedSimilarity = baseSimilarity * (0.9 + 0.1 * lengthRatio);
        } else {
            // 混合文本的相似度调整
            adjustedSimilarity = baseSimilarity * (0.8 + 0.2 * lengthRatio);
        }

        // 确保相似度在0到1之间
        return Math.max(0.0, Math.min(1.0, adjustedSimilarity));
    }

    private String normalizeText(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }

    private double getReplaceCost(char c1, char c2) {
        if (isChinese(c1) && isChinese(c2)) {
            return 0.8; // 降低中文字符间的替换成本
        }
        if (!isChinese(c1) && !isChinese(c2)) {
            return 0.6; // 进一步降低非中文字符的替换成本
        }
        return 1.0; // 中文和非中文字符间的替换保持较高成本
    }

    private double getDeleteCost(char c) {
        return isChinese(c) ? 1.0 : 0.8;
    }

    private double getInsertCost(char c) {
        return isChinese(c) ? 1.0 : 0.8;
    }

    private boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B;
    }

    private boolean allChinese(String text) {
        for (char c : text.toCharArray()) {
            if (!isChinese(c) && !Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }
}