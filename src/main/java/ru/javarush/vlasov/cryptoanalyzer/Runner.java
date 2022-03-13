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
                System.out.println("Enter input file, output file and encryption key.");
                String inputFile = scanner.nextLine();
                String outputFile = scanner.nextLine();
                String encryptionKey = scanner.nextLine();

                encryptFile(inputFile, outputFile, encryptionKey);
            }

            if (nextInt == 2) {
                System.out.println("Enter input file, output file and decoding key.");
                String inputFile = scanner.nextLine();
                String outputFile = scanner.nextLine();
                String encryptionKey = scanner.nextLine();

                decodeFile(inputFile, outputFile, encryptionKey);
            }
        }
        scanner.close();
    }

    private static void encryptFile(String inputFile, String outputFile, String encryptionKey) throws IOException {
        if (Files.exists(Path.of(inputFile))) {
            if (!Files.exists(Path.of(outputFile))) {
                Files.createFile(Path.of(outputFile));
            }
            Scanner scannerReader = new Scanner(Files.newBufferedReader(Path.of(inputFile)));
            BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(outputFile), StandardOpenOption.WRITE);
            int encKey = Math.abs(Integer.parseInt(encryptionKey) % Constants.ALPHABET.length);
            String line;

            while (scannerReader.hasNextLine()) {
                line = scannerReader.nextLine();
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
                bufferedWriter.write(encryptedChars);

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

    private static void decodeFile(String inputFile, String outputFile, String encryptionKey) throws IOException {
        if (Files.exists(Path.of(inputFile))) {
            if (!Files.exists(Path.of(outputFile))) {
                Files.createFile(Path.of(outputFile));
            }
            Scanner scannerReader = new Scanner(Files.newBufferedReader(Path.of(inputFile)));
            BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(outputFile), StandardOpenOption.WRITE);
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
                            if (newPos < 0){
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
}
