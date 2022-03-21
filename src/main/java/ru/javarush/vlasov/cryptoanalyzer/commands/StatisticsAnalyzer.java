package ru.javarush.vlasov.cryptoanalyzer.commands;

import ru.javarush.vlasov.cryptoanalyzer.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static ru.javarush.vlasov.cryptoanalyzer.ConsoleApplication.consoleScanner;

public class StatisticsAnalyzer implements Action {
    @Override
    public void run(String[] parameters) {
        String inputFile = parameters[0];
        String outputFile = parameters[1];
        String refFile = parameters[2];

        if (inputFile.isEmpty() || !Files.exists(Path.of(Constants.USER_DIR + inputFile))) {
            System.err.println("ERROR. Enter valid input file name (input file must exist).");
            return;
        }
        if (refFile.isEmpty() || !Files.exists(Path.of(Constants.USER_DIR + refFile))) {
            System.err.println("ERROR. Enter valid reference (dictionary) file name (it must exist).");
            return;
        }
        if (!outputFile.endsWith(".txt")) {
            System.err.println("ERROR. Enter valid output file name (output file name must be *.txt).");
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
        if (!Files.exists(Path.of(Constants.USER_DIR_TEMP_TXT))) {
            try {
                Files.createFile(Path.of(Constants.USER_DIR_TEMP_TXT));
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Couldn't create temporary file.");
            }
        }

        String line;
        HashMap<Character, Float> refMap = new HashMap<>();
        HashMap<Character, Float> encMap = new HashMap<>();
        int refCharCount = 0;
        int encCharCount = 0;

        for (char ch : Constants.ALPHABET) {
            refMap.put(ch, 0f);
            encMap.put(ch, 0f);
        }
        Set<Character> characterSet = refMap.keySet();

        //How many of each symbol in reference text.
        try (BufferedReader referenceFileReade = Files.newBufferedReader(Path.of(Constants.USER_DIR + refFile))) {

            while ((line = referenceFileReade.readLine()) != null) {
                char[] chars = line.toCharArray();

                for (char c : chars) {
                    if (characterSet.contains(c)) {
                        Float i = refMap.get(c);
                        i++;
                        refCharCount++;
                        refMap.put(c, i);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //How many of each symbol in encrypted text.
        try (BufferedReader encryptedFileReader = Files.newBufferedReader(Path.of(Constants.USER_DIR + inputFile))) {
            while ((line = encryptedFileReader.readLine()) != null) {
                char[] chars = line.toCharArray();

                for (char c : chars) {
                    if (characterSet.contains(c)) {
                        Float i = encMap.get(c);
                        i++;
                        encCharCount++;
                        encMap.put(c, i);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Collecting statistics.
        for (Character character : characterSet) {
            Float refF = refMap.get(character);
            refF = refF / refCharCount * 100;
            refMap.put(character, refF);

            Float encF = encMap.get(character);
            encF = encF / encCharCount * 100;
            encMap.put(character, encF);
        }

        //threshold for 'space'.
        float threshold = 2.0F;
        HashMap<Character, Character> refEncMap = new HashMap<>();

        //Finding 'space'.
        Float spaceStat = refMap.get(' ');
        for (Map.Entry<Character, Float> characterFloatEntry : encMap.entrySet()) {
            Float value = characterFloatEntry.getValue();
            if (Math.abs(value - spaceStat) < threshold) {
                refEncMap.put(characterFloatEntry.getKey(), ' ');
                characterSet.remove(' ');
                break;
            }
        }

        //minimum threshold for other symbols.
        threshold = 0.01F;
        //Making map of characters, reference vs encrypted.
        while (!characterSet.isEmpty()) {
            Iterator<Character> characterIterator = characterSet.iterator();
            while (characterIterator.hasNext()) {
                Character nextCh = characterIterator.next();
                Float rF = refMap.get(nextCh);
                for (Map.Entry<Character, Float> characterFloatEntry : encMap.entrySet()) {
                    Float value = characterFloatEntry.getValue();
                    if (Math.abs(rF - value) < threshold) {
                        if (!refEncMap.containsKey(characterFloatEntry.getKey())) {
                            refEncMap.put(characterFloatEntry.getKey(), nextCh);
                            characterIterator.remove();
                            break;
                        }
                    }
                }
            }
            threshold = threshold + 0.1F;
        }

        //Reading encrypted file and swapping chars.
        try (Scanner fileReader = new Scanner(Files.newBufferedReader(Path.of(Constants.USER_DIR + inputFile)));
             BufferedWriter fileWriter = Files.newBufferedWriter(Path.of(Constants.USER_DIR + outputFile), StandardOpenOption.TRUNCATE_EXISTING)) {

            while (fileReader.hasNext()) {
                line = fileReader.nextLine();
                char[] chars = line.toCharArray();

                for (int i = 0; i < chars.length; i++) {
                    Character ch = refEncMap.get(chars[i]);
                    if (ch != null) {
                        chars[i] = ch;
                    }
                }
                fileWriter.write(chars);

                if (fileReader.hasNextLine()) {
                    fileWriter.write("\r\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Correction mode.
        while (true) {
            char first = 'ф', second = 'ф';
            try (Scanner fileReader = new Scanner(Files.newBufferedReader(Path.of(Constants.USER_DIR + outputFile)))) {

                int linesToRead = 100, lineCounter = 0;
                //Printing first 100 lines for review.
                while (fileReader.hasNext() && lineCounter <= linesToRead) {
                    String nextLine = fileReader.nextLine();
                    System.out.println(nextLine);
                    lineCounter++;
                }

                System.out.println("""


                        -----------------------------------------
                        This is correction mode. Review text above. Type 2 characters you want to swipe.
                        Example: if you have 'йункциональныф', then type 'фй' or 'йф' to make 'функциональный'
                        (or type 'quit' for exit from correction mode).""");

                String str = consoleScanner.nextLine();
                if ("quit".equals(str)) {
                    break;
                } else if (str != null) {
                    first = str.charAt(0);
                    second = str.charAt(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Writing corrections into temp.txt. When done moving temp.txt to outputFile.txt.
            try (Scanner fileReader = new Scanner(Files.newBufferedReader(Path.of(Constants.USER_DIR + outputFile)));
                 BufferedWriter tempFileWriter = Files.newBufferedWriter(Path.of(Constants.USER_DIR_TEMP_TXT), StandardOpenOption.TRUNCATE_EXISTING)) {

                while (fileReader.hasNext()) {
                    line = fileReader.nextLine();
                    char[] chars = line.toCharArray();
                    char[] changedChars = new char[chars.length];

                    for (int i = 0; i < chars.length; i++) {
                        if (first == chars[i]) {
                            changedChars[i] = second;
                        } else if (second == chars[i]) {
                            changedChars[i] = first;
                        } else {
                            changedChars[i] = chars[i];
                        }
                    }
                    tempFileWriter.write(changedChars);

                    if (fileReader.hasNextLine()) {
                        tempFileWriter.write("\r\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Files.copy(Path.of(Constants.USER_DIR_TEMP_TXT), Path.of(Constants.USER_DIR + outputFile), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Couldn't copy temporary file to output file.");
            }
        }
        try {
            Files.delete(Path.of(Constants.USER_DIR_TEMP_TXT));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Couldn't delete temporary file.");
        }
    }
}
