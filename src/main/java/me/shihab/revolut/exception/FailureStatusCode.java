package me.shihab.revolut.exception;


public enum FailureStatusCode {
    ACCOUNT_NOT_FOUND(404);

    private int statusCode;

    FailureStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int statusCode() {
        return statusCode;
    }
}
