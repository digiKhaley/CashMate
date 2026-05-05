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
import javafx.scene.control.PasswordField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.*;

/**
 * FXML Controller class
 *
 * @author Kingsley Ezealisiobi
 */
public class ResetPasswordController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private PasswordField newPassword;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private boolean resetSuccessful = false;

    public boolean isResetSuccessful() {
        return resetSuccessful;
    }

    @FXML
    public void resetButton() {
        String newPass = newPassword.getText().trim();

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
        } else {

            try {
                Connection conn = DBConnect.getConnection();

                String sql = "UPDATE users SET password = ? WHERE role = 'admin'";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, newPass);

                pst.executeUpdate();

                try {

                    resetSuccessful = true;

                    Stage stage = (Stage) newPassword.getScene().getWindow();
                    stage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        newPassword.clear();
    }
}
