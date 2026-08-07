package emailverification;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks issued OTPs in memory: who they belong to, when they expire, and how many
 * times they've been checked. This does NOT send email or generate the raw digits
 * itself - it just tracks what OTPGenerator produced and validates what the user
 * submits later.
 *
 * Note: this is in-memory only, so codes are lost on app restart and won't work
 * across multiple app instances. That's fine for a single-instance desktop app;
 * if CashMate ever runs as a multi-instance server, back this with the database
 * (e.g. a otp_codes table with email, code_hash, expires_at, attempts) instead.
 */
public class OTPManager {

    private static final int OTP_VALID_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 5;

    private static final ConcurrentHashMap<String, OtpEntry> store = new ConcurrentHashMap<>();

    private OTPManager() {
    }

    /** Generates a fresh OTP for the given email, stores it, and returns it so it can be emailed. */
    public static String generateAndStore(String email) {
        String otp = OTPGenerator.generate();
        Instant expiry = Instant.now().plusSeconds(OTP_VALID_MINUTES * 60L);
        store.put(normalize(email), new OtpEntry(otp, expiry, 0));
        return otp;
    }

    /**
     * Checks a submitted code against what's stored for that email.
     * Consumes the code (removes it) on success, or once it's expired / over the
     * attempt limit, so a code can never be reused.
     */
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

    /** Cancels any outstanding OTP for this email, e.g. if the user requests a new one. */
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
