/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package loanappproject;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.*;
import javafx.scene.control.Alert;

/**
 * FXML Controller class
 *
 * @author Kingsley Ezealisiobi
 */
public class LoginController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextField email;
    @FXML
    private TextField password;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        if (passwordChanged) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Password changed successfully!");
            alert.showAndWait();
        });
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
    public void forgotLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ForgotLogin.fxml"));

            Stage stage = (Stage) email.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate - Forgot Password");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean accountCreated = false;

    private boolean passwordChanged = false;

    public void setAccountCreated(boolean status) {
        this.accountCreated = status;

        if (status) {
            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Account created successfully!");
                alert.showAndWait();
            });
        }
    }

    public void setPasswordChanged(boolean status) {
        this.passwordChanged = status;
    }

    @FXML
    public void handleLogin() {
        if (email.getText().trim().isEmpty() || password.getText().trim().isEmpty()) {

            showPopup("AllFieldsRequired.fxml", "Warning");
            return;
        }

        String em = email.getText().trim();
        String pas = password.getText().trim();

        if (!em.matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) {
            showPopup("InvalidEmail.fxml", "Invalid Email");
            return;
        }

        try {
            Connection conn = DBConnect.getConnection();

            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, em);
            pst.setString(2, pas);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                String displayName;

                if ("admin".equalsIgnoreCase(role)) {
                    displayName = "Admin";
                } else {
                    displayName = rs.getString("first_name");
                }

                Session.currentUserEmail = em;
                Session.currentFirstName = displayName;
                Session.role = role;

                FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
                Parent root = loader.load();

                DashboardController controller = loader.getController();

                controller.setUserDetails(displayName, em);

                Stage stage = (Stage) email.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard");
            } else {
                showPopup("IncorrectPassword.fxml", "Login Failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        clearForm();
    }

    private void clearForm() {
        email.clear();
        password.clear();
    }

    @FXML
    public void signUp() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("SignUp.fxml"));

            Stage stage = (Stage) email.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate - Create Account");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
