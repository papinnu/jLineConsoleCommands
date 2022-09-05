package org.jline.commands.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Spliterator;
import java.util.stream.Stream;

import lombok.NonNull;
import org.jline.commands.api.spi.CommandHandler;

public class CommandFactory {

    private static final CommandFactory INSTANCE = new CommandFactory();

    private final Map<String, CommandHandler> commandHandlers;

    private CommandFactory() {
        commandHandlers = initCommands();
    }

    public static CommandFactory getInstance() {
        return INSTANCE;
    }

    public Optional<CommandHandler> getCommand(@NonNull String name) {
        return Optional.ofNullable(commandHandlers.get(name));
    }

    public Stream<CommandHandler> allCommands() {
        return commandHandlers.values().stream();
    }

    private Map<String, CommandHandler> initCommands() {
        ServiceLoader<CommandHandler> loader = ServiceLoader.load(CommandHandler.class);
        Spliterator<CommandHandler> split = loader.spliterator();
        final Map<String, CommandHandler> commands = new HashMap<>();
        split.forEachRemaining(command -> commands.put(command.getName(), command));
        if (commands.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(commands);
    }

}
