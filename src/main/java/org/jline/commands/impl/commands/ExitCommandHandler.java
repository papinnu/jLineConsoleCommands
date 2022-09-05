package org.jline.commands.impl.commands;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.NonNull;
import org.jline.commands.ConsoleException;

public class ExitCommandHandler extends AbstractCommandHandler {

    private static final String PROP_FORMAT_EXIT = "\"\033[38;5;220m%1$s\033[0m\" %2$s";

    public static final String COMMAND_NAME = "exit";

    public ExitCommandHandler() {
        super(COMMAND_NAME);
    }

    @Override
    public void execute(Reader in, PrintWriter out, String... args) throws ConsoleException {
        try {
            if (args != null
                && args.length > 0
                && HelpCommandHandler.COMMAND_NAME.equalsIgnoreCase(args[0])) {
                printHelpVerbose(out);
                return;
            }

            out.print(String.format(PROP_FORMAT_EXIT, "Are you sure you want to exit?", " [y/N]"));
            out.flush();
            int input = in.read();
            if (input != 'y' && input != 'Y') {
                return;
            }

            if (listener != null) {
                out.print("Good bye!\n");
                listener.actionPerformed(new ActionEvent(this, 0, COMMAND_NAME));
            }
        } catch (IOException ex) {
            Logger.getLogger(ExitCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw new ConsoleException(ex);
        }
    }

    @Override
    public void printHelp(@NonNull PrintWriter out) {
        out.println(String.format(FORMAT_COMMAND_DESC, COMMAND_NAME, "exit from the application's console"));
    }

    @Override
    public void printHelpVerbose(@NonNull PrintWriter out) {
        out.println("");
        out.println("Exit from the application's console and stops running the application.");
        out.println(USAGE_LABEL);
        out.println("\033[38;5;87m" + COMMAND_NAME + "\033[0m");
        out.println("");
    }

}
