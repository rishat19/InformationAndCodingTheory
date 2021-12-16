package ru.itis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class Application {

    private static HuffmanCoder huffmanCoder;
    private static ArithmeticCoder arithmeticCoder;
    private static BurrowsWheelerTransformCoder burrowsWheelerTransformCoder;
    private static HammingCoder hammingCoder;
    private static String filePath;
    private static String codeFilePath;
    private static Scanner scanner;

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        scanner = new Scanner(System.in);
        huffmanCoder = new HuffmanCoder();
        arithmeticCoder = new ArithmeticCoder();
        burrowsWheelerTransformCoder = new BurrowsWheelerTransformCoder();
        hammingCoder = new HammingCoder();
        String command;
        System.out.println("Введите команду (справка - команда help):");
        while (!(command = scanner.nextLine()).equals("exit")) {
            runCommand(command);
            System.out.println("Введите команду:");
        }
    }

    private static void runCommand(String command) {
        try {
            switch (command) {
                case "huffman encode":
                    huffmanEncode();
                    break;
                case "huffman decode":
                    huffmanDecode();
                    break;
                case "arithmetic encode":
                    arithmeticEncode();
                    break;
                case "arithmetic decode":
                    arithmeticDecode();
                    break;
                case "bwt encode":
                    bwtEncode();
                    break;
                case "bwt decode":
                    bwtDecode();
                    break;
                case "hamming encode":
                    hammingEncode();
                    break;
                case "hamming decode":
                    hammingDecode();
                    break;
                case "compare":
                    compare();
                    break;
                case "help":
                    help();
                    break;
                default:
                    printCommandNameError(command);
                    break;
            }
        } catch (Exception e) {
            System.out.println("Ошибка ввода/вывода, проверьте корректность введённых данных");
        }

    }

    private static void huffmanEncode() throws IOException {
        printEncodeInvitation();
        huffmanCoder.encode(filePath, codeFilePath);
        System.out.println("Выполнено");
    }

    private static void huffmanDecode() throws IOException {
        printDecodeInvitation();
        huffmanCoder.decode(codeFilePath, filePath);
        System.out.println("Выполнено");
    }

    private static void arithmeticEncode() throws IOException {
        printEncodeInvitation();
        arithmeticCoder.encode(filePath, codeFilePath);
        System.out.println("Выполнено");
    }

    private static void arithmeticDecode() throws IOException {
        printDecodeInvitation();
        arithmeticCoder.decode(codeFilePath, filePath);
        System.out.println("Выполнено");
    }

    private static void bwtEncode() throws IOException {
        printEncodeInvitation();
        burrowsWheelerTransformCoder.encode(filePath, codeFilePath);
        System.out.println("Выполнено");
    }

    private static void bwtDecode() throws IOException {
        printDecodeInvitation();
        burrowsWheelerTransformCoder.decode(codeFilePath, filePath);
        System.out.println("Выполнено");
    }

    private static void hammingEncode() throws IOException {
        printEncodeInvitation();
        hammingCoder.encode(filePath, codeFilePath);
        System.out.println("Выполнено");
    }

    private static void hammingDecode() throws IOException {
        printDecodeInvitation();
        hammingCoder.decode(codeFilePath, filePath);
        System.out.println("Выполнено");
    }

    private static void compare() throws IOException {
        while (true) {
            System.out.println("Введите абсолютный путь первого текстового файла:");
            filePath = scanner.nextLine();
            if (!Files.exists(new File(codeFilePath).toPath())) {
                System.out.println("Указанный файл не существует");
            } else {
                break;
            }
        }
        while (true) {
            System.out.println("Введите абсолютный путь второго текстового файла:");
            codeFilePath = scanner.nextLine();
            if (!Files.exists(new File(filePath).toPath())) {
                System.out.println("Указанный файл не существует");
            } else {
                break;
            }
        }
        BufferedReader in1 = new BufferedReader(new FileReader(filePath));
        StringBuilder sb1 = new StringBuilder();
        String line;
        while ((line = in1.readLine()) != null) {
            sb1.append(line);
        }
        BufferedReader in2 = new BufferedReader(new FileReader(codeFilePath));
        StringBuilder sb2 = new StringBuilder();
        while ((line = in2.readLine()) != null) {
            sb2.append(line);
        }
        if (sb1.toString().equals(sb2.toString())) {
            System.out.println("Содержания файлов одинаковы");
        } else {
            System.out.println("Содержания файлов различны");
        }
    }

    private static void help() {
        System.out.println("Список доступных команд:");
        System.out.println("huffman encode - закодировать файл алгоритмом Хаффмана");
        System.out.println("huffman decode - декодировать файл алгоритмом Хаффмана");
        System.out.println("arithmetic encode - закодировать файл алгоритмом Арифметического кодирования");
        System.out.println("arithmetic decode - декодировать файл алгоритмом Арифметического кодирования");
        System.out.println("bwt encode - закодировать файл алгоритмом преобразования Барроуза — Уилера");
        System.out.println("bwt decode - декодировать файл алгоритмом преобразования Барроуза — Уилера");
        System.out.println("hamming encode - закодировать файл алгоритмом Хэмминга");
        System.out.println("hamming decode - декодировать файл алгоритмом Хэмминга");
        System.out.println("compare - сравнить содержания текстовых файлов");
        System.out.println("help - список доступных команд");
        System.out.println("exit - выход из программы");
    }

    private static void printCommandNameError(String command) {
        System.out.println("Команда " + command + " не распознана, введите help для получения справки");
    }

    private static void printEncodeInvitation() {
        while (true) {
            System.out.println("Введите абсолютный путь до текстового файла, подлежащего кодированию:");
            filePath = scanner.nextLine();
            if (!Files.exists(new File(filePath).toPath())) {
                System.out.println("Указанный файл не существует");
            } else {
                break;
            }
        }
        while (true) {
            System.out.println("Введите абсолютный путь до текстового файла, в котором будет сохранён код:");
            codeFilePath = scanner.nextLine();
            if (!Files.exists(new File(codeFilePath).toPath())) {
                System.out.println("Указанный файл не существует");
            } else {
                break;
            }
        }
    }

    private static void printDecodeInvitation() {
        while (true) {
            System.out.println("Введите абсолютный путь до текстового файла, подлежащего декодированию:");
            codeFilePath = scanner.nextLine();
            if (!Files.exists(new File(codeFilePath).toPath())) {
                System.out.println("Указанный файл не существует");
            } else {
                break;
            }
        }
        while (true) {
            System.out.println("Введите абсолютный путь до текстового файла, в котором будет сохранён расшифрованный текст:");
            filePath = scanner.nextLine();
            if (!Files.exists(new File(filePath).toPath())) {
                System.out.println("Указанный файл не существует");
            } else {
                break;
            }
        }
    }

}
