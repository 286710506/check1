package com.textcheck;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "similarity-check", 
        mixinStandardHelpOptions = true, 
        version = "1.0",
        description = "检查两段文本的相似度")
public class SimilarityChecker implements Callable<Integer> {

    @Option(names = {"-a", "--algorithm"}, 
            description = "选择算法: cosine (余弦相似度) 或 levenshtein (编辑距离)", 
            defaultValue = "cosine")
    private String algorithm;

    @Option(names = {"-f", "--file"}, 
            description = "从文件读取文本", 
            required = false)
    private boolean fromFile;

    @Parameters(index = "0", 
            description = "第一段文本或文件路径")
    private String text1;

    @Parameters(index = "1", 
            description = "第二段文本或文件路径")
    private String text2;

    @Override
    public Integer call() throws Exception {
        String content1 = fromFile ? readFile(text1) : text1;
        String content2 = fromFile ? readFile(text2) : text2;

        SimilarityCalculator calculator;
        if ("cosine".equalsIgnoreCase(algorithm)) {
            calculator = new CosineSimilarity();
        } else if ("levenshtein".equalsIgnoreCase(algorithm)) {
            calculator = new LevenshteinDistance();
        } else {
            System.err.println("不支持的算法: " + algorithm);
            return 1;
        }

        double similarity = calculator.calculate(content1, content2);
        System.out.printf("文本相似度: %.2f%%%n", similarity * 100);
        return 0;
    }

    private String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new SimilarityChecker()).execute(args);
        System.exit(exitCode);
    }
}