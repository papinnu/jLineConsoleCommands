package org.jline.commands;

public class ConsoleException extends Exception {

    public ConsoleException(String message) {
        super(message);
    }

    public ConsoleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConsoleException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getLocalizedMessage() {
        return super.getMessage();
    }

}
