package emailverification;

public class EmailTemplate {

    private EmailTemplate() {
    }

    private static String wrapper(String title, String bodyHtml) {
        return "<html><body style=\"font-family: Arial, sans-serif; background-color:#f4f4f4; padding:20px;\">"
                + "<div style=\"max-width:480px; margin:0 auto; background:#ffffff; border-radius:12px; padding:30px;\">"
                + "<h2 style=\"color:#147211; margin-top:0;\">" + title + "</h2>"
                + bodyHtml
                + "<p style=\"font-size:12px; color:#999; margin-top:30px;\">CashMate &mdash; this is an automated message, please do not reply.</p>"
                + "</div></body></html>";
    }

    public static String registrationOtp(String name, String otp) {
        String body = "<p>Hi " + name + ",</p>"
                + "<p>Use the code below to verify your CashMate account:</p>"
                + "<p style=\"font-size:28px; font-weight:bold; letter-spacing:4px; color:#147211; text-align:center;\">" + otp + "</p>"
                + "<p>This code expires in 10 minutes. If you didn't request this, you can ignore this email.</p>";
        return wrapper("Verify your account", body);
    }

    public static String forgotPasswordOtp(String name, String otp) {
        String body = "<p>Hi " + name + ",</p>"
                + "<p>Use the code below to reset your CashMate password:</p>"
                + "<p style=\"font-size:28px; font-weight:bold; letter-spacing:4px; color:#f55a12; text-align:center;\">" + otp + "</p>"
                + "<p>This code expires in 10 minutes. If you didn't request this, please secure your account.</p>";
        return wrapper("Reset your password", body);
    }

    public static String loanPending(String name, String amount) {
        String body = "<p>Hi " + name + ",</p>"
                + "<p>We've received your loan request for <strong>" + amount + "</strong> and it's now <strong style=\"color:#f55a12;\">under review</strong>.</p>"
                + "<p>We'll email you again as soon as a decision has been made &mdash; usually within 24 hours.</p>";
        return wrapper("Loan request received", body);
    }

    public static String loanApproved(String name, String amount, String repaymentDate) {
        String body = "<p>Hi " + name + ",</p>"
                + "<p>Good news &mdash; your loan request has been <strong style=\"color:#147211;\">approved</strong>.</p>"
                + "<p>Amount: <strong>" + amount + "</strong><br>Repayment date: <strong>" + repaymentDate + "</strong></p>"
                + "<p>Please make sure to repay on time to keep your account in good standing.</p>";
        return wrapper("Loan approved", body);
    }

    public static String loanRejected(String name, String reason) {
        String body = "<p>Hi " + name + ",</p>"
                + "<p>We're sorry to let you know your loan request was <strong style=\"color:#f55a12;\">not approved</strong> at this time.</p>"
                + "<p>Reason: " + reason + "</p>"
                + "<p>You're welcome to apply again once the issue above has been addressed.</p>";
        return wrapper("Loan request update", body);
    }

    public static String kycPending(String name) {
        String body = "<p>Hi " + name + ",</p>"
                + "<p>Thanks for submitting your KYC documents. They're now <strong style=\"color:#f55a12;\">under review</strong>.</p>"
                + "<p>We'll let you know as soon as verification is complete &mdash; you don't need to do anything else for now.</p>";
        return wrapper("KYC documents received", body);
    }

    public static String kycApproved(String name) {
        String body = "<p>Hi " + name + ",</p>"
                + "<p>Your KYC verification has been <strong style=\"color:#147211;\">approved</strong>. You now have full access to CashMate.</p>";
        return wrapper("KYC verified", body);
    }

    public static String kycRejected(String name, String reason) {
        String body = "<p>Hi " + name + ",</p>"
                + "<p>Your KYC verification could not be approved.</p>"
                + "<p>Reason: " + reason + "</p>"
                + "<p>Please resubmit your documents so we can review again.</p>";
        return wrapper("KYC verification issue", body);
    }
}
