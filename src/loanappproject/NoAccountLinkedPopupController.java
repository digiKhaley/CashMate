package loanappproject;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class NoAccountLinkedPopupController {

    @FXML
    private void handleClose(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
