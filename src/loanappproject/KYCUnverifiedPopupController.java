/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package loanappproject;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kingsley Ezealisiobi
 */
public class KYCUnverifiedPopupController implements Initializable {

    @FXML
    private Label titleLabel;
    @FXML
    private Label bodyLabel;
    @FXML
    private Button verifyNowButton;

    private Stage mainStage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    /**
     * The Dashboard passes its own stage in here (same pattern as
     * LogoutController.setDashboardController) so "Verify KYC Now" can
     * navigate the MAIN window, not this popup, to the KYC page.
     */
    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    /**
     * Adapts the popup to the user's actual KYC status - same idea as the
     * KYC page itself: Pending shows a different message than Unverified,
     * and there's nothing left to "verify now" once it's already submitted.
     */
    public void setStatus(String status) {
        switch (status.toLowerCase()) {
            case "pending":
                titleLabel.setText("KYC Pending");
                titleLabel.setTextFill(javafx.scene.paint.Color.web("#c77700"));
                bodyLabel.setText("Your KYC is currently under review. We'll let you know once it's verified - you'll be able to request a loan then.");
                verifyNowButton.setVisible(false);
                verifyNowButton.setManaged(false);
                break;
            case "rejected":
                titleLabel.setText("KYC Rejected");
                bodyLabel.setText("Your last KYC submission was rejected. Please resubmit your ID to become eligible for a loan.");
                verifyNowButton.setVisible(true);
                verifyNowButton.setManaged(true);
                break;
            default: // Unverified
                titleLabel.setText("KYC Unverified");
                bodyLabel.setText("Verify your KYC before you're eligible to get a loan.");
                verifyNowButton.setVisible(true);
                verifyNowButton.setManaged(true);
                break;
        }
    }

    @FXML
    private void handleVerifyNow(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("KYC.fxml"));

            Stage popupStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            popupStage.close();

            if (mainStage != null) {
                mainStage.setScene(new Scene(root));
                mainStage.setTitle("CashMate - Verify KYC");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
