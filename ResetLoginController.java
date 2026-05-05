/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package loanappproject;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import java.sql.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kingsley Ezealisiobi
 */
public class ResetLoginController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextField newPassword;

    private String email;
    private String role;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setUserData(String email, String role) {
        this.email = email;
        this.role = role;
    }

    private boolean resetSuccessful = false;

    public boolean isResetSuccessful() {
        return resetSuccessful;
    }

    @FXML
    public void resetButton() {
        String newPass = newPassword.getText();

        if (newPass.isEmpty()) {

            try {
                Parent root = FXMLLoader.load(getClass().getResource("FieldRequired.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Warning");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }

        try {
            Connection con = DBConnect.getConnection();

            if ("admin".equals(role)) {
                String query = "UPDATE users SET password=? WHERE role='admin'";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, newPass);
                pst.executeUpdate();
            } else {
                String query = "UPDATE users SET password=? WHERE email=?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, newPass);
                pst.setString(2, email);
                pst.executeUpdate();
            }

            try {

                resetSuccessful = true;

                Session.currentUserEmail = null;
                Session.role = null;

                Stage stage = (Stage) newPassword.getScene().getWindow();

                stage.close();
            } catch (Exception e) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to update password. Please try again.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
