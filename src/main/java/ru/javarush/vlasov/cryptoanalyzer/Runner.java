package ru.javarush.vlasov.cryptoanalyzer;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Runner {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("""
                    Choose one (or 'exit'):
                    1. Encryption.
                    2. Decoding.
                    3. Brute Force.
                    4. Statistical analysis.""");

            String nextLine = scanner.nextLine();
            if ("exit".equals(nextLine)) {
                break;
            }

            int nextInt = Integer.parseInt(nextLine);

            if (nextInt == 1) {
                System.out.println("Enter input file, output file and encryption key. Press 'Enter' after each input.");
                String inputFile = scanner.nextLine();
                String outputFile = scanner.nextLine();
                String encryptionKey = scanner.nextLine();

                encryptFile(inputFile, outputFile, encryptionKey);
            }

            if (nextInt == 2) {
                System.out.println("Enter input file, output file and decoding key. Press 'Enter' after each input.");
                String inputFile = scanner.nextLine();
                String outputFile = scanner.nextLine();
                String encryptionKey = scanner.nextLine();

                decodeFile(inputFile, outputFile, encryptionKey);
            }

            if (nextInt == 3) {
                System.out.println("Enter input file and output file. Press 'Enter' after each input.");
                String inputFile = scanner.nextLine();
                String outputFile = scanner.nextLine();

                bruteForce(inputFile, outputFile);
            }

            if (nextInt == 4) {
                System.out.println("Enter input file, output file and reference file. Press 'Enter' after each input.");
                String inputFile = scanner.nextLine();
                String outputFile = scanner.nextLine();
                String refFile = scanner.nextLine();

                statisticsAnalyzer(inputFile, outputFile, refFile);
            }
        }
        scanner.close();
    }

    private static void encryptFile(String inputFile, String outputFile, String encryptionKey) throws IOException {
        if (Files.exists(Path.of(inputFile))) {
            if (!Files.exists(Path.of(outputFile))) {
                Files.createFile(Path.of(outputFile));
            }

            Scanner scanner = new Scanner(Files.newBufferedReader(Path.of(inputFile)));
            BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(outputFile), StandardOpenOption.TRUNCATE_EXISTING);

            int encKey = Math.abs(Integer.parseInt(encryptionKey) % Constants.ALPHABET.length);
            String line;

            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                char[] chars = line.toCharArray();
                char[] encryptedChars = new char[chars.length];
                int alphabetPos = -1, newPos;

                //move over new line.
                for (int i = 0; i < chars.length; i++) {
                    char iChar = chars[i];
                    //finding symbol in ALPHABET.
                    for (int j = 0; j < Constants.ALPHABET.length; j++) {
                        if (iChar == Constants.ALPHABET[j]) {
                            newPos = j + encKey;
                            alphabetPos = newPos % Constants.ALPHABET.length;
                            break;
                        }
                    }
                    if (alphabetPos < 0) {
                        encryptedChars[i] = chars[i];
                    } else {
                        encryptedChars[i] = Constants.ALPHABET[alphabetPos];
                        alphabetPos = -1;
                    }
                }
                bufferedWriter.write(String.valueOf(encryptedChars));

                if (scanner.hasNextLine()) {
                    bufferedWriter.write("\r\n");
                }
            }
            scanner.close();
            bufferedWriter.close();
        } else {
            System.out.println("Check input file. It must exist.");
        }
    }

    private static void decodeFile(String inputFile, String outputFile, String encryptionKey) throws IOException {
        if (Files.exists(Path.of(inputFile))) {
            if (!Files.exists(Path.of(outputFile))) {
                Files.createFile(Path.of(outputFile));
            }

            Scanner scannerReader = new Scanner(Files.newBufferedReader(Path.of(inputFile)));
            BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(outputFile), StandardOpenOption.TRUNCATE_EXISTING);

            int encKey = Math.abs(Integer.parseInt(encryptionKey) % Constants.ALPHABET.length);
            String line;

            while (scannerReader.hasNextLine()) {
                line = scannerReader.nextLine();
                char[] chars = line.toCharArray();
                char[] decodedChars = new char[chars.length];
                int alphabetPos = -1, newPos;

                //move over new line.
                for (int i = 0; i < chars.length; i++) {
                    char iChar = chars[i];
                    //finding symbol in ALPHABET.
                    for (int j = 0; j < Constants.ALPHABET.length; j++) {
                        if (iChar == Constants.ALPHABET[j]) {
                            newPos = j - encKey;
                            if (newPos < 0) {
                                alphabetPos = newPos + Constants.ALPHABET.length;
                            } else {
                                alphabetPos = newPos;
                            }
                            break;
                        }
                    }
                    if (alphabetPos < 0) {
                        decodedChars[i] = chars[i];
                    } else {
                        decodedChars[i] = Constants.ALPHABET[alphabetPos];
                        alphabetPos = -1;
                    }
                }
                bufferedWriter.write(decodedChars);

                if (scannerReader.hasNextLine()) {
                    bufferedWriter.write("\r\n");
                }
            }
            scannerReader.close();
            bufferedWriter.close();
        } else {
            System.out.println("Check input file. It must exist.");
        }
    }

    private static void bruteForce(String inputFile, String outputFile) throws IOException {
        if (Files.exists(Path.of(inputFile))) {

            int encKey = Constants.ALPHABET.length - 1;
            boolean isEncKeyFound = false;

            while (encKey >= 0 && !isEncKeyFound) {

                if (!Files.exists(Path.of(outputFile))) {
                    Files.createFile(Path.of(outputFile));
                }

                Scanner scannerReader = new Scanner(Files.newBufferedReader(Path.of(inputFile)));
                BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(outputFile), StandardOpenOption.TRUNCATE_EXISTING);

                String line;

                while (scannerReader.hasNextLine()) {
                    line = scannerReader.nextLine();
                    char[] chars = line.toCharArray();
                    char[] decodedChars = new char[chars.length];
                    int alphabetPos = -1, newPos;

                    //move over new line.
                    for (int i = 0; i < chars.length; i++) {
                        char iChar = chars[i];
                        //finding symbol in ALPHABET.
                        for (int j = 0; j < Constants.ALPHABET.length; j++) {
                            if (iChar == Constants.ALPHABET[j]) {
                                newPos = j - encKey;
                                if (newPos < 0) {
                                    alphabetPos = newPos + Constants.ALPHABET.length;
                                } else {
                                    alphabetPos = newPos;
                                }
                                break;
                            }
                        }
                        if (alphabetPos < 0) {
                            decodedChars[i] = chars[i];
                        } else {
                            decodedChars[i] = Constants.ALPHABET[alphabetPos];
                            alphabetPos = -1;
                        }
                    }
                    bufferedWriter.write(decodedChars);

                    if (scannerReader.hasNextLine()) {
                        bufferedWriter.write("\r\n");
                    }

                    //Comparing words in decoded line with dictionary.
                    if (!isEncKeyFound) {
                        String string = String.valueOf(decodedChars);
                        String[] strArray = string.split(" ");
                        for (String s : strArray) {
                            if("как".equalsIgnoreCase(s)){
                                isEncKeyFound = true;
                                break;
                            }
                        }
                    }
                }
                scannerReader.close();
                bufferedWriter.close();
                encKey--;
            }
            System.out.println("Decoded key: " + ++encKey);
        } else {
            System.out.println("Check input file. It must exist.");
        }
    }

    private static void statisticsAnalyzer(String inputFile, String outputFile, String refFile) {
    }
}