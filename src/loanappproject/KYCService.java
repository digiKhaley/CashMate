/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loanappproject;

import java.sql.*;

/**
 * Handles all reads/writes against the kyc_records table, which lives in the
 * SAME loan_app database as users and loans (not a separate database) - it's
 * linked to users purely by matching the email column.
 *
 * @author Kingsley Ezealisiobi
 */
public class KYCService {

    /**
     * Returns the KYC status for a user, e.g. "Unverified", "Pending",
     * "Verified", "Rejected". If the user has no KYC record at all yet,
     * returns "Unverified" (the default, per spec).
     */
    public static String getStatus(String email) {
        String sql = "SELECT status FROM kyc_records WHERE email = ?";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("status");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Unverified";
    }

    public static boolean isVerified(String email) {
        return "Verified".equalsIgnoreCase(getStatus(email));
    }

    /**
     * Simple holder for the fields the admin needs to review a submission:
     * ID type, image path, loan purpose, and monthly income range.
     */
    public static class KYCDetails {
        public final String idType;
        public final String idImagePath;
        public final String loanPurpose;
        public final String monthlyIncomeRange;

        public KYCDetails(String idType, String idImagePath, String loanPurpose, String monthlyIncomeRange) {
            this.idType = idType;
            this.idImagePath = idImagePath;
            this.loanPurpose = loanPurpose;
            this.monthlyIncomeRange = monthlyIncomeRange;
        }
    }

    /**
     * Pulls the actual submitted details, so the admin review popup shows
     * real data instead of a placeholder.
     */
    public static KYCDetails getDetails(String email) {
        String sql = "SELECT id_type, id_image_path, loan_purpose, monthly_income_range FROM kyc_records WHERE email = ?";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new KYCDetails(
                        rs.getString("id_type"),
                        rs.getString("id_image_path"),
                        rs.getString("loan_purpose"),
                        rs.getString("monthly_income_range"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new KYCDetails(null, null, null, null);
    }

    /**
     * Inserts a new KYC submission, or updates the existing one if the user
     * already has a row (e.g. resubmitting after a Rejected status).
     * Sets status to "Pending" so the admin can review it.
     */
    public static boolean submitKYC(String email, String idType, String idImagePath, String loanPurpose, String monthlyIncomeRange) {
        String checkSql = "SELECT id FROM kyc_records WHERE email = ?";
        String today = java.time.LocalDate.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));

        try (Connection conn = DBConnect.getConnection()) {

            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setString(1, email);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                // Existing record - update it (e.g. resubmission)
                String updateSql = "UPDATE kyc_records SET id_type = ?, id_image_path = ?, "
                        + "loan_purpose = ?, monthly_income_range = ?, "
                        + "status = 'Pending', date_submitted = ? WHERE email = ?";
                PreparedStatement ps = conn.prepareStatement(updateSql);
                ps.setString(1, idType);
                ps.setString(2, idImagePath);
                ps.setString(3, loanPurpose);
                ps.setString(4, monthlyIncomeRange);
                ps.setString(5, today);
                ps.setString(6, email);
                ps.executeUpdate();
            } else {
                // New record
                String insertSql = "INSERT INTO kyc_records (email, id_type, id_image_path, loan_purpose, monthly_income_range, status, date_submitted) "
                        + "VALUES (?, ?, ?, ?, ?, 'Pending', ?)";
                PreparedStatement ps = conn.prepareStatement(insertSql);
                ps.setString(1, email);
                ps.setString(2, idType);
                ps.setString(3, idImagePath);
                ps.setString(4, loanPurpose);
                ps.setString(5, monthlyIncomeRange);
                ps.setString(6, today);
                ps.executeUpdate();
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Used by the admin app to update a user's KYC status (Verified/Rejected).
     */
    public static boolean updateStatus(String email, String status) {
        String sql = "UPDATE kyc_records SET status = ?, date_reviewed = ? WHERE email = ?";
        String today = java.time.LocalDate.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, today);
            ps.setString(3, email);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
