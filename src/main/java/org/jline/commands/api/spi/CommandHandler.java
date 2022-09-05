package org.jline.commands.api.spi;

import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.Reader;

import lombok.NonNull;
import org.jline.commands.ConsoleException;

public interface CommandHandler {

    String getName();

    void execute(@NonNull Reader in, @NonNull PrintWriter out, String... args) throws ConsoleException;

    ActionListener getListener();

    void setExitListener(ActionListener listener);

    void printHelp(@NonNull PrintWriter out);

    void printHelpVerbose(@NonNull PrintWriter out);

}
