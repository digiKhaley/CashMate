package emailverification;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Sends CashMate's transactional emails via SendGrid's v3 Mail Send API
 * (HTTPS, port 443) using java.net.http.HttpClient - no extra JARs needed.
 *
 * This is the class the rest of the app (RequestLoanController,
 * SignUpController, etc.) should call into - it does NOT generate or
 * validate OTPs itself, it only sends what it's given. Pair it with
 * OTPManager for generating/validating the actual codes, and
 * EmailExistenceChecker before sending to catch addresses that don't exist.
 */
public class EmailService {

    private static final String SENDGRID_ENDPOINT = "https://api.sendgrid.com/v3/mail/send";
    private static final String FROM_NAME = "CashMate";

    private final HttpClient httpClient;

    public EmailService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /** Low-level send: builds and POSTs one HTML email to SendGrid. Returns false instead of throwing on failure. */
    public boolean sendHtmlEmail(String toAddress, String subject, String htmlBody) {
        try {
            String json = "{"
                    + "\"personalizations\":[{\"to\":[{\"email\":\"" + jsonEscape(toAddress) + "\"}]}],"
                    + "\"from\":{\"email\":\"" + jsonEscape(EmailConfig.getSenderEmail()) + "\",\"name\":\"" + jsonEscape(FROM_NAME) + "\"},"
                    + "\"subject\":\"" + jsonEscape(subject) + "\","
                    + "\"content\":[{\"type\":\"text/html\",\"value\":\"" + jsonEscape(htmlBody) + "\"}]"
                    + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SENDGRID_ENDPOINT))
                    .header("Authorization", "Bearer " + EmailConfig.getApiKey())
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(15))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // SendGrid returns 202 Accepted with an empty body on success.
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return true;
            }

            System.err.println("SendGrid send failed (HTTP " + response.statusCode() + "): " + response.body());
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Escapes a string for safe embedding inside a JSON string value (quotes, backslashes, control chars). */
    private static String jsonEscape(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    // ---- Convenience methods for each email CashMate sends (unchanged) ----

    /** Generates + stores an OTP via OTPManager, emails it, and returns the OTP (mainly for logging/testing). */
    public String sendRegistrationOtp(String toAddress, String recipientName) {
        String otp = OTPManager.generateAndStore(toAddress);
        sendHtmlEmail(toAddress, "Verify your CashMate account", EmailTemplate.registrationOtp(recipientName, otp));
        return otp;
    }

    public String sendForgotPasswordOtp(String toAddress, String recipientName) {
        String otp = OTPManager.generateAndStore(toAddress);
        sendHtmlEmail(toAddress, "Reset your CashMate password", EmailTemplate.forgotPasswordOtp(recipientName, otp));
        return otp;
    }

    /** Checks a code the user typed in against what OTPManager has on file. */
    public boolean verifyOtp(String email, String submittedOtp) {
        return OTPManager.validate(email, submittedOtp);
    }

    public boolean sendLoanPending(String toAddress, String recipientName, String amount) {
        return sendHtmlEmail(toAddress, "We've received your loan request",
                EmailTemplate.loanPending(recipientName, amount));
    }

    public boolean sendLoanApproved(String toAddress, String recipientName, String amount, String repaymentDate) {
        return sendHtmlEmail(toAddress, "Your CashMate loan has been approved",
                EmailTemplate.loanApproved(recipientName, amount, repaymentDate));
    }

    public boolean sendLoanRejected(String toAddress, String recipientName, String reason) {
        return sendHtmlEmail(toAddress, "Update on your CashMate loan request",
                EmailTemplate.loanRejected(recipientName, reason));
    }

    public boolean sendKycPending(String toAddress, String recipientName) {
        return sendHtmlEmail(toAddress, "We've received your KYC documents",
                EmailTemplate.kycPending(recipientName));
    }

    public boolean sendKycApproved(String toAddress, String recipientName) {
        return sendHtmlEmail(toAddress, "Your CashMate KYC has been verified",
                EmailTemplate.kycApproved(recipientName));
    }

    public boolean sendKycRejected(String toAddress, String recipientName, String reason) {
        return sendHtmlEmail(toAddress, "Action needed: CashMate KYC verification",
                EmailTemplate.kycRejected(recipientName, reason));
    }
}
