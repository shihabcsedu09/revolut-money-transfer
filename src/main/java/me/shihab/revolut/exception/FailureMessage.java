package me.shihab.revolut.exception;

public enum FailureMessage {
    ACCOUNT_NOT_FOUND("No account found for this id"),
    SOURCE_ACCOUNT_NOT_FOUND("Source account not found for this id"),
    DESTINATION_ACCOUNT_NOT_FOUND("Destination account not found for this id"),
    NOT_ENOUGH_BALANCE("Not enough balance for this account");

    private String message;

    FailureMessage(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
