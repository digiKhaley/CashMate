/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package loanappprojectadmin;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import loanappproject.KYCService;
import emailverification.EmailService;

/**
 * FXML Controller class
 *
 * @author Kingsley Ezealisiobi
 */
public class KYCDecisionPopupController implements Initializable {

    @FXML
    private Text applicantText;
    @FXML
    private Text idTypeText;
    @FXML
    private Text loanPurposeText;
    @FXML
    private Text monthlyIncomeText;
    @FXML
    private ImageView idImageView;
    @FXML
    private Label noImageLabel;

    private String email;
    private String idImagePath;
    private Runnable onUpdateCallback;
    private final EmailService emailService = new EmailService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setKycData(String name, String email) {
        this.email = email;
        applicantText.setText(name);

        KYCService.KYCDetails details = KYCService.getDetails(email);

        idTypeText.setText(details.idType != null ? details.idType : "N/A");
        loanPurposeText.setText(details.loanPurpose != null ? details.loanPurpose : "N/A");
        monthlyIncomeText.setText(details.monthlyIncomeRange != null ? details.monthlyIncomeRange : "N/A");
        this.idImagePath = details.idImagePath;

        loadThumbnail();
    }

    private void loadThumbnail() {
        if (idImagePath == null || idImagePath.isEmpty()) {
            idImageView.setVisible(false);
            noImageLabel.setVisible(true);
            return;
        }

        File file = new File(idImagePath);
        if (!file.exists()) {
            idImageView.setVisible(false);
            noImageLabel.setText("Image file not found on disk");
            noImageLabel.setVisible(true);
            return;
        }

        Image image = new Image(file.toURI().toString());
        idImageView.setImage(image);
        idImageView.setVisible(true);
        noImageLabel.setVisible(false);
    }

    @FXML
    private void handleImageClick() {
        if (idImageView.getImage() == null) {
            return;
        }

        ImageView fullView = new ImageView(idImageView.getImage());
        fullView.setPreserveRatio(true);
        fullView.setFitWidth(700);

        StackPane root = new StackPane(fullView);
        root.setStyle("-fx-background-color: black;");

        Stage previewStage = new Stage();
        previewStage.setScene(new Scene(root));
        previewStage.setTitle("ID Image - " + applicantText.getText());
        previewStage.show();
    }

    public void setOnUpdateCallback(Runnable callback) {
        this.onUpdateCallback = callback;
    }

    @FXML
    private void handleVerify() {
        KYCService.updateStatus(email, "Verified");
        emailService.sendKycApproved(email, applicantText.getText());
        closeAndRefresh();
    }

    @FXML
    private void handleReject() {
        KYCService.updateStatus(email, "Rejected");
        emailService.sendKycRejected(email, applicantText.getText(),
                "One or more submitted documents could not be verified. Please resubmit clear, valid ID documents.");
        closeAndRefresh();
    }

    private void closeAndRefresh() {
        if (onUpdateCallback != null) {
            onUpdateCallback.run();
        }
        Stage stage = (Stage) applicantText.getScene().getWindow();
        stage.close();
    }
}
