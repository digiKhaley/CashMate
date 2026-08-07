/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package loanappprojectadmin;

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
import loanappproject.DBConnect;
import loanappproject.Loan;

public class AdminDashboardController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private Button usersTabButton;
    @FXML
    private Button loansTabButton;

    @FXML
    private TableView<UserRecord> usersTableView;
    @FXML
    private TableColumn<UserRecord, String> userNameCol;
    @FXML
    private TableColumn<UserRecord, String> userEmailCol;
    @FXML
    private TableColumn<UserRecord, String> userPhoneCol;
    @FXML
    private TableColumn<UserRecord, String> userKycStatusCol;
    @FXML
    private TableColumn<UserRecord, Void> userActionCol;
    @FXML
    private TableColumn<UserRecord, Void> userDeleteCol;
    @FXML
    private TableView<Loan> loansTableView;
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

    private ObservableList<UserRecord> userList = FXCollections.observableArrayList();
    private ObservableList<Loan> loanList = FXCollections.observableArrayList();

    private boolean onUsersTab = true;

    private static final String ACTIVE_STYLE = "-fx-background-color: #008000; -fx-background-radius: 8;";
    private static final String INACTIVE_STYLE = "-fx-background-color: #dddddd; -fx-background-radius: 8;";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupUsersTable();
        setupLoansTable();

        loadUsers();
        showUsersTab();
    }

    @FXML
    public void backButton() {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("AdminAccess.fxml"));

            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate Admin");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showUsersTab() {
        onUsersTab = true;
        usersTableView.setVisible(true);
        usersTableView.setManaged(true);
        loansTableView.setVisible(false);
        loansTableView.setManaged(false);

        usersTabButton.setStyle(ACTIVE_STYLE);
        usersTabButton.setTextFill(javafx.scene.paint.Color.WHITE);
        loansTabButton.setStyle(INACTIVE_STYLE);
        loansTabButton.setTextFill(javafx.scene.paint.Color.web("#333333"));

        searchField.setPromptText("Search by name...");
    }

    @FXML
    private void showLoansTab() {
        onUsersTab = false;
        loansTableView.setVisible(true);
        loansTableView.setManaged(true);
        usersTableView.setVisible(false);
        usersTableView.setManaged(false);

        loansTabButton.setStyle(ACTIVE_STYLE);
        loansTabButton.setTextFill(javafx.scene.paint.Color.WHITE);
        usersTabButton.setStyle(INACTIVE_STYLE);
        usersTabButton.setTextFill(javafx.scene.paint.Color.web("#333333"));

        loadLoans();
        searchField.setPromptText("Search by user name...");
    }

    @FXML
    public void handleViewAll() {
        if (onUsersTab) {
            loadUsers();
        } else {
            loadLoans();
        }
    }

    @FXML
    public void handleSearch() {
        String keyword = searchField.getText();
        if (onUsersTab) {
            searchUsers(keyword);
        } else {
            searchLoans(keyword);
        }
    }

    private void setupUsersTable() {
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        userEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        userPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        userKycStatusCol.setCellValueFactory(new PropertyValueFactory<>("kycStatus"));

        addKycStatusStyling();
        addKycActionButton();
        addUserDeleteButton();
    }

    private ObservableList<UserRecord> loadUsersFromDatabase() {
        ObservableList<UserRecord> list = FXCollections.observableArrayList();

        String query = "SELECT u.first_name, u.last_name, u.email, u.phone_number, "
                + "COALESCE(k.status, 'Unverified') AS kyc_status "
                + "FROM users u "
                + "LEFT JOIN kyc_records k ON u.email = k.email "
                + "WHERE u.role = 'user'";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("first_name") + " " + rs.getString("last_name");
                String email = rs.getString("email");
                String phone = rs.getString("phone_number");
                String kycStatus = rs.getString("kyc_status");

                list.add(new UserRecord(email, name, phone, kycStatus));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private void loadUsers() {
        userList = loadUsersFromDatabase();
        usersTableView.setItems(userList);
    }

    private void refreshUsers() {
        userList.clear();
        userList.addAll(loadUsersFromDatabase());
    }

    private void searchUsers(String keyword) {
        ObservableList<UserRecord> list = FXCollections.observableArrayList();

        String query = "SELECT u.first_name, u.last_name, u.email, u.phone_number, "
                + "COALESCE(k.status, 'Unverified') AS kyc_status "
                + "FROM users u "
                + "LEFT JOIN kyc_records k ON u.email = k.email "
                + "WHERE u.role = 'user' AND (u.first_name LIKE ? OR u.last_name LIKE ? OR u.email LIKE ?)";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("first_name") + " " + rs.getString("last_name");
                String email = rs.getString("email");
                String phone = rs.getString("phone_number");
                String kycStatus = rs.getString("kyc_status");

                list.add(new UserRecord(email, name, phone, kycStatus));
            }

            usersTableView.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addKycStatusStyling() {
        userKycStatusCol.setCellFactory(col -> new TableCell<>() {

            private final Label statusLabel = new Label();

            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                    return;
                }

                statusLabel.setText(status);
                String baseStyle = "-fx-padding: 5 12; -fx-background-radius: 15; -fx-border-radius: 15; -fx-font-size: 12px; -fx-font-weight: bold;";

                switch (status.toLowerCase()) {
                    case "verified":
                        statusLabel.setStyle(baseStyle + "-fx-background-color: #d4edda; -fx-text-fill: green;");
                        break;
                    case "rejected":
                        statusLabel.setStyle(baseStyle + "-fx-background-color: #f8d7da; -fx-text-fill: red;");
                        break;
                    case "pending":
                        statusLabel.setStyle(baseStyle + "-fx-background-color: #fff3cd; -fx-text-fill: orange;");
                        break;
                    default: // Unverified
                        statusLabel.setStyle(baseStyle + "-fx-background-color: #e2e3e5; -fx-text-fill: #6c757d;");
                        break;
                }

                setGraphic(statusLabel);
            }
        });
    }

    private void addKycActionButton() {
        userActionCol.setCellFactory(col -> new TableCell<>() {

            private final Button actionBtn = new Button("Review");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                UserRecord user = getTableView().getItems().get(getIndex());
                String status = user.getKycStatus();

                actionBtn.setOnAction(null);

                switch (status.toLowerCase()) {
                    case "pending":
                        actionBtn.setDisable(false);
                        actionBtn.setStyle("-fx-background-color: #008000; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
                        actionBtn.setOnAction(e -> showKycDecisionPopup(user));
                        break;
                    case "verified":
                        actionBtn.setDisable(true);
                        actionBtn.setStyle("-fx-background-color: #d4edda; -fx-text-fill: green; -fx-background-radius: 8;");
                        break;
                    case "rejected":
                        actionBtn.setDisable(true);
                        actionBtn.setStyle("-fx-background-color: #f8d7da; -fx-text-fill: red; -fx-background-radius: 8;");
                        break;
                    default:
                        actionBtn.setDisable(true);
                        actionBtn.setStyle("-fx-background-color: #cfe2ff; -fx-text-fill: #4a6fa5; -fx-background-radius: 8;");
                        break;
                }

                setGraphic(actionBtn);
            }
        });
    }

    private void showKycDecisionPopup(UserRecord user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("KYCDecisionPopup.fxml"));
            Parent root = loader.load();

            KYCDecisionPopupController controller = loader.getController();
            controller.setKycData(user.getName(), user.getEmail());
            controller.setOnUpdateCallback(this::refreshUsers);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Review KYC Submission");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addUserDeleteButton() {
        userDeleteCol.setCellFactory(col -> new TableCell<>() {

            private final Button deleteBtn = new Button();

            {
                ImageView icon = new ImageView(
                        new Image(getClass().getResourceAsStream("../loanappproject/IMAGES/delete.png"))
                );
                icon.setFitWidth(16);
                icon.setFitHeight(16);

                deleteBtn.setGraphic(icon);
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: #ffe5e5; -fx-cursor: hand;"));
                deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;"));

                deleteBtn.setOnAction(e -> {
                    UserRecord user = getTableView().getItems().get(getIndex());
                    deleteUser(user.getEmail());
                    refreshUsers();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                UserRecord user = getTableView().getItems().get(getIndex());
                boolean canDelete = "rejected".equalsIgnoreCase(user.getKycStatus());

                deleteBtn.setDisable(!canDelete);
                deleteBtn.setOpacity(canDelete ? 1.0 : 0.35);

                setGraphic(deleteBtn);
            }
        });
    }

    private void deleteUser(String email) {
        try (Connection conn = DBConnect.getConnection()) {

            try (PreparedStatement kycPs = conn.prepareStatement("DELETE FROM kyc_records WHERE email=?")) {
                kycPs.setString(1, email);
                kycPs.executeUpdate();
            }

            try (PreparedStatement userPs = conn.prepareStatement("DELETE FROM users WHERE email=?")) {
                userPs.setString(1, email);
                userPs.executeUpdate();
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot Delete User");
            alert.setHeaderText(null);
            alert.setContentText("This user couldn't be deleted - they likely still have loan record(s) on file. "
                    + "Delete their loan(s) from the Loans tab first, then try again.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void setupLoansTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        interestCol.setCellValueFactory(new PropertyValueFactory<>("interest"));
        repaymentCol.setCellValueFactory(new PropertyValueFactory<>("repayment"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        addMoneyFormatting(amountCol);
        addMoneyFormatting(interestCol);
        addMoneyFormatting(repaymentCol);

        addLoanDeleteButton();
        addLoanStatusStyling();
    }

    private void addMoneyFormatting(TableColumn<Loan, Double> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.2f", value));
                }
            }
        });
    }

    private void searchLoans(String keyword) {
        ObservableList<Loan> list = FXCollections.observableArrayList();

        try {
            Connection conn = DBConnect.getConnection();

            String query = "SELECT loans.id, users.first_name, users.last_name, loans.email, "
                    + "loans.loan_amount, loans.duration, loans.total_interest, "
                    + "loans.total_repayment, loans.date_requested, loans.repayment_date, loans.status "
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
                String email = rs.getString("email");
                double amount = rs.getDouble("loan_amount");
                String duration = rs.getString("duration");
                double interest = rs.getDouble("total_interest");
                double repayment = rs.getDouble("total_repayment");
                String date = rs.getString("date_requested");
                String repaymentDate = rs.getString("repayment_date");
                String status = rs.getString("status");

                list.add(new Loan(id, name, email, amount, duration, interest, repayment, date, repaymentDate, status));
            }

            loansTableView.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ObservableList<Loan> loadLoansFromDatabase() {
        ObservableList<Loan> list = FXCollections.observableArrayList();

        String query = "SELECT loans.id, users.first_name, users.last_name, loans.email, "
                + "loans.loan_amount, loans.duration, loans.total_interest, "
                + "loans.total_repayment, loans.date_requested, loans.repayment_date, loans.status "
                + "FROM loans JOIN users ON loans.email = users.email";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("first_name") + " " + rs.getString("last_name");
                String email = rs.getString("email");
                double amount = rs.getDouble("loan_amount");
                String duration = rs.getString("duration");
                double interest = rs.getDouble("total_interest");
                double repayment = rs.getDouble("total_repayment");
                String date = rs.getString("date_requested");
                String repaymentDate = rs.getString("repayment_date");
                String status = rs.getString("status");

                list.add(new Loan(id, name, email, amount, duration, interest, repayment, date, repaymentDate, status));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private void loadLoans() {
        loanList = loadLoansFromDatabase();
        loansTableView.setItems(loanList);
    }

    private void refreshLoans() {
        loanList.clear();
        loanList.addAll(loadLoansFromDatabase());
    }

    private void addLoanStatusStyling() {
        statusCol.setCellFactory(col -> new TableCell<>() {

            private final Label statusLabel = new Label();

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
                        statusLabel.setStyle(baseStyle + "-fx-background-color: #fff3cd; -fx-border-color: orange; -fx-text-fill: orange; -fx-cursor: hand;");
                        statusLabel.setOnMouseClicked(e -> showLoanDecisionPopup(loan));
                        break;
                }

                setGraphic(statusLabel);
            }
        });
    }

    private void showLoanDecisionPopup(Loan loan) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DecisionPopup.fxml"));
            Parent root = loader.load();

            DecisionPopupController controller = loader.getController();
            controller.setLoanData(loan);
            controller.setOnUpdateCallback(this::refreshLoans);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Review Loan Request");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Couldn't Open Review Popup");
            alert.setHeaderText(null);
            alert.setContentText("Something went wrong opening the loan review popup:\n" + e);
            alert.showAndWait();
        }
    }

    private void addLoanDeleteButton() {
        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button deleteBtn = new Button();

            {
                ImageView icon = new ImageView(
                        new Image(getClass().getResourceAsStream("../loanappproject/IMAGES/delete.png"))
                );
                icon.setFitWidth(16);
                icon.setFitHeight(16);

                deleteBtn.setGraphic(icon);
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: #ffe5e5; -fx-cursor: hand;"));
                deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;"));

                deleteBtn.setOnAction(e -> {
                    Loan loan = getTableView().getItems().get(getIndex());
                    deleteLoan(loan.getId());
                    loadLoans();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Loan loan = getTableView().getItems().get(getIndex());
                boolean canDelete = "rejected".equalsIgnoreCase(loan.getStatus());

                deleteBtn.setDisable(!canDelete);
                deleteBtn.setOpacity(canDelete ? 1.0 : 0.35);

                setGraphic(deleteBtn);
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
