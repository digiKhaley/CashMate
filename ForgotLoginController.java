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
public class ForgotLoginController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextField lastName;
    @FXML
    private TextField email;
    @FXML
    private TextField phnNumber;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    public void backButton() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Stage stage = (Stage) lastName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate - Login");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void submitButton() {
        String lName = lastName.getText().trim();
        String mail = email.getText().trim();
        String phone = phnNumber.getText().trim();

        if (lName.isEmpty() || mail.isEmpty() || phone.isEmpty()) {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("AllFieldsRequired.fxml"));
                Parent root = loader.load();

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

            String query = "SELECT * FROM users WHERE email=? AND last_name=? AND phone_number=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, mail);
            pst.setString(2, lName);
            pst.setString(3, phone);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

//                Session.currentUserEmail  = mail;
//                Session.role = role;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ResetLogin.fxml"));
                Parent root = loader.load();

                ResetLoginController controller = loader.getController();

                controller.setUserData(mail, role);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Reset Password");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                if (controller.isResetSuccessful()) {

                    try {
                        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
                        Parent loginRoot = loginLoader.load();

                        LoginController loginController = loginLoader.getController();
                        loginController.setPasswordChanged(true);

                        Stage currentStage = (Stage) lastName.getScene().getWindow();
                        currentStage.setScene(new Scene(loginRoot));
                        currentStage.setTitle("CashMate - Login");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("User not found. Please check your details.");
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
