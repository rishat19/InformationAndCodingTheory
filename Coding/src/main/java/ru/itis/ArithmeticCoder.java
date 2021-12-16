package ru.itis;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ArithmeticCoder {

    private final HashMap<Character, Long> characterRateMap;
    private final HashMap<Character, ArrayList<Long>> segmentsMap;
    private Long numberOfCharacters;

    public ArithmeticCoder() {
        this.characterRateMap = new HashMap<>();
        this.segmentsMap = new HashMap<>();
        numberOfCharacters = 0L;
    }

    public void encode(String filePath, String codeFilePath) throws IOException {
        characterRateMap.clear();
        segmentsMap.clear();
        numberOfCharacters = 0L;
        readFileAndCalculateCharacterRate(filePath);
        buildSegments();
        writeCodeFile(filePath, codeFilePath);
    }

    public void decode(String codeFilePath, String filePath) throws IOException {
        readCodeFile(codeFilePath, filePath);
    }

    private void readFileAndCalculateCharacterRate(String filePath) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filePath));
        String line;
        characterRateMap.put('\n', 0L);
        while ((line = in.readLine()) != null) {
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                long rate;
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

    private void buildSegments() {
        for (Map.Entry<Character, Long> entry : characterRateMap.entrySet()) {
            ArrayList<Long> segment = new ArrayList<>();
            segment.add(numberOfCharacters);
            numberOfCharacters += entry.getValue();
            segment.add(numberOfCharacters);
            segmentsMap.put(entry.getKey(), segment);
        }
    }

    private void writeCodeFile(String filePath, String codeFilePath) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filePath));
        BufferedWriter out = new BufferedWriter(new FileWriter(codeFilePath));
        // Write points
        for (Map.Entry<Character, ArrayList<Long>> entry : segmentsMap.entrySet()) {
            out.write(entry.getKey());
            out.write(entry.getValue().get(0).toString());
            out.write('\n');
            out.flush();
        }
        // Write delimiter
        out.write("--");
        out.write('\n');
        // Write number of characters
        out.write(numberOfCharacters.toString());
        out.write('\n');
        out.flush();
        // Write code
        long start = 0L;
        long end = 999999999999999999L;
        long divider = 100000000000000000L;
        String line;
        while ((line = in.readLine()) != null) {
            for (int i = 0; i <= line.length(); i++) {
                char c = '\n';
                if (i < line.length()) {
                    c = line.charAt(i);
                }
                long st = (long) (start + (end - start + 1) * ((double) segmentsMap.get(c).get(0) / numberOfCharacters));
                end = (long) (start + (end - start + 1) * ((double) segmentsMap.get(c).get(1) / numberOfCharacters) - 1);
                start = st;
                long s = (start - start % divider) / divider;
                long e = (end - end % divider) / divider;
                while (s == e) {
                    out.write(getDigitCode((int) s));
                    out.flush();
                    start -= (start - start % divider);
                    start *= 10;
                    end -= (end - end % divider);
                    end = end * 10 + 9;
                    s = (start - start % divider) / divider;
                    e = (end - end % divider) / divider;
                }
            }
        }
        long avg = (start + end) / 2;
        while (divider != 1) {
            out.write(getDigitCode((int) (avg / divider)));
            out.flush();
            avg -= (avg - avg % divider);
            divider /= 10;
        }
        out.write(getDigitCode((int) avg));
        out.flush();
        in.close();
        out.close();
    }

    private String getDigitCode(int digit) {
        String digitCode = "";
        switch (digit) {
            case 0:
            case 1:
                digitCode = "000" + Integer.toString(digit, 2);
                break;
            case 2:
            case 3:
                digitCode = "00" + Integer.toString(digit, 2);
                break;
            case 4:
            case 5:
            case 6:
            case 7:
                digitCode = "0" + Integer.toString(digit, 2);
                break;
            case 8:
            case 9:
                digitCode = Integer.toString(digit, 2);
                break;
        }
        return digitCode;
    }

    private void readCodeFile(String codeFilePath, String filePath) throws IOException {
        ArrayList<Long> points = new ArrayList<>();
        ArrayList<Character> characters = new ArrayList<>();
        BufferedReader in = new BufferedReader(new FileReader(codeFilePath));
        BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
        // Read points
        String line;
        boolean flag = false;
        while (!(line = in.readLine()).equals("--")) {
            if (line.length() > 0 && !flag) {
                char c = line.charAt(0);
                points.add(Long.parseLong(line.substring(1)));
                characters.add(c);
            } else if (flag) {
                points.add(Long.parseLong(line));
                characters.add('\n');
                flag = false;
            } else {
                flag = true;
            }
        }
        // Read number of characters
        numberOfCharacters = Long.parseLong(in.readLine());
        points.add(numberOfCharacters);
        // Read code
        line = in.readLine();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < line.length(); i += 4) {
            code.append(Long.valueOf(line.substring(i, i + 4), 2));
        }
        long start = 0L;
        long end = 999999999999999999L;
        long divider = 100000000000000000L;
        int it = 18;
        long written = 0;
        while (it <= code.length() && written < numberOfCharacters) {
            long frame = Long.parseLong(code.substring(it - 18, it));
            long index = (long) ((frame - start) * ((double) numberOfCharacters / (end - start + 1)) - ((double) 1 / (end - start + 1)));
            int i = Collections.binarySearch(points, index, Long::compareTo);
            if (i < 0) {
                i = -i - 2;
            }
            out.write(characters.get(i));
            written++;
            out.flush();
            long st = (long) (start + (end - start + 1) * ((double) points.get(i) / numberOfCharacters));
            end = (long) (start + (end - start + 1) * ((double) points.get(i + 1) / numberOfCharacters) - 1);
            start = st;
            long s = (start - start % divider) / divider;
            long e = (end - end % divider) / divider;
            while (s == e) {
                start -= (start - start % divider);
                start *= 10;
                end -= (end - end % divider);
                end = end * 10 + 9;
                s = (start - start % divider) / divider;
                e = (end - end % divider) / divider;
                it++;
            }
        }
        in.close();
        out.close();
    }

}
