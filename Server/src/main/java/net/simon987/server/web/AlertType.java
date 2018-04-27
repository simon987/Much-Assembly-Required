package net.simon987.server.web;

public enum AlertType {

    SUCCESS("alert-success"),
    INFO("alert-info"),
    WARNING("alert-info"),
    DANGER("alert-danger"),
    PRIMARY("alert-primary"),
    SECONDARY("alert-secondary"),
    DARK("alert-dark");

    public String name;

    AlertType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
