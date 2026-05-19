package ro.qatester.api.utils;

public class DataGenerator {

    private static final long timestamp = System.currentTimeMillis();

    public static String generateEmail() {
        return "test_" + timestamp + "@gmail.com";
    }

    public static String generatePassword() {
        return "Test1234!";
    }

    public static String generateFirstName() {
        return "Corneliu";
    }

    public static String generateLastName() {
        return "Dima";
    }

    public static long getTimestamp() {
        return timestamp;
    }
}
