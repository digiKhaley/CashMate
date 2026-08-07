/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package loanappproject;

import emailverification.EmailExistenceChecker;
import emailverification.EmailService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.*;
import javafx.scene.control.CheckBox;

/**
 * FXML Controller class
 *
 * @author Kingsley Ezealisiobi
 */
public class SignUpController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField email;
    @FXML
    private TextField phone;
    @FXML
    private TextField password;
    @FXML
    private CheckBox privacyCheckBox;
    @FXML
    private Button createAccountButton;

    private boolean privacyPolicyViewed = false;

    private final EmailService emailService = new EmailService();

    private String createAccountButtonDefaultText;

    private ImageView createAccountSpinner;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        createAccountButtonDefaultText = createAccountButton.getText();

        Image spinnerImage = new Image(getClass().getResourceAsStream("IMAGES/icons8-loading_b.gif"));
        createAccountSpinner = new ImageView(spinnerImage);
        createAccountSpinner.setFitWidth(22);
        createAccountSpinner.setFitHeight(22);
        createAccountSpinner.setPreserveRatio(true);
    }

    private void setButtonLoading(boolean loading) {
        if (loading) {
            createAccountButton.setText(null);
            createAccountButton.setGraphic(createAccountSpinner);
            createAccountButton.setDisable(true);
        } else {
            createAccountButton.setGraphic(null);
            createAccountButton.setText(createAccountButtonDefaultText);
            createAccountButton.setDisable(false);
        }
    }

    private void showPopup(String fileName, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fileName));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePrivacyPolicy() {
        showPopup("PrivacyPolicy.fxml", "Privacy Policy");
        privacyPolicyViewed = true;
    }

    private void showTermsRequiredPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TermsRequired.fxml"));
            Parent root = loader.load();

            TermsRequiredController controller = loader.getController();
            controller.setOnPolicyViewedCallback(() -> privacyPolicyViewed = true);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Terms & Conditions");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean verifyEmailWithOtp(String email, String firstName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OtpVerification.fxml"));
            Parent root = loader.load();

            OtpVerificationController controller = loader.getController();
            controller.setRecipient(email, firstName, true);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Verify your email");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            return controller.isVerified();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void handleCreateaccount() {
        if (firstName.getText().trim().isEmpty()
                || lastName.getText().trim().isEmpty()
                || email.getText().trim().isEmpty()
                || phone.getText().trim().isEmpty()
                || password.getText().trim().isEmpty()) {

            showPopup("AllFieldsRequired.fxml", "Warning");
            return;
        }

        String first = firstName.getText().trim();
        String last = lastName.getText().trim();
        String ema = email.getText().trim();
        String phn = phone.getText().trim();
        String pass = password.getText().trim();

        if (!ema.matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) {
            showPopup("InvalidEmail.fxml", "Invalid Email");
            return;
        }

        if (!phn.matches("^(\\+234|234|0)[789][01]\\d{8}$")) {
            showPopup("InvalidPhone.fxml", "Invalid Phone");
            return;
        }

        if (phn.startsWith("0")) {
            phn = "+234" + phn.substring(1);
        } else if (phn.startsWith("234")) {
            phn = "+" + phn;
        }

        if (!phn.matches("^\\+234[789][01]\\d{8}$")) {
            showPopup("InvalidPhone.fxml", "Invalid Phone");
            return;
        }

        if (!privacyCheckBox.isSelected() || !privacyPolicyViewed) {
            showTermsRequiredPopup();
            return;
        }
        final String finalEma = ema;
        final String finalFirst = first;
        final String finalLast = last;
        final String finalPhn = phn;
        final String finalPass = pass;

        Task<Boolean> otpPrecheckTask = new Task<>() {
            @Override
            protected Boolean call() {

                if (!EmailExistenceChecker.isDeliverable(finalEma)) {
                    return false;
                }

                emailService.sendRegistrationOtp(finalEma, finalFirst);
                return true;
            }
        };

        otpPrecheckTask.setOnSucceeded(e -> {
            setButtonLoading(false);

            boolean deliverable = otpPrecheckTask.getValue();
            if (!deliverable) {
                showPopup("EmailUnreachable.fxml", "Invalid Email");
                return;
            }

            boolean verified = verifyEmailWithOtp(finalEma, finalFirst);
            if (!verified) {
                return;
            }

            finishAccountCreation(finalFirst, finalLast, finalEma, finalPhn, finalPass);
        });

        otpPrecheckTask.setOnFailed(e -> {
            setButtonLoading(false);
            Throwable ex = otpPrecheckTask.getException();
            if (ex != null) {
                ex.printStackTrace();
            }
            showPopup("EmailUnreachable.fxml", "Invalid Email");
        });

        setButtonLoading(true);

        Thread precheckThread = new Thread(otpPrecheckTask, "signup-otp-precheck");
        precheckThread.setDaemon(true);
        precheckThread.start();
    }

    private void finishAccountCreation(String first, String last, String ema, String phn, String pass) {
        try {
            Connection conn = DBConnect.getConnection();

            String sql = "INSERT INTO users (first_name, last_name, email, phone_number, password) VALUES (?, ?, ?, ?, ?)";

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, first);
            pst.setString(2, last);
            pst.setString(3, ema);
            pst.setString(4, phn);
            pst.setString(5, pass);

            pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        showPopup("AccountCreated.fxml", "Success");

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();

            controller.setAccountCreated(true);

            Stage stage = (Stage) firstName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate - Login");

        } catch (Exception e) {
            e.printStackTrace();
        }

        clearForm();
    }

    private void clearForm() {
        firstName.clear();
        lastName.clear();
        email.clear();
        password.clear();
    }

    @FXML
    private void signIn() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));

            Stage stage = (Stage) firstName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate - Login");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
