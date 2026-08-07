package emailverification;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailExistenceChecker {

    private static final String ENDPOINT = "https://emailreputation.abstractapi.com/v1/";
    private static final String API_KEY = readSetting("abstractapi.email.key", "ABSTRACTAPI_EMAIL_KEY");

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private EmailExistenceChecker() {
    }

    private static String readSetting(String systemPropertyKey, String envVarKey) {
        String fromSystemProperty = System.getProperty(systemPropertyKey);
        if (fromSystemProperty != null && !fromSystemProperty.isBlank()) {
            return fromSystemProperty;
        }
        return System.getenv(envVarKey);
    }

    public static boolean isDeliverable(String email) {
        if (API_KEY == null || API_KEY.isBlank()) {
            return false;
        }

        try {
            String url = ENDPOINT + "?api_key=" + API_KEY
                    + "&email=" + URLEncoder.encode(email, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return false;
            }

            String status = extractDeliverabilityStatus(response.body());
            return "deliverable".equalsIgnoreCase(status);

        } catch (Exception e) {
            return false;
        }
    }

    private static String extractDeliverabilityStatus(String json) {
        int section = json.indexOf("\"email_deliverability\"");
        if (section < 0) {
            return null;
        }
        Matcher m = Pattern.compile("\"status\"\\s*:\\s*\"([^\"]*)\"").matcher(json.substring(section));
        return m.find() ? m.group(1) : null;
    }
}
