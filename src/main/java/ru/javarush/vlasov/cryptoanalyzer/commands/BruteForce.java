package ru.javarush.vlasov.cryptoanalyzer.commands;

import ru.javarush.vlasov.cryptoanalyzer.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BruteForce implements Action{
    @Override
    public void run(String[] parameters) {
        String inputFile = parameters[0];
        String outputFile = parameters[1];

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

        Instant timer = Instant.now();
        long startT = timer.toEpochMilli();
        //Any possible key.
        int encKey = Constants.ALPHABET.length - 1;
        //Array where index is possible encryption key.
        int[] keyCountArray = new int[Constants.ALPHABET.length];
        //Pattern 'End of sentence. Beginning of sentence'.
        String regex = "[к][а][к]";
        Pattern pattern = Pattern.compile(regex);
        //Actual key.
        int encryptionKey = 0;

        //Moving throughout all possible keys.
        while (encKey >= 0) {
            try (BufferedReader fileReader = Files.newBufferedReader(Path.of(Constants.USER_DIR + inputFile))) {

                String line;
                int linesToRead = 500, lineCounter = 0, patternCounter = 0;

                //Decoding first linesToRead lines of encrypted text.
                while (lineCounter <= linesToRead && fileReader.ready()) {

                    line = fileReader.readLine();
                    char[] chars = line.toCharArray();
                    char[] decodedChars = new char[chars.length];
                    int alphabetPos = -1, newPos;

                    //Decoding new line.
                    for (int i = 0; i < chars.length; i++) {
                        char iChar = chars[i];
                        //Finding symbol in ALPHABET.
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

                    line = String.valueOf(decodedChars);
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find()) {
                        patternCounter++;
                    }
                    lineCounter++;
                }
                //Filling array where index is a key and value is a number of patterns in decoded text.
                keyCountArray[encKey] = patternCounter;

            } catch (IOException e) {
                e.printStackTrace();
            }

            //Getting the biggest number of patterns.
            int intMaxOfKeys = 0;
            for (int i : keyCountArray) {
                if (i > intMaxOfKeys) {
                    intMaxOfKeys = i;
                }
            }

            //Getting index which would be the Encryption Key.
            for (int key = 0; key < keyCountArray.length; key++) {
                if (keyCountArray[key] == intMaxOfKeys) {
                    encryptionKey = key;
                    break;
                }
            }
            encKey--;
        }
        new Decoder().run(new String[]{inputFile, outputFile, String.valueOf(encryptionKey)});

        timer = Instant.now();
        long stopT = timer.toEpochMilli();
        System.out.println("Decoded key: " + encryptionKey);
        System.out.printf("Time: %.2f sec.\n", (stopT - startT) / 1000.0F);
    }
}
