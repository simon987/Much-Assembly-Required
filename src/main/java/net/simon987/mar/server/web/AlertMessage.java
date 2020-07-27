package net.simon987.mar.server.web;

public class AlertMessage {

    private final String message;
    private final AlertType type;

    public AlertMessage(String message, AlertType type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public AlertType getType() {
        return type;
    }
}
