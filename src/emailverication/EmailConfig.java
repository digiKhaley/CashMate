package emailverification;

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
