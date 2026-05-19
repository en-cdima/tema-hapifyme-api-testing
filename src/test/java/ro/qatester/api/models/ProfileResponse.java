package ro.qatester.api.models;

public class ProfileResponse {

    private String status;
    private String message;
    private ProfileUser user;

    public ProfileResponse() {
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ProfileUser getUser() {
        return user;
    }
}