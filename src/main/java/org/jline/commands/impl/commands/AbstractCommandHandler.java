package org.jline.commands.impl.commands;

import java.awt.event.ActionListener;

import lombok.NonNull;
import org.jline.commands.api.spi.CommandHandler;

public abstract class AbstractCommandHandler implements CommandHandler {

    protected static final String FORMAT_COMMAND_DESC = "%1$-20s %2$s";
    protected static final String FORMAT_COMMAND_DESC_ARGS = "%1$-20s %2$-20s %3$s";
    protected static final String FORMAT_COMMAND_OUTPUT = "\033[38;5;42m%s\033[0m";
    protected static final String USAGE_LABEL = "\033[38;5;87mUsage\033[0m:";
    protected static final String USAGE_LABEL_STAR = "\033[38;5;87mUsage\033[0m (optionals marked with *):";
    protected static final String EXAMPLE_LABEL = "\033[7mExamples:\033[0m";

    private final String commandName;
    protected ActionListener listener = null;

    public AbstractCommandHandler(@NonNull String commandName) {
        this.commandName = commandName;
    }

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public ActionListener getListener() {
        return listener;
    }

    @Override
    public void setExitListener(ActionListener listener) {
        this.listener = listener;
    }

}
