package com.textcheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class SimilarityTest {

    private final SimilarityCalculator cosine = new CosineSimilarity();
    private final SimilarityCalculator levenshtein = new LevenshteinDistance();
    private final SimilarityCalculator jaccard = new JaccardSimilarity();

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
    public void testJaccardSimilarity() {
        // 测试完全相同的文本
        assertEquals(1.0, jaccard.calculate("这是一个测试", "这是一个测试"), 0.001,
                "完全相同的文本应该返回1.0");

        // 测试部分重叠的文本
        double partialOverlap = jaccard.calculate("我喜欢编程和阅读", "我喜欢阅读和写作");
        assertTrue(partialOverlap > 0.3 && partialOverlap < 0.7,
                "部分重叠文本的相似度应该在0.3-0.7之间，实际值：" + partialOverlap);

        // 测试少量重叠的文本
        double smallOverlap = jaccard.calculate("今天天气真好", "今天心情不错");
        assertTrue(smallOverlap > 0.1 && smallOverlap < 0.5,
                "少量重叠文本的相似度应该在0.1-0.5之间，实际值：" + smallOverlap);

        // 测试完全不同的文本
        double noDiff = jaccard.calculate("这是一个测试", "完全不同的内容");
        assertTrue(noDiff < 0.1,
                "完全不同文本的相似度应该接近0，实际值：" + noDiff);

        // 测试空字符串
        assertEquals(0.0, jaccard.calculate("", "测试文本"), 0.001,
                "空字符串与非空字符串的相似度应该为0");
        assertEquals(1.0, jaccard.calculate("", ""), 0.001,
                "两个空字符串的相似度应该为1");

        // 测试null值
        assertEquals(0.0, jaccard.calculate(null, "测试文本"), 0.001,
                "null值与非null值的相似度应该为0");
        assertEquals(0.0, jaccard.calculate("测试文本", null), 0.001,
                "非null值与null值的相似度应该为0");
        assertEquals(0.0, jaccard.calculate(null, null), 0.001,
                "两个null值的相似度应该为0");
    }

    @Test
    public void testChineseTextComparison() {
        String text1 = "中国是一个伟大的国家，有着悠久的历史文化。";
        String text2 = "中国是一个伟大的国家，具有深厚的历史文化。";
        
        // 测试所有算法处理中文文本的效果
        double cosineSim = cosine.calculate(text1, text2);
        assertTrue(cosineSim > 0.7, 
                "余弦相似度应该大于0.7，实际值：" + cosineSim);
        
        double levenshteinSim = levenshtein.calculate(text1, text2);
        assertTrue(levenshteinSim > 0.7, 
                "编辑距离相似度应该大于0.7，实际值：" + levenshteinSim);
        
        double jaccardSim = jaccard.calculate(text1, text2);
        assertTrue(jaccardSim > 0.5,  // 调整期望值
                "Jaccard相似度应该大于0.5，实际值：" + jaccardSim);
        
        // 测试短文本
        String text3 = "软件开发";
        String text4 = "软件测试";
        
        double shortTextSim = jaccard.calculate(text3, text4);
        assertTrue(shortTextSim > 0.2 && shortTextSim < 0.6,
                "短文本的Jaccard相似度应该在0.2-0.6之间，实际值：" + shortTextSim);
    }
}