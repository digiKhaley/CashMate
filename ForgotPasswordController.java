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
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Kingsley Ezealisiobi
 */
public class ForgotPasswordController implements Initializable {

    @FXML
    private TextField adminName;
    @FXML
    private TextField teacherName;
    @FXML
    private TextField teacherOtherName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    public void backButton() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AdminAccess.fxml"));
            Stage stage = (Stage) adminName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate - Admin Access");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        adminName.clear();
        teacherName.clear();
        teacherOtherName.clear();
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
    public void submitButton() {
        String admin = adminName.getText().trim();
        String teacher = teacherName.getText().trim();
        String teacherO = teacherOtherName.getText().trim();

        if (admin.isEmpty() || teacher.isEmpty() || teacherO.isEmpty()) {

            showPopup("AllFieldsRequired.fxml", "Warning");
            return;
        }

        if ((admin.equalsIgnoreCase("kingsley")
                || admin.equalsIgnoreCase("esther")
                || admin.equalsIgnoreCase("busayo")
                || admin.equalsIgnoreCase("yemi"))
                && teacher.equalsIgnoreCase("dare")
                && teacherO.equalsIgnoreCase("kola")) {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ResetPassword.fxml"));
                Parent root = loader.load();

                ResetPasswordController controller = loader.getController();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Reset Password");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                if (controller.isResetSuccessful()) {

                    FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("AdminAccess.fxml"));
                    Parent adminRoot = adminLoader.load();

                    AdminAccessController adminController = adminLoader.getController();
                    adminController.setPasswordChanged(true);

                    Stage currentStage = (Stage) adminName.getScene().getWindow();
                    currentStage.setScene(new Scene(adminRoot));
                    currentStage.setTitle("CashMate - Admin Access");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            showPopup("FPInvalidInput.fxml", "Invalid Input");
        }

        clearFields();
    }

    @FXML
    private void handleForgotPassword() {
        showPopup("ForgotPassword.fxml", "Forgot Password");
    }

}
