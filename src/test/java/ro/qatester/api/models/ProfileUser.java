package ro.qatester.api.models;

public class ProfileUser {

    private String id;
    private String first_name;
    private String last_name;
    private String username;
    private String email;
    private String signup_date;
    private String profile_pic;

    public ProfileUser() {
    }

    public String getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getSignup_date() {
        return signup_date;
    }

    public String getProfile_pic() {
        return profile_pic;
    }
}