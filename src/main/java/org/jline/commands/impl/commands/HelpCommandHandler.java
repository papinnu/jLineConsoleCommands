package org.jline.commands.impl.commands;

import java.io.PrintWriter;
import java.io.Reader;

import lombok.NonNull;
import org.jline.commands.api.CommandFactory;

public class HelpCommandHandler extends AbstractCommandHandler {

    public static final String COMMAND_NAME = "help";

    public HelpCommandHandler() {
        super(COMMAND_NAME);
    }

    @Override
    public void execute(Reader in, PrintWriter out, String... args) {
        if (args != null && args.length == 1
            && HelpCommandHandler.COMMAND_NAME.equalsIgnoreCase(args[0])) {
            printHelpVerbose(out);
            return;
        }
        out.println("");
        out.println(USAGE_LABEL_STAR);
        out.println("\033[38;5;87m[command] [help]* | {[path to file] [arguments]*}\033[0m");
        out.println("");
        out.println("\033[7mAvailable commands:\033[0m");
        CommandFactory.getInstance().allCommands().forEach(command -> command.printHelp(out));
        out.println("");
        out.println(EXAMPLE_LABEL);
        out.println("seal /home/user/my-raw-document.json");
        out.println("validate ~/my-sealed-document.json");
        out.println("");
    }

    @Override
    public void printHelp(@NonNull PrintWriter out) {
        out.println(String.format(FORMAT_COMMAND_DESC, COMMAND_NAME, "prints this message"));
    }

    @Override
    public void printHelpVerbose(@NonNull PrintWriter out) {
        printHelp(out);
    }

}
