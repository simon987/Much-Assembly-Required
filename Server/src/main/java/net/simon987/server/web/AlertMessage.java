package net.simon987.server.web;

public class AlertMessage {

    private String message;
    private AlertType type;

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
