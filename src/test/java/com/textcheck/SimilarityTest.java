package com.textcheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class SimilarityTest {

    private final SimilarityCalculator cosine = new CosineSimilarity();
    private final SimilarityCalculator levenshtein = new LevenshteinDistance();

    @Test
    public void testCosineSimilarity() {
        // 测试完全相同的文本
        assertEquals(1.0, cosine.calculate("这是一个测试", "这是一个测试"), 0.001);
        
        // 测试完全不同的文本
        assertTrue(cosine.calculate("这是一个测试", "完全不同的文本") < 0.3);
        
        // 测试部分相似的文本
        double similarity = cosine.calculate("我喜欢编程", "我很喜欢写程序");
        assertTrue(similarity > 0.3 && similarity < 0.8);
        
        // 测试空字符串
        assertEquals(0.0, cosine.calculate("", "测试文本"), 0.001);
        assertEquals(1.0, cosine.calculate("", ""), 0.001);
        
        // 测试null值
        assertEquals(0.0, cosine.calculate(null, "测试文本"), 0.001);
        assertEquals(0.0, cosine.calculate("测试文本", null), 0.001);
        assertEquals(0.0, cosine.calculate(null, null), 0.001);
    }

    @Test
    public void testLevenshteinDistance() {
        // 测试完全相同的文本
        assertEquals(1.0, levenshtein.calculate("这是一个测试", "这是一个测试"), 0.001,
                "完全相同的文本应该返回1.0");
        
        // 测试一个字符不同的文本
        double oneDiff = levenshtein.calculate("这是一个测试", "这是一个试验");
        assertTrue(oneDiff > 0.6, 
                "一个字符不同的相似度应该大于0.6，实际值：" + oneDiff);
        
        // 测试部分不同的文本
        double partialDiff = levenshtein.calculate("软件工程师", "软件测试员");
        assertTrue(partialDiff > 0.4 && partialDiff < 0.8,
                "部分不同的文本相似度应该在0.4-0.8之间，实际值：" + partialDiff);
        
        // 测试完全不同的文本
        double different = levenshtein.calculate("这是一个测试", "完全不同文本");
        assertTrue(different < 0.4, 
                "完全不同文本的相似度应该小于0.4，实际值：" + different);
        
        // 测试空字符串
        assertEquals(0.0, levenshtein.calculate("", "测试文本"), 0.001,
                "空字符串与非空字符串的相似度应该为0");
        assertEquals(1.0, levenshtein.calculate("", ""), 0.001,
                "两个空字符串的相似度应该为1");
        
        // 测试null值
        assertEquals(0.0, levenshtein.calculate(null, "测试文本"), 0.001,
                "null值与非null值的相似度应该为0");
        assertEquals(0.0, levenshtein.calculate("测试文本", null), 0.001,
                "非null值与null值的相似度应该为0");
        assertEquals(0.0, levenshtein.calculate(null, null), 0.001,
                "两个null值的相似度应该为0");
    }

    @Test
    public void testChineseTextComparison() {
        String text1 = "中国是一个伟大的国家，有着悠久的历史文化。";
        String text2 = "中国是一个伟大的国家，具有深厚的历史文化。";
        
        // 测试余弦相似度
        double cosineSim = cosine.calculate(text1, text2);
        assertTrue(cosineSim > 0.7, 
                "余弦相似度应该大于0.7，实际值：" + cosineSim);
        
        // 测试编辑距离
        double levenshteinSim = levenshtein.calculate(text1, text2);
        assertTrue(levenshteinSim > 0.7, 
                "编辑距离相似度应该大于0.7，实际值：" + levenshteinSim);
        
        // 测试部分相似的短文本
        String text3 = "软件开发";
        String text4 = "软件测试";
        double shortTextSim = levenshtein.calculate(text3, text4);
        assertTrue(shortTextSim > 0.4 && shortTextSim < 0.8,
                "部分相似短文本的相似度应该在0.4-0.8之间，实际值：" + shortTextSim);
    }
}