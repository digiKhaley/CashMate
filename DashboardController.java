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
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kingsley Ezealisiobi
 */
public class DashboardController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Label profileName;

    private String currentName;
    private String currentEmail;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        if (Session.currentFirstName != null) {
            profileName.setText(Session.currentFirstName);
        }
    }

    public void logoutToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));

            Stage stage = (Stage) profileName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate - Login");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void profile() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Logout.fxml"));
            Parent root = loader.load();

            LogoutController controller = loader.getController();

            controller.setDashboardController(this);

            controller.setUserDetails(Session.currentFirstName, Session.currentUserEmail);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Logout");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserDetails(String name, String email) {
        this.currentName = name;
        this.currentEmail = email;
        profileName.setText(name);
    }

    @FXML
    private void handleContinueUser() {
//        try {
//            Parent root = FXMLLoader.load(getClass().getResource("MyLoan.fxml"));
//
//            Stage stage = (Stage) profileName.getScene().getWindow();
//            stage.setScene(new Scene(root));
//            stage.setTitle("CashMate - User");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {

            boolean hasLoan = LoanService.hasUserTakenLoan(currentEmail);

            Parent root;

            if (hasLoan) {
                // 🔥 User already has loan → go to success page
                root = FXMLLoader.load(getClass().getResource("MyLoanSuccess.fxml"));
            } else {
                // 🔥 No loan yet → go to MyLoan page
                root = FXMLLoader.load(getClass().getResource("MyLoan.fxml"));
            }

            Stage stage = (Stage) profileName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate - User");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdmin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AdminAccess.fxml"));

            Stage stage = (Stage) profileName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate - Admin Access");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
