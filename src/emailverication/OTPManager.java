package emailverification;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class OTPManager {

    private static final int OTP_VALID_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 5;

    private static final ConcurrentHashMap<String, OtpEntry> store = new ConcurrentHashMap<>();

    private OTPManager() {
    }

    public static String generateAndStore(String email) {
        String otp = OTPGenerator.generate();
        Instant expiry = Instant.now().plusSeconds(OTP_VALID_MINUTES * 60L);
        store.put(normalize(email), new OtpEntry(otp, expiry, 0));
        return otp;
    }

    public static boolean validate(String email, String submittedOtp) {
        String key = normalize(email);
        OtpEntry entry = store.get(key);

        if (entry == null) {
            return false;
        }

        if (Instant.now().isAfter(entry.expiry)) {
            store.remove(key);
            return false;
        }

        if (entry.attempts >= MAX_ATTEMPTS) {
            store.remove(key);
            return false;
        }

        entry.attempts++;

        if (entry.otp.equals(submittedOtp)) {
            store.remove(key);
            return true;
        }

        return false;
    }

    public static void invalidate(String email) {
        store.remove(normalize(email));
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase();
    }

    private static class OtpEntry {

        final String otp;
        final Instant expiry;
        int attempts;

        OtpEntry(String otp, Instant expiry, int attempts) {
            this.otp = otp;
            this.expiry = expiry;
            this.attempts = attempts;
        }
    }
}
