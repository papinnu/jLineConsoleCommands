package org.jline.commands.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

public class CommandCompleter implements Completer {

    private final Map<String, Candidate> commands = new HashMap<>();

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        CommandFactory.getInstance().allCommands()
            .forEach(cmd -> commands.computeIfAbsent(cmd.getName(), k -> new Candidate(cmd.getName())));
        candidates.addAll(commands.values());
    }

}
