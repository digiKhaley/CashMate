package loanappproject;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WelcomeController implements Initializable {

    @FXML
    private javafx.scene.layout.AnchorPane rootPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        PauseTransition delay = new PauseTransition(Duration.seconds(5));

        delay.setOnFinished(event -> {

            FadeTransition fade = new FadeTransition(Duration.seconds(1), rootPane);
            fade.setFromValue(1);
            fade.setToValue(0);

            fade.setOnFinished(e -> {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));

                    Stage stage = (Stage) rootPane.getScene().getWindow();
                    stage.setTitle("CashMate - Login");
                    stage.setScene(new Scene(root));

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            fade.play();
        });

        delay.play();
    }
}
