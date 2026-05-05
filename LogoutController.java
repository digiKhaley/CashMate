/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package loanappproject;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kingsley Ezealisiobi
 */
public class LogoutController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Label name;
    @FXML
    private Label email;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setUserDetails(String userName, String userEmail) {
        name.setText(userName);
        email.setText(userEmail);
    }

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    @FXML
    public void logoutButton() {
        try {
            Session.currentUserEmail = null;
        Session.currentFirstName = null;
        Session.role = null;

            Stage stage = (Stage) name.getScene().getWindow();
            stage.close();

            if (dashboardController != null) {
                dashboardController.logoutToLogin();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
