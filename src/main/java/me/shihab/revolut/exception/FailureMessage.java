package me.shihab.revolut.exception;

public enum FailureMessage {
    ACCOUNT_NOT_FOUND("No account found for this id");

    private String message;

    FailureMessage(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
