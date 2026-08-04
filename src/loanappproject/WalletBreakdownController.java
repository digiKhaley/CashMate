/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package loanappproject;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        LoanService.LatestLoan latestLoan = LoanService.getLatestLoan(Session.currentUserEmail);

        if (latestLoan == null) {
            // Never requested a loan at all - neutral grey
            showEmptyState("You haven't requested a loan yet.", "#888888");
            return;
        }

        switch (latestLoan.status.toLowerCase()) {
            case "approved":
                showApprovedState(latestLoan.amount);
                break;
            case "pending":
                showEmptyState("Your loan request is pending approval.", "#c77700"); // orange
                break;
            case "rejected":
                showEmptyState("Your loan was not approved.", "#b02a37"); // dark red
                break;
            default:
                showEmptyState("You haven't requested a loan yet.", "#888888"); // grey
                break;
        }
    }

    /**
     * No usable balance yet - everything stays at ₦0.00, with a message
     * explaining why (no loan yet / pending / rejected), colored to match
     * the status.
     */
    private void showEmptyState(String message, String textColor) {
        balanceText.setText("₦0.00");
        availableLabel.setText("Available Balance: ₦0.00");
        pendingLabel.setText("Pending Disbursements: ₦0.00");
        repaidLabel.setText("Total Repaid: ₦0.00");
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + textColor + ";");
    }

    /**
     * Loan approved - the wallet becomes actually useful: balance reflects
     * the real disbursed amount. Total Repaid stays ₦0.00 since there's no
     * repayment tracking yet (a later feature, per your note).
     */
    private void showApprovedState(double amount) {
        String formatted = "₦" + String.format("%,.2f", amount);

        balanceText.setText(formatted);
        availableLabel.setText("Available Balance: " + formatted);
        pendingLabel.setText("Pending Disbursements: ₦0.00");
        repaidLabel.setText("Total Repaid: ₦0.00");
        messageLabel.setText("Your loan has been approved and disbursed to your wallet.");
        messageLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #147211;"); // green
    }

    @FXML
    private void handleClose(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
