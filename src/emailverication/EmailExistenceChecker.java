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

/**
 * Checks whether a specific email address is actually deliverable, using
 * AbstractAPI's Email Reputation API (https://www.abstractapi.com/api/email-reputation-api)
 * instead of a bare MX-record lookup. Unlike checking the domain alone, this
 * can flag one exact address as undeliverable (doesn't exist, bounces,
 * disposable, etc.) rather than only confirming the domain accepts mail in
 * general.
 *
 * Credentials read from a -D system property or an env var, same pattern as
 * the rest of this package:
 *   abstractapi.email.key / ABSTRACTAPI_EMAIL_KEY -> your Email Reputation API key
 *
 * In NetBeans: Project Properties -> Run -> VM Options, add:
 *   -Dabstractapi.email.key=yourkeyhere
 *
 * Get a free key at https://www.abstractapi.com -> Email Reputation product
 * -> "View API keys" (free tier: 100 requests/month at time of writing).
 */
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

    /**
     * True ONLY when AbstractAPI explicitly comes back and confirms this
     * exact address as "deliverable". Everything else - a confirmed
     * "undeliverable", an "unknown" (AbstractAPI can't confirm the
     * mailbox - common for gmail.com specifically, since Google doesn't
     * give third-party checkers a reliable per-mailbox signal), a missing
     * API key, an HTTP error, or an unparseable response - blocks the
     * signup. SignUpController already shows EmailUnreachable.fxml
     * whenever this returns false, so all of those cases surface to the
     * user the same way a confirmed-fake address does.
     */
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

    /**
     * Pulls "status" out of the "email_deliverability" section of the JSON
     * response without needing a full JSON library - just enough parsing
     * for the one field this class actually needs.
     */
    private static String extractDeliverabilityStatus(String json) {
        int section = json.indexOf("\"email_deliverability\"");
        if (section < 0) {
            return null;
        }
        Matcher m = Pattern.compile("\"status\"\\s*:\\s*\"([^\"]*)\"").matcher(json.substring(section));
        return m.find() ? m.group(1) : null;
    }
}
