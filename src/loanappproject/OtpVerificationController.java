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

    public void setRecipient(String email, String recipientName, boolean otpAlreadySent) {
        this.email = email;
        this.recipientName = recipientName;
        emailLabel.setText("We sent a 6-digit code to " + email + ". Enter it below.");
        if (!otpAlreadySent) {
            emailService.sendRegistrationOtp(email, recipientName);
        }
    }

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
