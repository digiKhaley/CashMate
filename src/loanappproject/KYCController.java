/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package loanappproject;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kingsley Ezealisiobi
 */
public class KYCController implements Initializable {

    @FXML
    private ComboBox<String> idTypeBox;
    @FXML
    private Label idFileNameLabel;
    @FXML
    private Label statusMessageLabel;
    @FXML
    private AnchorPane formSection;
    @FXML
    private VBox statusSection;
    @FXML
    private Label statusTitleLabel;
    @FXML
    private Label statusBodyLabel;

    @FXML
    private RadioButton personalPurposeRadio;
    @FXML
    private RadioButton businessPurposeRadio;

    @FXML
    private RadioButton income1Radio;
    @FXML
    private RadioButton income2Radio;
    @FXML
    private RadioButton income3Radio;
    @FXML
    private RadioButton income4Radio;
    private final ToggleGroup purposeGroup = new ToggleGroup();
    private final ToggleGroup incomeGroup = new ToggleGroup();

    private File selectedIdImage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idTypeBox.getItems().addAll("NIN", "Driver's License", "International Passport", "Permanent Voter's Card (PVC)", "Utility Bill");

        personalPurposeRadio.setToggleGroup(purposeGroup);
        businessPurposeRadio.setToggleGroup(purposeGroup);

        income1Radio.setToggleGroup(incomeGroup);
        income2Radio.setToggleGroup(incomeGroup);
        income3Radio.setToggleGroup(incomeGroup);
        income4Radio.setToggleGroup(incomeGroup);

        String currentStatus = KYCService.getStatus(Session.currentUserEmail);

        switch (currentStatus.toLowerCase()) {
            case "pending":
                showStatusOnly("KYC Pending", "Your KYC verification is pending review. We'll let you know once it's been reviewed.");
                break;
            case "verified":
                showStatusOnly("KYC Verified", "Your identity has been verified. You're all set to request a loan.");
                break;
            case "rejected":
                showForm();
                statusMessageLabel.setText("Your previous submission was rejected. Please resubmit your details and ID.");
                break;
            default:
                showForm();
                break;
        }
    }

    /**
     * Hides the submission form and shows the status message instead - same
     * idea as MyLoan.fxml swapping to MyLoanSuccess.fxml once a loan has
     * already been requested.
     */
    private void showStatusOnly(String title, String body) {
        formSection.setVisible(false);
        formSection.setManaged(false);

        statusTitleLabel.setText(title);
        statusBodyLabel.setText(body);
        statusSection.setVisible(true);
        statusSection.setManaged(true);
    }

    private void showForm() {
        statusSection.setVisible(false);
        statusSection.setManaged(false);

        formSection.setVisible(true);
        formSection.setManaged(true);
    }

    @FXML
    private void handleUploadIdImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select ID Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(idFileNameLabel.getScene().getWindow());
        if (file != null) {
            selectedIdImage = file;
            idFileNameLabel.setText(file.getName());
        }
    }

    @FXML
    private void handleSubmitKyc() {

        RadioButton selectedPurpose = (RadioButton) purposeGroup.getSelectedToggle();
        RadioButton selectedIncome = (RadioButton) incomeGroup.getSelectedToggle();
        String idType = idTypeBox.getValue();

        if (selectedPurpose == null) {
            statusMessageLabel.setText("Please select a loan purpose.");
            return;
        }
        if (selectedIncome == null) {
            statusMessageLabel.setText("Please select your monthly income range.");
            return;
        }
        if (idType == null) {
            statusMessageLabel.setText("Please select an ID type.");
            return;
        }
        if (selectedIdImage == null) {
            statusMessageLabel.setText("Please upload an image of your ID.");
            return;
        }

        String loanPurpose = selectedPurpose.getText();
        String monthlyIncome = selectedIncome.getText();

        boolean success = KYCService.submitKYC(
                Session.currentUserEmail, idType, selectedIdImage.getAbsolutePath(), loanPurpose, monthlyIncome);

        if (success) {
            showStatusOnly("KYC Pending", "Your KYC verification is pending review. We'll let you know once it's been reviewed.");
        } else {
            statusMessageLabel.setText("Something went wrong - please try again.");
        }
    }

    @FXML
    private void backButton() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));

            Stage stage = (Stage) idTypeBox.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CashMate");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
