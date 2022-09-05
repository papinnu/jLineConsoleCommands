package org.jline.commands;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.FutureTask;

import lombok.NonNull;
import org.jline.builtins.Completers.DirectoriesCompleter;
import org.jline.builtins.Completers.FilesCompleter;
import org.jline.commands.api.CommandCompleter;
import org.jline.commands.api.CommandFactory;
import org.jline.commands.api.spi.CommandHandler;
import org.jline.commands.impl.ExitListenerImpl;
import org.jline.commands.impl.commands.ExitCommandHandler;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.style.StyledWriter;
import org.jline.style.Styler;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class CommandsConsole {

    private static final String PROP_FORMAT_ERROR = "\033[48;5;13m%s\033[0m";
    private static final String PROP_HISTORY_FILE = "sealer_cli.history.log";
    private static final String PROP_FORMAT_LEFT_PROMPT = "\033[38;5;220m%s> \033[0m";
    private static final String PROP_FORMAT_RIGHT_PROMPT = "\033[33;1m%s\033[0m";
    private static final String PROP_FORMAT_WELCOME = "\033[38;5;87m%s\033[0m";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.US);

    private static final Completer[] COMPLETERS = new Completer[]{new CommandCompleter(),
        new DirectoriesCompleter(Paths.get("")), new FilesCompleter(Paths.get(""))};

    private final String termName;
    private Terminal terminal;
    private FutureTask<Void> task;
    private volatile boolean canceled;

    public CommandsConsole(String name) {
        this.termName = name;
        CommandFactory.getInstance().getCommand(ExitCommandHandler.COMMAND_NAME)
            .ifPresent(command -> command.setExitListener(new ExitListenerImpl(this)));
    }

    public void start() throws Exception {
        terminal = TerminalBuilder.builder().name(termName).system(true).nativeSignals(true)
            .signalHandler(Terminal.SignalHandler.SIG_IGN).build();

        LineReader reader = LineReaderBuilder.builder().appName(termName).terminal(terminal)
            .variable(LineReader.HISTORY_FILE, PROP_HISTORY_FILE).history(new DefaultHistory())
            .completer(new AggregateCompleter(COMPLETERS)).build();
        reader.setOpt(LineReader.Option.AUTO_FRESH_LINE);
        StyledWriter writer = new StyledWriter(terminal.writer(), terminal, Styler.resolver(termName), true);

        final String lPrompt = Terminal.TYPE_DUMB.equals(terminal.getType()) ?
            null :
            String.format(PROP_FORMAT_LEFT_PROMPT, termName);
        writer.println(String.format(PROP_FORMAT_WELCOME,"Welcome to Sealer CLI..."));
        task = new FutureTask<>(() -> {
            while (!canceled) {
                try {
                    String rPrompt = Terminal.TYPE_DUMB.equals(terminal.getType()) ?
                        null :
                        String.format(PROP_FORMAT_RIGHT_PROMPT, LocalTime.now().format(TIME_FORMATTER));
                    reader.readLine(lPrompt, rPrompt, (Character) null, null);
                    List<String> words = reader.getParsedLine().words();
                    if (!words.isEmpty() && !words.get(0).isEmpty()) {
                        String name = words.get(0);
                        Optional<CommandHandler> command = CommandFactory.getInstance().getCommand(name);
                        if (command.isPresent()) {
                            String[] args = words.stream().skip(1).filter(s -> !s.isEmpty()).toArray(String[]::new);
                            try {
                                command.get().execute(terminal.reader(), writer, args);
                            } catch (ConsoleException ex) {
                                appendException(writer, ex);
                            }
                        } else {
                            writer.append(name).println(": command not found");
                        }
                    }
                } catch (UserInterruptException ex) {
                    CommandFactory.getInstance().getCommand(ExitCommandHandler.COMMAND_NAME).ifPresent(command -> {
                        try {
                            command.execute(terminal.reader(), writer);
                        } catch (ConsoleException e1) {
                            appendException(writer, ex);
                        }
                    });
                } catch (Throwable ex) {
                    appendException(writer, ex);
                } finally {
                    writer.flush();
                }
            }
        }, null);
        new Thread(task).start();
    }

    public void stop() {
        try {
            if (task != null) {
                canceled = true;
                task.cancel(true);
                task = null;
            }
            if (terminal != null) {
                terminal.close();
                terminal = null;
            }
        } catch (IOException ex) {
            // ignore
        }
    }

    public static void appendException(PrintWriter writer, @NonNull Throwable ex) {
        writer.append(String.format(PROP_FORMAT_ERROR, ex.getMessage())).println();
    }
}
