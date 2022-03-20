package ru.javarush.vlasov.cryptoanalyzer;

import ru.javarush.vlasov.cryptoanalyzer.commands.Action;
import ru.javarush.vlasov.cryptoanalyzer.commands.Commands;

import java.util.Arrays;

public class Controller {
    public void doAction(String[] parameters) {
        String commandName = parameters[0];
        Action action = Commands.find(commandName);
        action.run(Arrays.copyOfRange(parameters, 1, parameters.length));
    }
}
