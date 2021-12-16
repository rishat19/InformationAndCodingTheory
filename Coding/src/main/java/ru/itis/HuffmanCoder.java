package ru.itis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.*;
import java.util.*;

public class HuffmanCoder {

    private Node root;
    private final HashMap<Character, Integer> characterRateMap;
    private final HashMap<Character, String> codeMap;

    @AllArgsConstructor
    @Getter
    @Builder
    private static class Node {
        private final char value;
        private final int rate;
        private final Node left;
        private final Node right;

    }

    public HuffmanCoder() {
        characterRateMap = new HashMap<>();
        codeMap = new HashMap<>();
    }

    public void encode(String filePath, String codeFilePath) throws IOException {
        characterRateMap.clear();
        codeMap.clear();
        readFileAndCalculateCharacterRate(filePath);
        buildHuffmanTree();
        fillCodeTable(root, "");
        writeCodeFile(filePath, codeFilePath);
    }

    public void decode(String codeFilePath, String filePath) throws IOException {
        readCodeFile(codeFilePath, filePath);
    }

    private void readFileAndCalculateCharacterRate(String filePath) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filePath));
        String line;
        characterRateMap.put('\n', 0);
        while ((line = in.readLine()) != null) {
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                int rate;
                if (!characterRateMap.containsKey(c)) {
                    rate = 1;
                } else {
                    rate = characterRateMap.get(c) + 1;
                }
                characterRateMap.put(c, rate);
            }
            characterRateMap.put('\n', characterRateMap.get('\n') + 1);
        }
        in.close();
    }

    private void buildHuffmanTree() {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(Node::getRate));
        for (Map.Entry<Character, Integer> entry : characterRateMap.entrySet()) {
            priorityQueue.offer(Node.builder()
                    .value(entry.getKey())
                    .rate(entry.getValue())
                    .left(null)
                    .right(null)
                    .build());
        }
        Node left;
        Node right;
        while (!priorityQueue.isEmpty()) {
            left = priorityQueue.poll();
            if (priorityQueue.peek() != null) {
                right = priorityQueue.poll();
                root = Node.builder()
                        .value('\0')
                        .rate(left.rate + right.rate)
                        .left(left)
                        .right(right)
                        .build();
            } else {
                root = Node.builder()
                        .value('\0')
                        .rate(left.rate)
                        .left(left)
                        .right(null)
                        .build();
            }
            if (priorityQueue.peek() != null) {
                priorityQueue.offer(root);
            } else {
                break;
            }
        }
    }

    private void fillCodeTable(Node node, String code) {
        if (node != null) {
            if (!(node.left == null && node.right == null)) {
                fillCodeTable(node.left, code + '0');
                fillCodeTable(node.right, code + '1');
            } else {
                codeMap.put(node.value, code);
            }
        }
    }

    private void writeCodeFile(String filePath, String codeFilePath) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filePath));
        BufferedWriter out = new BufferedWriter(new FileWriter(codeFilePath));
        // Write code table
        for (Map.Entry<Character, String> entry : codeMap.entrySet()) {
            out.write(entry.getKey());
            out.write(entry.getValue());
            out.write('\n');
            out.flush();
        }
        // Write delimiter
        out.write("--");
        out.write('\n');
        out.flush();
        // Write code
        String line;
        while ((line = in.readLine()) != null) {
            for (int i = 0; i < line.length(); i++) {
                out.write(codeMap.get(line.charAt(i)));
                out.flush();
            }
            out.write(codeMap.get('\n'));
            out.flush();
        }
        in.close();
        out.close();
    }

    private void readCodeFile(String codeFilePath, String filePath) throws IOException {
        HashMap<String, Character> charactersMap = new HashMap<>();
        BufferedReader in = new BufferedReader(new FileReader(codeFilePath));
        BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
        // Read code table
        String line;
        boolean flag = false;
        while (!(line = in.readLine()).equals("--")) {
            if (line.length() > 0 && !flag) {
                char c = line.charAt(0);
                charactersMap.put(line.substring(1), c);
            } else if (flag) {
                charactersMap.put(line, '\n');
                flag = false;
            } else {
                flag = true;
            }
        }
        // Read code
        line = in.readLine();
        if (line != null) {
            StringBuilder code = new StringBuilder();
            for (int i = 0; i < line.length(); i++) {
                code.append(line.charAt(i));
                if (charactersMap.containsKey(code.toString())) {
                    out.write(charactersMap.get(code.toString()));
                    out.flush();
                    code.setLength(0);
                }
            }
        }
        in.close();
        out.close();
    }

}
