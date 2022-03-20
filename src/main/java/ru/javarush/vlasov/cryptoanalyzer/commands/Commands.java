package ru.javarush.vlasov.cryptoanalyzer.commands;

public enum Commands {
    ENCRYPT(new Encrypter()),
    DECODE(new Decoder()),
    BRUTEFORCE(new BruteForce()),
    STATISTICS_ANALYZER(new StatisticsAnalyzer());

    private final Action action;


    Commands(Action action) {
        this.action = action;
    }

    public static Action find(String commandName) {
        Commands command = Commands.valueOf(commandName.toUpperCase());
        return command.action;
    }
}
