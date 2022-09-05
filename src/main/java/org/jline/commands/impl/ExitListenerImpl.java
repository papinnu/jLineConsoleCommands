package org.jline.commands.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.NonNull;
import org.jline.commands.CommandsConsole;
import org.jline.commands.impl.commands.ExitCommandHandler;

public class ExitListenerImpl implements ActionListener {

    private final CommandsConsole console;

    public ExitListenerImpl(@NonNull CommandsConsole console) {
        this.console = console;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ExitCommandHandler.COMMAND_NAME.equalsIgnoreCase(e.getActionCommand())) {
            try {
                console.stop();
            } catch (Exception ex) {
                Logger.getLogger(ExitListenerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
