package me.shihab.revolut.exception;

public class FailureResponse {
    private int statusCode;
    private String message;

    public FailureResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
