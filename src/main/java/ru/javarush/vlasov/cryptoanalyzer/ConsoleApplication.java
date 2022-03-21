package ru.javarush.vlasov.cryptoanalyzer;

import java.util.Scanner;

public class ConsoleApplication {
    private final Controller controller;
    public static Scanner consoleScanner;

    public ConsoleApplication() {
        controller = new Controller();
    }

    public void start(String[] args) {
        if (args.length == 0) {
            consoleScanner = new Scanner(System.in);
            while (true) {
                System.out.println("""
                    Choose one (or 'exit'):
                    1. Encryption.
                    2. Decoding.
                    3. Brute Force.
                    4. Statistical analysis.""");

                String nextLine = consoleScanner.nextLine();
                if ("exit".equalsIgnoreCase(nextLine)) {
                    break;
                }

                if (!nextLine.isEmpty()) {
                    int nextInt;
                    try {
                        nextInt = Integer.parseInt(nextLine);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        continue;
                    }

                    if (nextInt == 1) {
                        System.out.println("Enter input file, output file (file_name.txt) and encryption key. " +
                                "Press 'Enter' after each input.");
                        String inputFile = consoleScanner.nextLine();
                        String outputFile = consoleScanner.nextLine();
                        String encryptionKeyString = consoleScanner.nextLine();
                        String[] parameters = new String[]{"ENCRYPT", inputFile, outputFile, encryptionKeyString};
                        controller.doAction(parameters);
                    } else if (nextInt == 2) {
                        System.out.println("Enter input file, output file (file_name.txt) and decoding key. " +
                                "Press 'Enter' after each input.");
                        String inputFile = consoleScanner.nextLine();
                        String outputFile = consoleScanner.nextLine();
                        String encryptionKeyString = consoleScanner.nextLine();
                        String[] parameters = new String[]{"DECODE", inputFile, outputFile, encryptionKeyString};
                        controller.doAction(parameters);
                    } else if (nextInt == 3) {
                        System.out.println("Enter input file and output file (file_name.txt). " +
                                "Press 'Enter' after each input.");
                        String inputFile = consoleScanner.nextLine();
                        String outputFile = consoleScanner.nextLine();
                        String[] parameters = new String[]{"BRUTEFORCE", inputFile, outputFile};
                        controller.doAction(parameters);
                    } else if (nextInt == 4) {
                        System.out.println("Enter input file, output file (file_name.txt) and reference file. " +
                                "Press 'Enter' after each input.");
                        String inputFile = consoleScanner.nextLine();
                        String outputFile = consoleScanner.nextLine();
                        String refFile = consoleScanner.nextLine();
                        String[] parameters = new String[]{"STATISTICS_ANALYZER", inputFile, outputFile, refFile};
                        controller.doAction(parameters);
                    } else {
                        System.err.println("ERROR. Enter 1, 2, 3 or 4.");
                    }
                } else {
                    System.err.println("ERROR. Enter 'exit', 1, 2, 3 or 4.");
                }
            }
            consoleScanner.close();
        }
    }
}
