package ru.javarush.vlasov.cryptoanalyzer;

import java.io.*;
import java.nio.file.*;
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

                statisticsAnalyzer(inputFile, outputFile, refFile, scanner);
            }

        }
        scanner.close();
    }

    private static void encryptFile(String inputFile, String outputFile, String encryptionKey) throws IOException {
        if (Files.exists(Path.of(Constants.USER_DIR + inputFile))) {
            if (!Files.exists(Path.of(Constants.USER_DIR + outputFile))) {
                Files.createFile(Path.of(Constants.USER_DIR + outputFile));
            }
        } else {
            System.out.println("Check input file. It must exist.");
            return;
        }

        Scanner scanner = new Scanner(Files.newBufferedReader(Path.of(Constants.USER_DIR + inputFile)));
        BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(Constants.USER_DIR + outputFile), StandardOpenOption.TRUNCATE_EXISTING);

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

    }

    private static void decodeFile(String inputFile, String outputFile, String encryptionKey) throws IOException {
        if (Files.exists(Path.of(Constants.USER_DIR + inputFile))) {
            if (!Files.exists(Path.of(Constants.USER_DIR + outputFile))) {
                Files.createFile(Path.of(Constants.USER_DIR + outputFile));
            }
        } else {
            System.out.println("Check input file. It must exist.");
            return;
        }

        Scanner scannerReader = new Scanner(Files.newBufferedReader(Path.of(Constants.USER_DIR + inputFile)));
        BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(Constants.USER_DIR + outputFile), StandardOpenOption.TRUNCATE_EXISTING);

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

        int encKey = Constants.ALPHABET.length - 1;
        boolean isEncKeyFound = false;

        while (encKey >= 0 && !isEncKeyFound) {

/*            if (!Files.exists(Path.of(Constants.USER_DIR + outputFile))) {
                Files.createFile(Path.of(Constants.USER_DIR + outputFile));
            }*/

            Scanner scannerReader = new Scanner(Files.newBufferedReader(Path.of(Constants.USER_DIR + inputFile)));
            BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(Constants.USER_DIR + outputFile), StandardOpenOption.TRUNCATE_EXISTING);

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
                        if ("как".equalsIgnoreCase(s)) {
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

    }

    private static void statisticsAnalyzer(String inputFile, String outputFile, String refFile, Scanner scanner) throws IOException {
        if (!Files.exists(Path.of(Constants.USER_DIR + inputFile))) {
            System.out.println("Input file doesn't exist.");
            return;
        }
        if (!Files.exists(Path.of(Constants.USER_DIR + refFile))) {
            System.out.println("Reference file doesn't exist.");
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
        try (BufferedReader refBR = Files.newBufferedReader(Path.of(Constants.USER_DIR + refFile))) {

            while ((line = refBR.readLine()) != null) {
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
        System.out.println("Reference chars count: " + refCharCount);

        //How many of each symbol in encrypted text.
        try (BufferedReader encBR = Files.newBufferedReader(Path.of(Constants.USER_DIR + inputFile))) {
            while ((line = encBR.readLine()) != null) {
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
        //Making map of characters reference vs encrypted.
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

        //Reading ecrypted file and swapping chars.
        try (Scanner scannerReader = new Scanner(Files.newBufferedReader(Path.of(Constants.USER_DIR + inputFile)));
             BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(Constants.USER_DIR + outputFile), StandardOpenOption.TRUNCATE_EXISTING)) {

            while (scannerReader.hasNext()) {
                line = scannerReader.nextLine();
                char[] chars = line.toCharArray();

                for (int i = 0; i < chars.length; i++) {
                    Character ch = refEncMap.get(chars[i]);
                    if (ch != null) {
                        chars[i] = ch;
                    }
                }
                bufferedWriter.write(chars);

                if (scannerReader.hasNextLine()) {
                    bufferedWriter.write("\r\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Correction mode.
        while (true) {
            char first = 'ф', second = 'ф';
            try (Scanner file = new Scanner(Files.newBufferedReader(Path.of(Constants.USER_DIR + outputFile)))) {

                int lineCounter = 0;
                while (file.hasNext() && lineCounter <= 20) {
                    String nextLine = file.nextLine();
                    System.out.println(nextLine);
                    lineCounter++;
                }

                System.out.println("""


                        -----------------------------------------
                        This is correction mode. Review text above.
                        Type 2 characters you want to swipe. Example: фй
                        (or type 'escape' for exit from correction mode).""");

                String str = scanner.nextLine();
                if ("escape".equals(str)) {
                    break;
                } else if (str != null) {
                    first = str.charAt(0);
                    second = str.charAt(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Writing correction to temp.txt in user.dir\text
            try (Scanner encryptedFile = new Scanner(Files.newBufferedReader(Path.of(Constants.USER_DIR + outputFile)));
                 BufferedWriter tempFile = Files.newBufferedWriter(Path.of(Constants.USER_DIR_TEMP_TXT), StandardOpenOption.TRUNCATE_EXISTING)) {

                while (encryptedFile.hasNext()) {
                    line = encryptedFile.nextLine();
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
                    tempFile.write(changedChars);

                    if (encryptedFile.hasNextLine()) {
                        tempFile.write("\r\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Files.copy(Path.of(Constants.USER_DIR_TEMP_TXT), Path.of(Constants.USER_DIR + outputFile), StandardCopyOption.REPLACE_EXISTING);
            Files.delete(Path.of(Constants.USER_DIR_TEMP_TXT));
        }
    }
}