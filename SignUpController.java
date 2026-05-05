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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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

        if (!privacyCheckBox.isSelected()) {
            showPopup("TermsRequired.fxml", "Terms & Conditions");
            return;
        }

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
