/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package loanappprojectadmin;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.sql.*;
import loanappproject.DBConnect;
import loanappproject.Loan;
import emailverification.EmailService;

public class DecisionPopupController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Text loanIdText;
    @FXML
    private Text applicantText;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private Loan currentLoan;
    private final EmailService emailService = new EmailService();

    public void setLoanData(Loan loan) {
        this.currentLoan = loan;

        loanIdText.setText(String.valueOf(loan.getId()));
        applicantText.setText(loan.getName());
    }

    @FXML
    private void handleApprove() {
        updateStatus("Approved");
    }

    @FXML
    private void handleReject() {
        updateStatus("Rejected");
    }

    private Runnable onUpdateCallback;

    public void setOnUpdateCallback(Runnable callback) {
        this.onUpdateCallback = callback;
    }

    private void updateStatus(String status) {
        String query = "UPDATE loans SET status=? WHERE id=?";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, status);
            ps.setInt(2, currentLoan.getId());

            ps.executeUpdate();

            String formattedAmount = "\u20a6" + String.format("%,.2f", currentLoan.getAmount());
            if ("Approved".equals(status)) {
                emailService.sendLoanApproved(currentLoan.getEmail(), currentLoan.getName(),
                        formattedAmount, currentLoan.getRepaymentDate());
            } else if ("Rejected".equals(status)) {
                emailService.sendLoanRejected(currentLoan.getEmail(), currentLoan.getName(),
                        "This request did not meet our current lending criteria.");
            }

            if (onUpdateCallback != null) {
                onUpdateCallback.run();
            }

            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) loanIdText.getScene().getWindow();
        stage.close();
    }

}
