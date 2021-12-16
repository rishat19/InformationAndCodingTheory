package ru.itis;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HammingCoder {

    private final byte[] masks = new byte[]{
            (byte) 0b10000000,
            0b01000000,
            0b00100000,
            0b00010000,
            0b00001000,
            0b00000100,
            0b00000010,
            0b00000001
    };

    public HammingCoder() {
    }

    public void encode(String filePath, String codeFilePath) throws IOException {
        readFileAndWriteCodeFile(filePath, codeFilePath);
    }

    public void decode(String codeFilePath, String filePath) throws IOException {
        readCodeFile(codeFilePath, filePath);
    }

    private void readFileAndWriteCodeFile(String filePath, String codeFilePath) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filePath));
        BufferedWriter out = new BufferedWriter(new FileWriter(codeFilePath));
        String line;
        while ((line = in.readLine()) != null) {
            out.write(encodeBytesArray((line + '\n').getBytes(StandardCharsets.UTF_8)));
            out.flush();
        }
        in.close();
        out.close();
    }

    private String encodeBytesArray(byte[] bytes) {
        ArrayList<Integer> bits = new ArrayList<>();
        for (byte b : bytes) {
            for (byte mask : masks) {
                bits.add((b & mask) != 0 ? 1 : 0);
            }
        }
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < bits.size(); i += 4) {
            code.append(encodeByte(bits.get(i), bits.get(i + 1), bits.get(i + 2), bits.get(i + 3)));
        }
        return code.toString();
    }

    private String encodeByte(int b1, int b2, int b3, int b4) {
        int p1 = (b1 + b2 + b4) % 2;
        int p2 = (b1 + b3 + b4) % 2;
        int p3 = (b2 + b3 + b4) % 2;
        return String.valueOf(p1) + p2 + b1 + p3 + b2 + b3 + b4;
    }

    private void readCodeFile(String codeFilePath, String filePath) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(codeFilePath));
        BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.length() % 14 == 0) {
                int size = line.length() / 14;
                byte[] bytes = new byte[size];
                for (int i = 0; i < line.length(); i += 14) {
                    bytes[i / 14] = decodeByte(line.substring(i, i + 14));
                }
                out.write(new String(bytes, StandardCharsets.UTF_8));
            } else {
                throw new IOException("Incorrect code file");
            }
        }
        in.close();
        out.close();
    }

    private byte decodeByte(String code) {
        int[] startBits = new int[7];
        int[] endBits = new int[7];
        for (int i = 0; i < 7; i++) {
            startBits[i] = Integer.parseInt(String.valueOf(code.charAt(i)));
        }
        for (int i = 7; i < 14; i++) {
            endBits[i - 7] = Integer.parseInt(String.valueOf(code.charAt(i)));
        }
        int startBitsErrorIndex = getErrorIndex(startBits);
        if (startBitsErrorIndex != -1) {
            startBits[startBitsErrorIndex] = (startBits[startBitsErrorIndex] + 1) % 2;
        }
        int endBitsErrorIndex = getErrorIndex(endBits);
        if (endBitsErrorIndex != -1) {
            endBits[endBitsErrorIndex] = (endBits[endBitsErrorIndex] + 1) % 2;
        }
        String byteStr = String.valueOf(startBits[2]) + startBits[4] + startBits[5] + startBits[6]
                + endBits[2] + endBits[4] + endBits[5] + endBits[6];
        int byteInt = Integer.valueOf(byteStr, 2);
        return (byte) byteInt;
    }

    private int getErrorIndex(int[] bits) {
        int p1 = (bits[2] + bits[4] + bits[6]) % 2;
        int p2 = (bits[2] + bits[5] + bits[6]) % 2;
        int p3 = (bits[4] + bits[5] + bits[6]) % 2;
        int errorIndex = -1;
        if (p1 != bits[0]) {
            errorIndex += 1;
        }
        if (p2 != bits[1]) {
            errorIndex += 2;
        }
        if (p3 != bits[3]) {
            errorIndex += 4;
        }
        return errorIndex;
    }

}
