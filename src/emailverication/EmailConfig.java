package emailverification;

/**
 * Holds the SendGrid configuration used to send mail over HTTPS.
 *
 * Credentials are read as either a -D system property or an OS environment
 * variable - whichever is set - so nothing is hardcoded in source:
 *   sendgrid.api.key / SENDGRID_API_KEY     -> your SendGrid API key
 *   cashmate.mail.user / CASHMATE_MAIL_USER -> your verified sender address
 *
 * In NetBeans: right-click the project -> Properties -> Run -> VM Options, and add:
 *   -Dsendgrid.api.key=SG.yourkeyhere -Dcashmate.mail.user=you@example.com
 *
 * Setup on SendGrid's side (one-time):
 *   1. Settings -> API Keys -> Create API Key -> pick "Restricted Access" and
 *      enable "Mail Send" (or "Full Access" if you don't want to bother
 *      picking permissions) -> copy the key immediately, shown once.
 *   2. Settings -> Sender Authentication -> "Verify a Single Sender" -> enter
 *      the email you want CashMate to send FROM -> confirm the link SendGrid
 *      emails you. This is required before that address can send anything.
 */
public class EmailConfig {

    private static final String API_KEY = readSetting("sendgrid.api.key", "SENDGRID_API_KEY");
    private static final String SENDER_EMAIL = readSetting("cashmate.mail.user", "CASHMATE_MAIL_USER");

    private EmailConfig() {
    }

    private static String readSetting(String systemPropertyKey, String envVarKey) {
        String fromSystemProperty = System.getProperty(systemPropertyKey);
        if (fromSystemProperty != null && !fromSystemProperty.isBlank()) {
            return fromSystemProperty;
        }
        return System.getenv(envVarKey);
    }

    public static String getApiKey() {
        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IllegalStateException(
                "SendGrid API key not found. Set it in NetBeans under Project Properties -> Run -> "
                + "VM Options, e.g. -Dsendgrid.api.key=SG.yourkeyhere"
            );
        }
        return API_KEY;
    }

    public static String getSenderEmail() {
        if (SENDER_EMAIL == null || SENDER_EMAIL.isBlank()) {
            throw new IllegalStateException(
                "Sender email not found. Set it in NetBeans under Project Properties -> Run -> "
                + "VM Options, e.g. -Dcashmate.mail.user=you@example.com"
            );
        }
        return SENDER_EMAIL;
    }
}
