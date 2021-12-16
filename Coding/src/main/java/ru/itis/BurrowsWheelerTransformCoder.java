package ru.itis;

import java.io.*;
import java.util.*;

public class BurrowsWheelerTransformCoder {

    private final ArrayList<String> rotations;
    private final HashSet<Character> alphabet;
    private final ArrayList<Character> tempCode;
    private Integer keyIndex;
    private static final int PIECE_SIZE = 1000;

    public BurrowsWheelerTransformCoder() {
        rotations = new ArrayList<>();
        alphabet = new HashSet<>();
        tempCode = new ArrayList<>();
        keyIndex = -1;
    }

    public void encode(String filePath, String codeFilePath) throws IOException {
        clearPieceMetadata();
        readFileAndWriteCodeFile(filePath, codeFilePath);
    }

    public void decode(String codeFilePath, String filePath) throws IOException {
        clearPieceMetadata();
        readCodeFile(codeFilePath, filePath);
    }

    private void readFileAndWriteCodeFile(String filePath, String codeFilePath) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filePath));
        BufferedWriter out = new BufferedWriter(new FileWriter(codeFilePath));
        String line;
        StringBuilder piece = new StringBuilder();
        while ((line = in.readLine()) != null) {
            int border = 0;
            while (border < line.length() || piece.length() == PIECE_SIZE) {
                if (piece.length() + line.substring(border).length() < PIECE_SIZE) {
                    piece.append(line.substring(border));
                    border = line.length();
                } else {
                    int oldBorder = border;
                    border += (PIECE_SIZE - piece.length());
                    piece.append(line, oldBorder, border);
                }
                if (piece.length() == PIECE_SIZE) {
                    fillPieceMetadata(piece);
                    // Write alphabet
                    for (Character c : alphabet) {
                        out.write(c);
                        out.write('\n');
                        out.flush();
                    }
                    // Write delimiter
                    out.write("--");
                    out.write('\n');
                    // Write code
                    out.write(keyIndex.toString());
                    out.write('\n');
                    out.write(encodeWithMoveToFront());
                    out.write('\n');
                    out.flush();
                    // Clear piece
                    piece.setLength(0);
                    clearPieceMetadata();
                }
            }
            piece.append('\n');
        }
        if (piece.length() > 0) {
            fillPieceMetadata(piece);
            // Write alphabet
            for (Character c : alphabet) {
                out.write(c);
                out.write('\n');
                out.flush();
            }
            // Write delimiter
            out.write("--");
            out.write('\n');
            // Write code
            out.write(keyIndex.toString());
            out.write('\n');
            out.write(encodeWithMoveToFront());
            out.flush();
        }
        in.close();
        out.close();
    }

    private void fillPieceMetadata(StringBuilder piece) {
        for (int i = 0; i < piece.length(); i++) {
            rotations.add(piece.toString());
            piece.append(piece.charAt(0));
            piece.deleteCharAt(0);
        }
        rotations.sort(String::compareTo);
        for (int i = 0; i < rotations.size(); i++) {
            if (rotations.get(i).equals(piece.toString())) {
                keyIndex = i;
            }
            alphabet.add(rotations.get(i).charAt(0));
            tempCode.add(rotations.get(i).charAt(rotations.get(i).length() - 1));
        }
    }

    private String encodeWithMoveToFront() {
        LinkedList<Character> list = new LinkedList<>(alphabet);
        Collections.sort(list);
        StringBuilder code = new StringBuilder();
        int blockSize = Integer.toString(alphabet.size() - 1, 2).length();
        for (char c : tempCode) {
            int index = list.indexOf(c);
            list.remove(index);
            list.addFirst(c);
            String characterCode = Integer.toString(index, 2);
            for (int j = 0; j < blockSize - characterCode.length(); j++) {
                code.append('0');
            }
            code.append(characterCode);
        }
        return code.toString();
    }

    private void clearPieceMetadata() {
        rotations.clear();
        alphabet.clear();
        tempCode.clear();
        keyIndex = -1;
    }

    private void readCodeFile(String codeFilePath, String filePath) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(codeFilePath));
        BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
        String line;
        while ((line = in.readLine()) != null) {
            if (!line.equals("--") && line.length() > 0) {
                alphabet.add(line.charAt(0));
            } else if (line.length() == 0) {
                alphabet.add('\n');
            } else {
                keyIndex = Integer.parseInt(in.readLine());
                decodeWithMoveToFront(in.readLine());
                ArrayList<Character> sortedAlphabet = new ArrayList<>(alphabet);
                sortedAlphabet.sort(Character::compareTo);
                HashMap<Character, Integer> indices = new HashMap<>();
                ArrayList<Integer> count = new ArrayList<>();
                for (int i = 0; i < sortedAlphabet.size(); i++) {
                    count.add(0);
                    indices.put(sortedAlphabet.get(i), i);
                }
                for (Character c : tempCode) {
                    count.set(indices.get(c), count.get(indices.get(c)) + 1);
                }
                int sum = 0;
                for (Character c : sortedAlphabet) {
                    sum += count.get(indices.get(c));
                    count.set(indices.get(c), sum - count.get(indices.get(c)));
                }
                ArrayList<Integer> t = new ArrayList<>();
                for (int i = 0; i < tempCode.size(); i++) {
                    t.add(0);
                }
                for (int i = 0; i < tempCode.size(); i++) {
                    t.set(count.get(indices.get(tempCode.get(i))), i);
                    count.set(indices.get(tempCode.get(i)), count.get(indices.get(tempCode.get(i))) + 1);
                }
                int index = t.get(keyIndex);
                for (int i = 0; i < t.size(); i++) {
                    out.write(tempCode.get(index));
                    out.flush();
                    index = t.get(index);
                }
                clearPieceMetadata();
            }
        }
        in.close();
        out.close();
    }

    private void decodeWithMoveToFront(String code) {
        LinkedList<Character> list = new LinkedList<>(alphabet);
        Collections.sort(list);
        int blockSize = Integer.toString(alphabet.size() - 1, 2).length();
        for (int i = 0; i < code.length(); i += blockSize) {
            int index = Integer.valueOf(code.substring(i, i + blockSize), 2);
            char c = list.remove(index);
            tempCode.add(c);
            list.addFirst(c);
        }
    }

}
