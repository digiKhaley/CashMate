package loanappproject;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * FXML Controller class for the "Account Not Linked" popup shown when a
 * user clicks Withdraw but has no payout account connected yet.
 */
public class NoAccountLinkedPopupController {

    @FXML
    private void handleClose(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
