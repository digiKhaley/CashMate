/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package loanappproject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Kingsley Ezealisiobi
 */
public class MyLoanSuccessController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Label statusBlock;

    @FXML
    private VBox statusCont;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadUserLoan();
    }

    @FXML
    public void backButton() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));

            Stage stage = (Stage) statusBlock.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUserLoan() {

        String query = "SELECT * FROM loans WHERE email = ? ORDER BY id DESC LIMIT 1";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, Session.currentUserEmail);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String status = rs.getString("status");

                statusBlock.setText("Status: " + status);

                switch (status.toLowerCase()) {

                    case "approved":
                        statusBlock.setStyle("-fx-text-fill: green;");
                        statusCont.setStyle(
                                "-fx-background-color: #d4edda;"
                                + "-fx-border-color: green;"
                                + "-fx-border-radius: 13;"
                                + "-fx-background-radius: 10;"
                                + "-fx-padding: 6;"
                        );
                        break;

                    case "rejected":
                        statusBlock.setStyle("-fx-text-fill: red;");
                        statusCont.setStyle(
                                "-fx-background-color: #f8d7da;"
                                + "-fx-border-color: red;"
                                + "-fx-border-radius: 13;"
                                + "-fx-background-radius: 10;"
                                + "-fx-padding: 6;"
                        );
                        break;

                    case "pending":
                        statusBlock.setStyle("-fx-text-fill: orange;");
                        statusCont.setStyle(
                                "-fx-background-color: #fff3cd;"
                                + "-fx-border-color: orange;"
                                + "-fx-border-radius: 13;"
                                + "-fx-background-radius: 10;"
                                + "-fx-padding: 6;"
                        );
                        break;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewLoan(ActionEvent event) {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoanDetails.fxml"));
        Parent root = loader.load();

        Stage modal = new Stage();
        modal.setScene(new Scene(root));
        modal.setTitle("CashMate - Loan Details");

        modal.initOwner(statusBlock.getScene().getWindow());
        modal.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        modal.showAndWait();

    } catch (Exception e) {
        e.printStackTrace();
    }
    }

}
