package loanappproject;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

public class LoanDetailsController implements Initializable {

    @FXML
    private Label amountLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Label interestLabel;
    @FXML
    private Label repaymentLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadLoanDetails();
    }

    private void loadLoanDetails() {

        String query = "SELECT * FROM loans WHERE email = ? ORDER BY id DESC LIMIT 1";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, Session.currentUserEmail);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                amountLabel.setText("Amount: ₦" + String.format("%,.2f", rs.getDouble("loan_amount")));
                durationLabel.setText("Duration: " + rs.getString("duration"));
                interestLabel.setText("Interest: ₦" + rs.getDouble("total_interest"));
                repaymentLabel.setText("Repayment: ₦" + rs.getDouble("total_repayment"));
                dateLabel.setText("Date: " + rs.getString("created_at"));
                statusLabel.setText("Status: " + rs.getString("status"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
