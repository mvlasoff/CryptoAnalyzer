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

        //Any line.
        String line;
        //Reference file statistics.
        HashMap<Character, Float> refMap = new HashMap<>();
        //Encrypted file statistics.
        HashMap<Character, Float> encMap = new HashMap<>();
        //Reference char counter.
        int refCharCount = 0;
        //Encrypted char counter.
        int encCharCount = 0;

        //How many of each symbol in reference text.
        try (BufferedReader referenceFileReade = Files.newBufferedReader(Path.of(Constants.USER_DIR + refFile))) {
            char oneChar;
            while (referenceFileReade.ready()) {
                oneChar = (char) referenceFileReade.read();
                if (refMap.containsKey(oneChar)) {
                    Float refValue = refMap.get(oneChar);
                    refValue++;
                    refMap.put(oneChar, refValue);
                } else {
                    refMap.put(oneChar, 1.0F);
                }
                refCharCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //How many of each symbol in encrypted text.
        try (BufferedReader encryptedFileReade = Files.newBufferedReader(Path.of(Constants.USER_DIR + inputFile))) {
            char oneChar;
            while (encryptedFileReade.ready()) {
                oneChar = (char) encryptedFileReade.read();
                if (encMap.containsKey(oneChar)) {
                    Float encValue = encMap.get(oneChar);
                    encValue++;
                    encMap.put(oneChar, encValue);
                } else {
                    encMap.put(oneChar, 1.0F);
                }
                encCharCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Collecting statistics.
        for (Character character : refMap.keySet()) {
            Float refF = refMap.get(character);
            refF = refF / refCharCount * 100;
            refMap.put(character, refF);
        }
        for (Character character : encMap.keySet()) {
            Float encF = encMap.get(character);
            encF = encF / encCharCount * 100;
            encMap.put(character, encF);
        }

        //Sorting reference map by value.
        List<Map.Entry<Character, Float>> refList = new ArrayList<>(refMap.entrySet());
        refList.sort(Map.Entry.comparingByValue(new Comparator<Float>() {
            @Override
            public int compare(Float o1, Float o2) {
                return (int) ((o2 - o1) * 10000);
            }
        }));

        Map<Character, Float> refMapSorted = new LinkedHashMap<>();
        for (Map.Entry<Character, Float> entry : refList) {
            refMapSorted.put(entry.getKey(), entry.getValue());
        }

        //Sorting encrypted map by value.
        List<Map.Entry<Character, Float>> encList = new ArrayList<>(encMap.entrySet());
        encList.sort(Map.Entry.comparingByValue(new Comparator<Float>() {
            @Override
            public int compare(Float o1, Float o2) {
                return (int) ((o2 - o1) * 10000);
            }
        }));

        Map<Character, Float> encMapSorted = new LinkedHashMap<>();
        for (Map.Entry<Character, Float> entry : encList) {
            encMapSorted.put(entry.getKey(), entry.getValue());
        }

        //Reference key array.
        Object[] refKeyArray = refMapSorted.keySet().toArray();
        //Encrypted key array.
        Object[] encKeyArray = encMapSorted.keySet().toArray();

        //Map of encrypted chars - reference chars.
        HashMap<Character, Character> refEncMap = new HashMap<>();
        //Creating map encrypted character - reference(dictionary) character.
        for (int i = 0; i < encKeyArray.length && i < refKeyArray.length; i++) {
            refEncMap.put((Character) encKeyArray[i], (Character) refKeyArray[i]);
        }

        //Reading encrypted file and swapping chars.
        try (BufferedReader fileReader = Files.newBufferedReader(Path.of(Constants.USER_DIR + inputFile));
             BufferedWriter fileWriter = Files.newBufferedWriter(Path.of(Constants.USER_DIR + outputFile), StandardOpenOption.TRUNCATE_EXISTING)) {

            StringBuilder stringBuilder = new StringBuilder();

            while (fileReader.ready()) {
                char oneChar = (char) fileReader.read();
                Character decodedChar = refEncMap.get(oneChar);
                if (decodedChar != null) {
                    stringBuilder.append(decodedChar);
                } else {
                    stringBuilder.append(oneChar);
                }
            }
            fileWriter.write(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Correction mode.
        while (true) {
            char first = 'ф', second = 'ф';
            String str1 = "", str2 = "";
            try (BufferedReader fileReader = Files.newBufferedReader(Path.of(Constants.USER_DIR + outputFile))) {

                int linesToRead = 100, lineCounter = 0;

                //Printing first 100 lines for review.
                while (fileReader.ready() && lineCounter <= linesToRead) {
                    line = fileReader.readLine();
                    System.out.println(line);
                    lineCounter++;
                }

                System.out.println("""


                        -----------------------------------------
                        This is correction mode. Review text above. Type 2 characters you want to swipe.
                        Press 'Enter' after each input.
                        Example: if you have "йункциональныф", then type 'ф', 'Enter', 'й', 'Enter'
                        or type 'й', 'Enter, 'ф', 'Enter' to make 'функциональный' (either way works).
                        For '\\n' or Line Feed type "LF", for '\\r' Carriage Return - "CR", for '\\t' Tab - "TB".
                        Sometimes LF and CR are swapped (you can notice a blank line between other lines).
                        To fix type "LF", Enter, "CR", Enter (or "CR", Enter, "LF", Enter).
                        Type 'quit' for exit from correction mode).""");

                str1 = consoleScanner.nextLine();
                if ("quit".equals(str1)) {
                    break;
                }
                str2 = consoleScanner.nextLine();
                if ("LF".equals(str1)) {
                    first = '\n';
                }else if ("CR".equals(str1)) {
                    first = '\r';
                } else if ("TB".equals(str1)) {
                    first = '\t';
                } else if (!str1.isEmpty()) {
                    first = str1.charAt(0);
                } else {
                    continue;
                }

                if ("LF".equals(str2)) {
                    second = '\n';
                } else if ("CR".equals(str2)) {
                    second = '\r';
                } else if ("TB".equals(str2)) {
                    second = '\t';
                } else if (!str2.isEmpty()) {
                    second = str2.charAt(0);
                } else {
                    continue;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            //Writing corrections into temp.txt. When done moving temp.txt to outputFile.txt.
            try (BufferedReader fileReader = Files.newBufferedReader(Path.of(Constants.USER_DIR + outputFile));
                 BufferedWriter tempFileWriter = Files.newBufferedWriter(Path.of(Constants.USER_DIR_TEMP_TXT), StandardOpenOption.TRUNCATE_EXISTING)) {

                StringBuilder strB = new StringBuilder();
                while (fileReader.ready()) {
                    char oneChar = (char) fileReader.read();
                    if (oneChar == first) {
                        strB.append(second);
                    } else if (oneChar == second) {
                        strB.append(first);
                    }
                    else {
                        strB.append(oneChar);
                    }
                }
                tempFileWriter.write(strB.toString());
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
