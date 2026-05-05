/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loanappproject;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.*;

/**
 *
 * @author Kingsley Ezealisiobi
 */
public class RequestLoanController implements Initializable {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField emailAddressField;
    @FXML
    private TextField homeAddressField;
    @FXML
    private TextField loanAmountField;

    @FXML
    private ComboBox<String> durationBox;

    @FXML
    private Text totalInterestText;
    @FXML
    private Text totalRepaymentText;
    @FXML
    private Text monthlyPaymentText;
    @FXML
    private Text repaymentDateText;

    private void loadUserData() {
        try {
            Connection conn = DBConnect.getConnection();

            String sql = "SELECT first_name, last_name, email, phone_number FROM users WHERE email = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, Session.currentUserEmail);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                firstNameField.setText(rs.getString("first_name"));
                lastNameField.setText(rs.getString("last_name"));
                emailAddressField.setText(rs.getString("email"));
                phoneNumberField.setText(rs.getString("phone_number"));
            }

            firstNameField.setEditable(false);
            lastNameField.setEditable(false);
            emailAddressField.setEditable(false);
            phoneNumberField.setEditable(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        durationBox.getItems().addAll("1 year", "2 years", "3 years", "4 years", "5 years");

        loadUserData();
    }

    @FXML
    public void backButton() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MyLoan.fxml"));

            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate - User");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double getRate(int years) {
        if (years <= 1) {
            return 0.20;
        } else if (years <= 3) {
            return 0.25;
        } else {
            return 0.30;
        }
    }

    @FXML
    private void handleCalculate() {

        if (loanAmountField.getText().trim().isEmpty() || durationBox.getValue() == null) {
            showPopup("CalculateRequiredFields.fxml", "Warning");
            return;
        }

        try {
            double amount = Double.parseDouble(loanAmountField.getText().trim());
            String duration = durationBox.getValue();
            int years = Integer.parseInt(duration.split(" ")[0]);

            double rate = getRate(years);

            double interest = amount * rate * years;
            double total = amount + interest;
            double monthly = total / (years * 12);

            totalInterestText.setText("₦" + String.format("%.2f", interest));
            totalRepaymentText.setText("₦" + String.format("%.2f", total));
            monthlyPaymentText.setText("₦" + String.format("%.2f", monthly));

            java.time.LocalDate repaymentDate = java.time.LocalDate.now().plusYears(years);
            repaymentDateText.setText(repaymentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")));

        } catch (NumberFormatException e) {
            showPopup("FPInvalidInput.fxml", "Invalid Input");
        }
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
    private void handleSubmitLoan() {
        if (firstNameField.getText().trim().isEmpty()
                || lastNameField.getText().trim().isEmpty()
                || phoneNumberField.getText().trim().isEmpty()
                || emailAddressField.getText().trim().isEmpty()
                || homeAddressField.getText().trim().isEmpty()
                || loanAmountField.getText().trim().isEmpty()
                || durationBox.getValue() == null) {

            showPopup("AllFieldsRequired.fxml", "Warning");
            return;
        }

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneNumberField.getText().trim();
        String email = emailAddressField.getText().trim();
        String address = homeAddressField.getText().trim();

        double amount;
        try {
            amount = Double.parseDouble(loanAmountField.getText().trim());
        } catch (NumberFormatException e) {
            showPopup("FPInvalidInput.fxml", "Invalid Input");
            return;
        }

        String duration = durationBox.getValue();

        if (!email.matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) {
            showPopup("InvalidEmail.fxml", "Warning");
            return;
        }

        if (!phone.matches("^(\\+234|234|0)[789][01]\\d{8}$")) {
            showPopup("InvalidPhone.fxml", "Warning");
            return;
        }

        if (phone.startsWith("0")) {
            phone = "+234" + phone.substring(1);
        } else if (phone.startsWith("234")) {
            phone = "+" + phone;
        }

        if (!phone.matches("^\\+234[789][01]\\d{8}$")) {
            showPopup("InvalidPhone.fxml", "Warning");
            return;
        }

        String durationText = durationBox.getValue();
        int years = Integer.parseInt(durationText.replaceAll("[^0-9]", ""));

        double rate;
        if (years <= 1) {
            rate = 0.20;
        } else if (years <= 3) {
            rate = 0.25;
        } else {
            rate = 0.30;
        }

        double total_interest = amount * rate * years;
        double total_repayment = amount + total_interest;
        double monthly_payment = total_repayment / (years * 12);

        java.time.LocalDate repaymentDate = java.time.LocalDate.now().plusYears(years);
        String repayment_date = repaymentDate.format(
                java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")
        );

        boolean saved = LoanService.saveLoan(
                email,
                address,
                amount,
                duration,
                total_interest,
                total_repayment,
                monthly_payment,
                repayment_date
        );

        if (!saved) {
            showPopup("DatabaseError.fxml", "Error");
            return;
        }

        clearForm();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("MyLoanSuccess.fxml"));

            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate - User");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        phoneNumberField.clear();
        emailAddressField.clear();
        homeAddressField.clear();
        loanAmountField.clear();
        durationBox.setValue(null);

        totalInterestText.setText("₦0");
        totalRepaymentText.setText("₦0");
        monthlyPaymentText.setText("₦0");
        repaymentDateText.setText("N/A");
    }
}
