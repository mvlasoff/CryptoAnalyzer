package ru.javarush.vlasov.cryptoanalyzer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

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

    private static void statisticsAnalyzer(String inputFile, String outputFile, String refFile) throws IOException {
        if (!Files.exists(Path.of(inputFile))) {
            System.out.println("Input file doesn't exist.");
            return;
        }
        if (!Files.exists(Path.of(refFile))) {
            System.out.println("Reference file doesn't exist.");
            return;
        }

        if (!Files.exists(Path.of(outputFile))) {
            Files.createFile(Path.of(outputFile));
        }

        BufferedReader refBR = Files.newBufferedReader(Path.of(refFile));
        BufferedReader encBR = Files.newBufferedReader(Path.of(inputFile));
        BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(outputFile), StandardOpenOption.TRUNCATE_EXISTING);

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
        //Scanner refScannerReader = new Scanner(Files.newBufferedReader(Path.of(refFile)));
        while ((line = refBR.readLine()) != null) {
            //line = refScannerReader.nextLine();
            char[] chars = line.toCharArray();
            //refCharCount = refCharCount + chars.length;

            for (char c : chars) {
                if (characterSet.contains(c)) {
                    Float i = refMap.get(c);
                    i++;
                    refCharCount++;
                    refMap.put(c, i);
                }
            }
        }
        //refScannerReader.close();
        refBR.close();
        System.out.println("Reference chars count: " + refCharCount);

        //How many of each symbol in encrypted text.
        //Scanner encScannerReader = new Scanner(Files.newBufferedReader(Path.of(inputFile)));
        while ((line = encBR.readLine()) != null) {
            char[] chars = line.toCharArray();
            //encCharCount = encCharCount + chars.length;

            for (char c : chars) {
                if (characterSet.contains(c)) {
                    Float i = encMap.get(c);
                    i++;
                    encCharCount++;
                    encMap.put(c, i);
                }
            }
        }
        //encScannerReader.close();
        encBR.close();
        System.out.println("Encoded chars count: " + encCharCount);

        //Collecting statistics.
        for (Character character : characterSet) {
            Float refF = refMap.get(character);
            refF = refF / refCharCount * 100;
            refMap.put(character, refF);

            Float encF = encMap.get(character);
            encF = encF / encCharCount * 100;
            encMap.put(character, encF);
        }

        System.out.println(refMap);
        System.out.println("-------------------------");
        System.out.println(encMap);

        float treshold = 2.0F;
        HashMap<Character, Character> refEncMap = new HashMap<>();

        //Finding 'space'.
        Float spaceStat = refMap.get(' ');
        for (Map.Entry<Character, Float> characterFloatEntry : encMap.entrySet()) {
            Float value = characterFloatEntry.getValue();
            if (Math.abs(value - spaceStat) < treshold) {
                refEncMap.put(characterFloatEntry.getKey(), ' ');
                characterSet.remove(' ');
                break;
            }
        }

        treshold = 0.01F;
        //Making map of characters reference vs encrypted.
        while (!characterSet.isEmpty()){
            Iterator<Character> characterIterator = characterSet.iterator();
            while(characterIterator.hasNext()){
                Character nextCh = characterIterator.next();
                Float rF = refMap.get(nextCh);
                for (Map.Entry<Character, Float> characterFloatEntry : encMap.entrySet()) {
                    Float value = characterFloatEntry.getValue();
                    if (Math.abs(rF - value) < treshold) {
                        if (!refEncMap.containsKey(characterFloatEntry.getKey())) {
                            refEncMap.put(characterFloatEntry.getKey(), nextCh);
                            characterIterator.remove();
                            break;
                        }
                    }
                }
            }
            treshold = treshold + 0.1F;
        }

        System.out.println("------------------");
        System.out.println(refEncMap);

        //Reading ecrypted file and swapping chars.
        Scanner scanner = new Scanner(Files.newBufferedReader(Path.of(inputFile)));
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            char[] chars = line.toCharArray();

            for (int i = 0; i < chars.length; i++) {
                Character ch = refEncMap.get(chars[i]);
                if(ch != null) {
                    chars[i] = ch;
                }
            }
            bufferedWriter.write(chars);

            if (scanner.hasNextLine()) {
                bufferedWriter.write("\r\n");
            }
        }
        scanner.close();
        bufferedWriter.close();
    }
}