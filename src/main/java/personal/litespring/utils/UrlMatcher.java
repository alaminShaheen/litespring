package personal.litespring.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlMatcher {
    // Convert full URL pattern with {id} to regex
    private static String convertToRegex(String urlPattern) {
        return urlPattern.replaceAll("\\{[^/]+\\}", "([^/]+)");  // Replace {id} with a regex group
    }

    // Method to check if partial URL matches full URL
    public static boolean matchUrl(String partialUrl, String fullUrlPattern) {
        // Convert fullUrlPattern with path variables to regex
        String regexPattern = convertToRegex(partialUrl);
        System.out.println("partialUrl = " + partialUrl);
        System.out.println("fullUrlPattern = " + fullUrlPattern);

        // Create a regex pattern from the full URL
        Pattern pattern = Pattern.compile(regexPattern);

        // Match the partial URL against the regex
        Matcher matcher = pattern.matcher(fullUrlPattern);

        return matcher.matches();  // Returns true if the URLs match
    }
}
