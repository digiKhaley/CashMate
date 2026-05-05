/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package loanappproject;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.*;

/**
 * FXML Controller class
 *
 * @author Kingsley Ezealisiobi
 */
public class AdminAccessController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private PasswordField passwordField;

    private boolean passwordChanged = false;
    private boolean initialized = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        initialized = true;
        checkAndShowAlert();
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
                    stage.setTitle("CashMate - Admin Dashboard");

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
            stage.setTitle("CashMate - Forgot Password");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void backButton() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));

            Stage stage = (Stage) passwordField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
