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

/**
 * Entry point for the standalone Admin application. This is completely
 * separate from LoanAppProject.java (the user-facing app) - the regular app
 * no longer has any Admin button or route to this at all.
 *
 * To run: in NetBeans, right-click this file -> "Run File" (not the main
 * project Run button, which still launches the user-facing LoanAppProject).
 *
 * @author Kingsley Ezealisiobi
 */
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
