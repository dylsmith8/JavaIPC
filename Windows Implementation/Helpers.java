/*
    Author: Dylan Smith
    Date: 16 October 2017
*/

public class Helpers {
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.length() == 0 || value.equals("");
    }
}