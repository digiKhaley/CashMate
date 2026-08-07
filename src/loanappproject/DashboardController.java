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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kingsley Ezealisiobi
 */
public class DashboardController implements Initializable {

    @FXML
    private Label profileName;
    @FXML
    private Label kycStatusBadge;

    private String currentName;
    private String currentEmail;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        if (Session.currentFirstName != null) {
            profileName.setText(Session.currentFirstName);
        }

        refreshKycBadge();
    }

    private void refreshKycBadge() {
        String status = KYCService.getStatus(Session.currentUserEmail);

        switch (status.toLowerCase()) {
            case "verified":
                kycStatusBadge.setText("KYC Verified");
                kycStatusBadge.setStyle("-fx-background-color: #d4edda; -fx-text-fill: green; -fx-background-radius: 12; -fx-padding: 4 12; -fx-font-size: 15px; -fx-font-weight: bold;");
                break;
            case "pending":
                kycStatusBadge.setText("KYC Pending");
                kycStatusBadge.setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #c77700; -fx-background-radius: 12; -fx-padding: 4 12; -fx-font-size: 15px; -fx-font-weight: bold;");
                break;
            case "rejected":
                kycStatusBadge.setText("KYC Rejected");
                kycStatusBadge.setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #b02a37; -fx-background-radius: 12; -fx-padding: 4 12; -fx-font-size: 15px; -fx-font-weight: bold;");
                break;
            default:
                kycStatusBadge.setText("KYC Unverified");
                kycStatusBadge.setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #b02a37; -fx-background-radius: 12; -fx-padding: 4 12; -fx-font-size: 15px; -fx-font-weight: bold;");
                break;
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
    private void handleWalletBalance() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("WalletBreakdown.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Wallet Balance");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

//            refreshWalletButton();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleContinueUser() {

        try {

            String kycStatus = KYCService.getStatus(Session.currentUserEmail);

            if (!"Verified".equalsIgnoreCase(kycStatus)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("KYCUnverifiedPopup.fxml"));
                Parent popupRoot = loader.load();

                KYCUnverifiedPopupController controller = loader.getController();
                controller.setMainStage((Stage) profileName.getScene().getWindow());
                controller.setStatus(kycStatus);

                Stage popupStage = new Stage();
                popupStage.setScene(new Scene(popupRoot));
                popupStage.setTitle("KYC Required");
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.showAndWait();

                refreshKycBadge();
                return;
            }

            boolean hasLoan = LoanService.hasUserTakenLoan(Session.currentUserEmail);

            Parent root;

            if (hasLoan) {
                root = FXMLLoader.load(getClass().getResource("MyLoanSuccess.fxml"));
            } else {
                root = FXMLLoader.load(getClass().getResource("MyLoan.fxml"));
            }

            Stage stage = (Stage) profileName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate - User");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
