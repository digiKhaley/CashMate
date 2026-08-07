package emailverification;

import java.security.SecureRandom;

public class OTPGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private OTPGenerator() {
    }

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
