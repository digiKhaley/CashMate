/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package loanappprojectadmin;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.*;
import loanappproject.DBConnect;

/**
 * FXML Controller class - entry point of the standalone admin app.
 *
 * @author Kingsley Ezealisiobi
 */
public class AdminAccessController implements Initializable {

    @FXML
    private PasswordField passwordField;

    private boolean passwordChanged = false;
    private boolean initialized = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initialized = true;
        checkAndShowAlert();
    }

    /**
     * Called by ForgotPasswordController after a successful reset, so this
     * screen can show the "password changed" confirmation alert.
     */
    public void setPasswordChanged(boolean status) {
        this.passwordChanged = status;
        checkAndShowAlert();
    }

    private void checkAndShowAlert() {
        if (initialized && passwordChanged) {
            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Admin password changed successfully!");
                alert.showAndWait();
            });
        }
    }

    private void showPopup(String fileName, String title) {
        try {
            // Reuses the shared popup FXMLs that already live in loanappproject
            Parent root = FXMLLoader.load(getClass().getResource("../loanappproject/" + fileName));
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
    private void handleLogin() {
        String input = passwordField.getText();

        if (input.isEmpty()) {
            showPopup("FieldRequired.fxml", "Warning");
            return;
        }
        try {
            Connection conn = DBConnect.getConnection();

            String sql = "SELECT * FROM users WHERE role = 'admin' AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, input);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("AdminDashboard.fxml"));

                    Stage stage = (Stage) passwordField.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("CashMate Admin - Dashboard");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                showPopup("IncorrectPassword.fxml", "Warning");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        passwordField.clear();
    }

    @FXML
    private void handleForgotPassword() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ForgotPassword.fxml"));

            Stage stage = (Stage) passwordField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate Admin - Forgot Password");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This is now the standalone admin app's entry screen, so there is no
     * user-side page to "go back" to. The back button simply exits the app.
     */
    @FXML
    public void backButton() {
        Stage stage = (Stage) passwordField.getScene().getWindow();
        stage.close();
    }
}
