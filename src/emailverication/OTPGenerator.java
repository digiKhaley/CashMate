package emailverification;

import java.security.SecureRandom;

/**
 * Generates numeric one-time passcodes. Uses SecureRandom (not Random/Math.random)
 * since these codes are a security control.
 */
public class OTPGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private OTPGenerator() {
    }

    /** Generates the default 6-digit OTP, e.g. "042917". */
    public static String generate() {
        return generate(6);
    }

    public static String generate(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be positive");
        }
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            otp.append(RANDOM.nextInt(10));
        }
        return otp.toString();
    }
}
