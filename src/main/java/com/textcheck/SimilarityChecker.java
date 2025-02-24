package com.textcheck;

/**
 * 文本相似度检查程序
 */
public class SimilarityChecker {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("用法：");
            System.out.println("直接文本比较：");
            System.out.println("  <text1> <text2> [-a algorithm]");
            System.out.println("文件比较：");
            System.out.println("  -f <file1> <file2> [-a algorithm]");
            System.out.println("算法选项：");
            System.out.println("  -a cosine     (默认) 使用余弦相似度");
            System.out.println("  -a levenshtein 使用编辑距离");
            System.out.println("  -a jaccard    使用Jaccard相似度");
            return;
        }

        String algorithm = "cosine";  // 默认使用余弦相似度
        String text1 = "";
        String text2 = "";
        boolean isFile = false;

        // 解析命令行参数
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-f":
                    isFile = true;
                    if (i + 2 < args.length) {
                        text1 = args[++i];
                        text2 = args[++i];
                    }
                    break;
                case "-a":
                    if (i + 1 < args.length) {
                        algorithm = args[++i].toLowerCase();
                    }
                    break;
                default:
                    if (text1.isEmpty()) {
                        text1 = args[i];
                    } else if (text2.isEmpty()) {
                        text2 = args[i];
                    }
                    break;
            }
        }

        // 选择相似度算法
        SimilarityCalculator calculator;
        switch (algorithm) {
            case "levenshtein":
                calculator = new LevenshteinDistance();
                break;
            case "jaccard":
                calculator = new JaccardSimilarity();
                break;
            case "cosine":
            default:
                calculator = new CosineSimilarity();
                break;
        }

        // 如果是文件比较，读取文件内容
        if (isFile) {
            // TODO: 实现文件读取逻辑
            System.out.println("文件比较功能尚未实现");
            return;
        }

        // 计算相似度
        double similarity = calculator.calculate(text1, text2);
        
        // 格式化输出，保留两位小数
        System.out.printf("文本相似度: %.2f%%\n", similarity * 100);
    }
}