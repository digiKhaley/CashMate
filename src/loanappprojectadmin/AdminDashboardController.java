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
//import loanappproject.DecisionPopupController;

/**
 * FXML Controller class - now shows Users (default) and Loans in one window,
 * toggled by the two nav buttons.
 *
 * @author Kingsley Ezealisiobi
 */
public class AdminDashboardController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private Button usersTabButton;
    @FXML
    private Button loansTabButton;

    // Users tab
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
//    @FXML
//    private TableColumn<UserRecord, Integer> userIdCol;

    // Loans tab
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
        showUsersTab(); // Users tab shown by default
    }

    @FXML
    public void backButton() {
//        Stage stage = (Stage) searchField.getScene().getWindow();
//        stage.close();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("AdminAccess.fxml"));

            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(new Scene(root));
//            ResponsiveHelper.setResponsiveScene(stage, root);
            stage.setTitle("CashMate Admin");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- Tab switching ----------
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

    // ================= USERS TAB =================
    private void setupUsersTable() {
//        userIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        userEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        userPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        userKycStatusCol.setCellValueFactory(new PropertyValueFactory<>("kycStatus"));

        addKycStatusStyling();
        addKycActionButton();
        addUserDeleteButton();
    }

    /**
     * users LEFT JOIN kyc_records on email - both tables live in loan_app.
     */
    private ObservableList<UserRecord> loadUsersFromDatabase() {
        ObservableList<UserRecord> list = FXCollections.observableArrayList();

        String query = "SELECT u.first_name, u.last_name, u.email, u.phone_number, "
                + "COALESCE(k.status, 'Unverified') AS kyc_status "
                + "FROM users u "
                + "LEFT JOIN kyc_records k ON u.email = k.email "
                + "WHERE u.role = 'user'";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
//                int id = rs.getInt("id");
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
//                int id = rs.getInt("id");
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

    /**
     * Colored badge for KYC status - same visual pattern as the Loans
     * Pending/Approved/Rejected badges.
     */
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

    /**
     * Action button: greyed/disabled while Unverified (user hasn't submitted
     * anything yet). Becomes active (clickable, normal color) once the user has
     * requested verification (status = Pending), opening the verify/reject
     * popup. Verified/Rejected are final states - shown disabled since there's
     * nothing left to action.
     */
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
                    default: // Unverified - nothing submitted yet, nothing to review
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

    /**
     * Delete button for the Users tab - same look/behavior as the Loans tab's
     * delete button. Deletes the user (and their kyc_records row, if any) from
     * the database.
     */
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

            // Remove their kyc_records row first (if any), then the user.
            try (PreparedStatement kycPs = conn.prepareStatement("DELETE FROM kyc_records WHERE email=?")) {
                kycPs.setString(1, email);
                kycPs.executeUpdate();
            }

            try (PreparedStatement userPs = conn.prepareStatement("DELETE FROM users WHERE email=?")) {
                userPs.setString(1, email);
                userPs.executeUpdate();
            }

        } catch (SQLException e) {
            // Most likely cause: this user still has loan(s) in the loans
            // table, and loans.email has a foreign key pointing at users.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot Delete User");
            alert.setHeaderText(null);
            alert.setContentText("This user couldn't be deleted - they likely still have loan record(s) on file. "
                    + "Delete their loan(s) from the Loans tab first, then try again.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    // ================= LOANS TAB (same behavior as before) =================
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

    /**
     * Renders a Double column as a plain, comma-separated number (e.g.
     * "90,100,000.00") instead of Java's default Double.toString(), which
     * switches to scientific notation ("9.01E7") for large values.
     */
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

            loansTableView.setItems(list);

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
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("../loanappproject/DecisionPopup.fxml"));
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
