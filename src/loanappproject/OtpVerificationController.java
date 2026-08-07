package loanappproject;

import emailverification.EmailService;
import emailverification.OTPManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller for OtpVerification.fxml.
 *
 * Flow: SignUpController sends the first OTP itself (on a background thread,
 * while the Create account button shows a spinner), then opens this popup
 * and calls setRecipient(..., true) to tell it the email already went out -
 * this just fills in the label instead of sending a second one. Once the
 * popup closes, SignUpController checks isVerified() to decide whether to
 * actually create the account - so nothing gets written to the database
 * until the email has been proven real by the user typing back the code
 * sent to it.
 */
public class OtpVerificationController implements Initializable {

    @FXML
    private Label emailLabel;
    @FXML
    private TextField otpField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button confirmButton;

    private final EmailService emailService = new EmailService();

    private String email;
    private String recipientName;
    private boolean verified = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    /**
     * Called by SignUpController right after this popup is loaded.
     *
     * @param otpAlreadySent true if the caller already sent the first OTP
     * itself (this is what SignUpController does now, on a background
     * thread, before opening this popup) - in that case this just fills in
     * the label. Pass false if this popup should send the first code itself.
     */
    public void setRecipient(String email, String recipientName, boolean otpAlreadySent) {
        this.email = email;
        this.recipientName = recipientName;
        emailLabel.setText("We sent a 6-digit code to " + email + ". Enter it below.");
        if (!otpAlreadySent) {
            emailService.sendRegistrationOtp(email, recipientName);
        }
    }

    /** SignUpController reads this after showAndWait() returns to decide whether to create the account. */
    public boolean isVerified() {
        return verified;
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        String submitted = otpField.getText().trim();

        if (submitted.isEmpty()) {
            showError("Enter the code we sent you first.");
            return;
        }

        boolean correct = emailService.verifyOtp(email, submitted);

        if (correct) {
            verified = true;
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } else {
            showError("That code's incorrect or has expired. Try again, or resend a new one.");
            otpField.clear();
        }
    }

    @FXML
    private void handleResend(ActionEvent event) {
        OTPManager.invalidate(email);
        emailService.sendRegistrationOtp(email, recipientName);
        otpField.clear();
        showError("A new code was sent to " + email + ".");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
