/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package loanappproject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Kingsley Ezealisiobi
 */
public class WalletBreakdownController implements Initializable {

    @FXML
    private Text balanceText;
    @FXML
    private Label availableLabel;
    @FXML
    private Label pendingLabel;
    @FXML
    private Label repaidLabel;
    @FXML
    private Label messageLabel;
    @FXML
    private Button withdrawButton;

    private boolean payoutAccountLinked = false; // TODO: wire up to real account-linking status once that exists

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LoanService.LatestLoan latestLoan = LoanService.getLatestLoan(Session.currentUserEmail);
        if (latestLoan == null) {
            showEmptyState("You haven't requested a loan yet.", "#888888");
            return;
        }
        switch (latestLoan.status.toLowerCase()) {
            case "approved":
                showApprovedState(latestLoan.amount);
                break;
            case "pending":
                showEmptyState("Your loan request is pending approval.", "#c77700");
                break;
            case "rejected":
                showEmptyState("Your loan was not approved.", "#b02a37");
                break;
            default:
                showEmptyState("You haven't requested a loan yet.", "#888888");
                break;
        }
    }

    private void showEmptyState(String message, String textColor) {
        setAmount(balanceText, "₦0.00", "#147211", true);
        setAmount(availableLabel, "₦0.00", "#333333", true);
        setAmount(pendingLabel, "₦0.00", "#333333", true);
        setAmount(repaidLabel, "₦0.00", "#333333", true);
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + textColor + ";");
        setWithdrawVisible(false);
    }

    private void showApprovedState(double amount) {
        String formatted = "₦" + String.format("%,.2f", amount);
        setAmount(balanceText, formatted, "#147211", false);
        setAmount(availableLabel, formatted, "#1a2b4a", false);
        setAmount(pendingLabel, "₦0.00", "#333333", true);
        setAmount(repaidLabel, "₦0.00", "#333333", true);
        messageLabel.setText("Your loan has been approved and disbursed to your wallet.");
        messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #147211;"); // green
        setWithdrawVisible(amount > 0);
    }

    private void setWithdrawVisible(boolean visible) {
        withdrawButton.setVisible(visible);
        withdrawButton.setManaged(visible);
    }

    private void setAmount(Text node, String value, String color, boolean struckThrough) {
        node.setText(value);
        node.setStyle("-fx-fill: " + color + "; -fx-strikethrough: " + struckThrough + ";");
    }

    private void setAmount(Label node, String value, String color, boolean struckThrough) {
        node.setText(value);
        node.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-strikethrough: " + struckThrough + ";");
    }

    @FXML
    private void handleWithdraw(javafx.event.ActionEvent event) {
        if (!payoutAccountLinked) {
            openPopup("NoAccountLinkedPopup.fxml", event);
            return;
        }
        // TODO: real withdraw flow once a payout account can actually be linked
    }

    private void openPopup(String fxmlName, javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName));
            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
