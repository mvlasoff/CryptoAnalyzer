package ru.javarush.vlasov.cryptoanalyzer;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Runner {
    public static void main(String[] args) throws IOException {
        Scanner consoleScanner = new Scanner(System.in);
        while (true) {
            System.out.println("""
                    Choose one (or 'exit'):
                    1. Encryption.
                    2. Decoding.
                    3. Brute Force.
                    4. Statistical analysis.""");

            String nextLine = consoleScanner.nextLine();
            if ("exit".equals(nextLine)) {
                break;
            }

            int nextInt = Integer.parseInt(nextLine);
            int encryptionKey;

            if (nextInt == 1) {
                System.out.println("Enter input file, output file and encryption key. Press 'Enter' after each input.");
                String inputFile = consoleScanner.nextLine();
                String outputFile = consoleScanner.nextLine();
                String encryptionKeyString = consoleScanner.nextLine();

                try {
                    encryptionKey = Integer.parseInt(encryptionKeyString);
                    encryptFile(inputFile, outputFile, encryptionKey);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    continue;
                }
            }

            if (nextInt == 2) {
                System.out.println("Enter input file, output file and decoding key. Press 'Enter' after each input.");
                String inputFile = consoleScanner.nextLine();
                String outputFile = consoleScanner.nextLine();
                String encryptionKeyString = consoleScanner.nextLine();
                try {
                    encryptionKey = Integer.parseInt(encryptionKeyString);
                    decodeFile(inputFile, outputFile, encryptionKey);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    continue;
                }
            }

            if (nextInt == 3) {
                System.out.println("Enter input file and output file. Press 'Enter' after each input.");
                String inputFile = consoleScanner.nextLine();
                String outputFile = consoleScanner.nextLine();

                bruteForce(inputFile, outputFile);
            }

            if (nextInt == 4) {
                System.out.println("Enter input file, output file and reference file. Press 'Enter' after each input.");
                String inputFile = consoleScanner.nextLine();
                String outputFile = consoleScanner.nextLine();
                String refFile = consoleScanner.nextLine();

                statisticsAnalyzer(inputFile, outputFile, refFile, consoleScanner);
            }
        }
        consoleScanner.close();
    }

    private static void encryptFile(String inputFile, String outputFile, int encryptionKey) throws IOException {
        if (Files.exists(Path.of(Constants.USER_DIR + inputFile))) {
            if (!Files.exists(Path.of(Constants.USER_DIR + outputFile))) {
                Files.createFile(Path.of(Constants.USER_DIR + outputFile));
            }
        } else {
            System.out.println("Check input file. It must exist.");
            return;
        }

        try (Scanner fileReader = new Scanner(Files.newBufferedReader(Path.of(Constants.USER_DIR + inputFile)));
             BufferedWriter fileWriter = Files.newBufferedWriter(Path.of(Constants.USER_DIR + outputFile), StandardOpenOption.TRUNCATE_EXISTING)) {

            int encKey = Math.abs(encryptionKey % Constants.ALPHABET.length);
            String line;

            while (fileReader.hasNextLine()) {
                line = fileReader.nextLine();
                char[] chars = line.toCharArray();
                char[] encryptedChars = new char[chars.length];
                int alphabetPos = -1, newPos;

                //Encrypting new line.
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
                fileWriter.write(String.valueOf(encryptedChars));

                if (fileReader.hasNextLine()) {
                    fileWriter.write("\r\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void decodeFile(String inputFile, String outputFile, int encryptionKey) throws IOException {
        if (Files.exists(Path.of(Constants.USER_DIR + inputFile))) {
            if (!Files.exists(Path.of(Constants.USER_DIR + outputFile))) {
                Files.createFile(Path.of(Constants.USER_DIR + outputFile));
            }
        } else {
            System.out.println("Check input file. It must exist.");
            return;
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

    private static void bruteForce(String inputFile, String outputFile) throws IOException {
        if (Files.exists(Path.of(Constants.USER_DIR + inputFile))) {
            if (!Files.exists(Path.of(Constants.USER_DIR + outputFile))) {
                Files.createFile(Path.of(Constants.USER_DIR + outputFile));
            }
        } else {
            System.out.println("Check input file. It must exist.");
            return;
        }

        Instant timer = Instant.now();
        long startT = timer.toEpochMilli();
        //Any possible key.
        int encKey = Constants.ALPHABET.length - 1;
        boolean isEncKeyFound = false;
        int[] keyCountArray = new int[Constants.ALPHABET.length];
        //Pattern 'End of sentence. Beginning of sentence'.
        String regex = "\\.[ ]+[А-ЯЁ]";
        Pattern pattern = Pattern.compile(regex);
        //Actual key.
        int encryptionKey = 0;

        //Moving throughout all possible keys.
        while (encKey >= 0) {
            try (BufferedReader fileReader = Files.newBufferedReader(Path.of(Constants.USER_DIR + inputFile))) {

                String line;
                int lineCounter = 0, patternCounter = 0;

                //Decoding first 50 lines of encrypted text.
                while (lineCounter <= 50) {

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
                //Making list of Possible Encryption Key as indexes and Pattern Counter as value.
                keyCountArray[encKey] = patternCounter;

            } catch (IOException e) {
                e.printStackTrace();
            }

            //Getting the biggest value.
            int intMaxOfKeys = 0;
            for (int i : keyCountArray) {
                if (i > intMaxOfKeys) {
                    intMaxOfKeys = i;
                }
            }

            //Getting index which is Encryption Key.
            for (int key = 0; key < keyCountArray.length; key++) {
                if (keyCountArray[key] == intMaxOfKeys) {
                    encryptionKey = key;
                    break;
                }
            }
            encKey--;
        }
        decodeFile(inputFile, outputFile, encryptionKey);

        timer = Instant.now();
        long stopT = timer.toEpochMilli();
        System.out.println("Decoded key: " + encryptionKey);
        System.out.printf("Time: %.2f sec.\n", (stopT - startT) / 1000.0F);
    }

    private static void statisticsAnalyzer(String inputFile, String outputFile, String refFile, Scanner consoleScanner) throws IOException {
        if (!Files.exists(Path.of(Constants.USER_DIR + inputFile))) {
            System.out.println("Check input file. It must exist.");
            return;
        }
        if (!Files.exists(Path.of(Constants.USER_DIR + refFile))) {
            System.out.println("Check reference (dictionary) file. It must exist.");
            return;
        }

        if (!Files.exists(Path.of(Constants.USER_DIR + outputFile))) {
            Files.createFile(Path.of(Constants.USER_DIR + outputFile));
        }
        if (!Files.exists(Path.of(Constants.USER_DIR_TEMP_TXT))) {
            Files.createFile(Path.of(Constants.USER_DIR_TEMP_TXT));
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
        //System.out.println("Reference chars count: " + refCharCount);

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
        //System.out.println("Encoded chars count: " + encCharCount);

        //Collecting statistics.
        for (Character character : characterSet) {
            Float refF = refMap.get(character);
            refF = refF / refCharCount * 100;
            refMap.put(character, refF);

            Float encF = encMap.get(character);
            encF = encF / encCharCount * 100;
            encMap.put(character, encF);
        }

        /*System.out.println(refMap);
        System.out.println("-------------------------");
        System.out.println(encMap);*/

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
        //Making map of characters, reference vs encrypted.
        while (!characterSet.isEmpty()) {
            Iterator<Character> characterIterator = characterSet.iterator();
            while (characterIterator.hasNext()) {
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

        /*System.out.println("------------------");
        System.out.println(refEncMap);*/

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

                int lineCounter = 0;
                //Printing first 100 lines for review.
                while (fileReader.hasNext() && lineCounter <= 100) {
                    String nextLine = fileReader.nextLine();
                    System.out.println(nextLine);
                    lineCounter++;
                }

                System.out.println("""


                        -----------------------------------------
                        This is correction mode. Review text above. Type 2 characters you want to swipe.
                        Example: if you have 'йункциональныф', then type 'фй' or 'йф' for 'функциональный'
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

            //Writing corrections into temp.txt
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
            Files.copy(Path.of(Constants.USER_DIR_TEMP_TXT), Path.of(Constants.USER_DIR + outputFile), StandardCopyOption.REPLACE_EXISTING);
        }
        Files.delete(Path.of(Constants.USER_DIR_TEMP_TXT));
    }
}