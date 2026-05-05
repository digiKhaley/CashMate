/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package loanappproject;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.sql.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;

/**
 * FXML Controller class
 *
 * @author Kingsley Ezealisiobi
 */
public class AdminDashboardController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextField searchField;

    @FXML
    private TableView<Loan> tableView;

    @FXML
    private TableColumn<Loan, Integer> idCol;
    @FXML
    private TableColumn<Loan, String> nameCol;
    @FXML
    private TableColumn<Loan, Double> amountCol;
    @FXML
    private TableColumn<Loan, String> durationCol;
    @FXML
    private TableColumn<Loan, Double> interestCol;
    @FXML
    private TableColumn<Loan, Double> repaymentCol;
    @FXML
    private TableColumn<Loan, String> dateCol;
    @FXML
    private TableColumn<Loan, String> statusCol;
    @FXML
    private TableColumn<Loan, Void> actionCol;

    private ObservableList<Loan> loanList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        interestCol.setCellValueFactory(new PropertyValueFactory<>("interest"));
        repaymentCol.setCellValueFactory(new PropertyValueFactory<>("repayment"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        addDeleteButton();
        addStatusStyling();
        loadLoans();
    }

    @FXML
    public void backButton() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AdminAccess.fxml"));

            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate - Admin Access");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleViewAll() {
        loadLoans();
    }

    @FXML
    public void handleSearch() {
        String keyword = searchField.getText();

        ObservableList<Loan> list = FXCollections.observableArrayList();

        try {
            Connection conn = DBConnect.getConnection();

            String query = "SELECT loans.id, users.first_name, users.last_name, "
                    + "loans.loan_amount, loans.duration, loans.total_interest, "
                    + "loans.total_repayment, loans.date_requested, loans.status "
                    + "FROM loans JOIN users ON loans.email = users.email "
                    + "WHERE users.first_name LIKE ? OR users.last_name LIKE ? OR users.email LIKE ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id");
                String name = rs.getString("first_name") + " " + rs.getString("last_name");
                double amount = rs.getDouble("loan_amount");
                String duration = rs.getString("duration");
                double interest = rs.getDouble("total_interest");
                double repayment = rs.getDouble("total_repayment");
                String date = rs.getString("date_requested");
                String status = rs.getString("status");

                list.add(new Loan(id, name, amount, duration, interest, repayment, date, status));
            }

            tableView.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ObservableList<Loan> loadLoansFromDatabase() {
        ObservableList<Loan> list = FXCollections.observableArrayList();

        String query = "SELECT loans.id, users.first_name, users.last_name, "
                + "loans.loan_amount, loans.duration, loans.total_interest, "
                + "loans.total_repayment, loans.date_requested, loans.status "
                + "FROM loans JOIN users ON loans.email = users.email";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("first_name") + " " + rs.getString("last_name");
                double amount = rs.getDouble("loan_amount");
                String duration = rs.getString("duration");
                double interest = rs.getDouble("total_interest");
                double repayment = rs.getDouble("total_repayment");
                String date = rs.getString("date_requested");
                String status = rs.getString("status");

                list.add(new Loan(id, name, amount, duration, interest, repayment, date, status));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private void loadLoans() {
        loanList = loadLoansFromDatabase();
        tableView.setItems(loanList);
    }

    private void addStatusStyling() {
        statusCol.setCellFactory(col -> new TableCell<>() {

            private final Label statusLabel = new Label();

            {
                statusLabel.setStyle(
                        "-fx-padding: 5 12;"
                        + "-fx-background-radius: 15;"
                        + "-fx-border-radius: 15;"
                        + "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
                );
            }

            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                    return;
                }

                statusLabel.setText(status);

                Loan loan = getTableView().getItems().get(getIndex());

                String baseStyle = "-fx-padding: 5 12; -fx-background-radius: 15; -fx-border-radius: 15; -fx-font-size: 12px; -fx-font-weight: bold;";

                switch (status.toLowerCase()) {

                    case "approved":
                        statusLabel.setStyle(baseStyle + "-fx-background-color: #d4edda; -fx-text-fill: green;");
                        statusLabel.setOnMouseClicked(null);
                        break;

                    case "rejected":
                        statusLabel.setStyle(baseStyle + "-fx-background-color: #f8d7da; -fx-text-fill: red;");
                        statusLabel.setOnMouseClicked(null);
                        break;

                    case "pending":
                        statusLabel.setStyle(baseStyle
                                + "-fx-background-color: #fff3cd;"
                                + "-fx-border-color: orange;"
                                + "-fx-text-fill: orange;"
                                + "-fx-cursor: hand;"
                        );

                        statusLabel.setOnMouseClicked(e -> showDecisionPopup(loan));
                        break;
                }

                setGraphic(statusLabel);
            }
        });
    }

    private void refreshTable() {
        loanList.clear();
        loanList.addAll(loadLoansFromDatabase());
    }

    private void showDecisionPopup(Loan loan) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DecisionPopup.fxml"));
            Parent root = loader.load();

            DecisionPopupController controller = loader.getController();

            controller.setLoanData(loan);

            controller.setOnUpdateCallback(() -> {
                refreshTable();
            });

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Review Loan Request");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            controller.setOnUpdateCallback(() -> {
                refreshTable();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLoanStatus(int id, String status) {
        String query = "UPDATE loans SET status=? WHERE id=?";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addDeleteButton() {
        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button deleteBtn = new Button();

            {
                ImageView icon = new ImageView(
                        new Image(getClass().getResourceAsStream("IMAGES/delete.png"))
                );
                icon.setFitWidth(16);
                icon.setFitHeight(16);

                deleteBtn.setGraphic(icon);

                deleteBtn.setStyle(
                        "-fx-background-color: transparent;"
                        + "-fx-cursor: hand;"
                );

                deleteBtn.setOnMouseEntered(e
                        -> deleteBtn.setStyle("-fx-background-color: #ffe5e5; -fx-cursor: hand;")
                );

                deleteBtn.setOnMouseExited(e
                        -> deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;")
                );

                deleteBtn.setOnAction(e -> {
                    Loan loan = getTableView().getItems().get(getIndex());
                    deleteLoan(loan.getId());
                    loadLoans();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }

    private void deleteLoan(int id) {
        String query = "DELETE FROM loans WHERE id=?";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
