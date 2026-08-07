/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package loanappproject;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TermsRequiredController implements Initializable {

    private Runnable onPolicyViewedCallback;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setOnPolicyViewedCallback(Runnable callback) {
        this.onPolicyViewedCallback = callback;
    }

    @FXML
    private void handleOpenPrivacyPolicy(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("PrivacyPolicy.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Privacy Policy");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (onPolicyViewedCallback != null) {
                onPolicyViewedCallback.run();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
