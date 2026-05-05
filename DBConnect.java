/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loanappproject;

import java.sql.*;

/**
 *
 * @author Kingsley Ezealisiobi
 */
public class DBConnect {
    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/loan_app";
            String user = "root";
            String password = "";

            Connection conn = DriverManager.getConnection(url, user, password);
            return conn;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
