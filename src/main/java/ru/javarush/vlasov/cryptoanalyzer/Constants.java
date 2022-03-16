package ru.javarush.vlasov.cryptoanalyzer;

import java.io.File;

public class Constants {
    public static final char[] ALPHABET = {'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з',
            'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ',
            'ъ', 'ы', 'ь', 'э', 'ю', 'я', '.', ',', '«', '»', '"', '\'', ':', '!', '?', ' '};

    public static final String USER_DIR_TEMP_TXT = System.getProperty("user.dir")
            + File.separator + "text" + File.separator + "temp.txt" + File.separator;

    public static final String USER_DIR = System.getProperty("user.dir")
            + File.separator + "text" + File.separator;
}
