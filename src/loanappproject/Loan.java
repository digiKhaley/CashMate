/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loanappproject;

/**
 *
 * @author Kingsley Ezealisiobi
 */
public class Loan {

    private int id;
    private String name;
    private String email;
    private double amount;
    private String duration;
    private double interest;
    private double repayment;
    private String date;
    private String repaymentDate;
    private String status;

    public Loan(int id, String name, String email, double amount, String duration,
            double interest, double repayment, String date, String repaymentDate, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.amount = amount;
        this.duration = duration;
        this.interest = interest;
        this.repayment = repayment;
        this.date = date;
        this.repaymentDate = repaymentDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public String getDuration() {
        return duration;
    }

    public double getInterest() {
        return interest;
    }

    public double getRepayment() {
        return repayment;
    }

    public String getDate() {
        return date;
    }

    public String getEmail() {
        return email;
    }

    public String getRepaymentDate() {
        return repaymentDate;
    }

    public String getStatus() {
        return status;
    }
}   
