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
    private double amount;
    private String duration;
    private double interest;
    private double repayment;
    private String date;
    private String status;

    public Loan(int id, String name, double amount, String duration,
            double interest, double repayment, String date, String status) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.duration = duration;
        this.interest = interest;
        this.repayment = repayment;
        this.date = date;
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

    public String getStatus() {
        return status;
    }
}
