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
public class LoanService {

    public static boolean saveLoan(String email, String address, double amount,
            String duration, double total_interest, double total_repayment,
            double monthly_payment, String repayment_date) {

        String sql = "INSERT INTO loans (email, home_address, loan_amount, duration, "
                + "total_interest, total_repayment, monthly_payment, repayment_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, address);
            ps.setDouble(3, amount);
            ps.setString(4, duration);
            ps.setDouble(5, total_interest);
            ps.setDouble(6, total_repayment);
            ps.setDouble(7, monthly_payment);
            ps.setString(8, repayment_date);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean hasUserTakenLoan(String email) {
    String query = "SELECT id FROM loans WHERE email = ? LIMIT 1";

    try (Connection conn = DBConnect.getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {

        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();

        return rs.next(); // TRUE if loan exists

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
}
