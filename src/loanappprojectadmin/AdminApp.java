/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML.java to edit this template
 */
package loanappprojectadmin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdminApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("AdminAccess.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("CashMate Admin");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
