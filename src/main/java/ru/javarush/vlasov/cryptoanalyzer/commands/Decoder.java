package ru.javarush.vlasov.cryptoanalyzer.commands;

import ru.javarush.vlasov.cryptoanalyzer.Constants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class Decoder implements Action{
    @Override
    public void run(String[] parameters) {
        String inputFile = parameters[0];
        String outputFile = parameters[1];
        String encryptionKeyString = parameters[2];
        int encryptionKey;

        try {
            encryptionKey = Integer.parseInt(encryptionKeyString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }

        if (!Files.exists(Path.of(Constants.USER_DIR + inputFile))) {
            System.err.println("ERROR. Input file must exist.");
            return;
        }
        if (!outputFile.endsWith(".txt")) {
            System.err.println("ERROR. Output file name must be *.txt.");
            return;
        }
        if (!Files.exists(Path.of(Constants.USER_DIR + outputFile))) {
            try {
                Files.createFile(Path.of(Constants.USER_DIR + outputFile));
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Couldn't create output file.");
                return;
            }
        }

        try (Scanner fileReader = new Scanner(Files.newBufferedReader(Path.of(Constants.USER_DIR + inputFile)));
             BufferedWriter fileWriter = Files.newBufferedWriter(Path.of(Constants.USER_DIR + outputFile), StandardOpenOption.TRUNCATE_EXISTING)) {

            int encKey = Math.abs(encryptionKey % Constants.ALPHABET.length);
            String line;

            while (fileReader.hasNextLine()) {
                line = fileReader.nextLine();
                char[] chars = line.toCharArray();
                char[] decodedChars = new char[chars.length];
                int alphabetPos = -1, newPos;

                //Decoding new line.
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
                fileWriter.write(decodedChars);

                if (fileReader.hasNextLine()) {
                    fileWriter.write("\r\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
