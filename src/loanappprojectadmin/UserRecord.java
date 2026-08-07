/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loanappprojectadmin;

public class UserRecord {

    private String email;
    private String name;
    private String phone;
    private String kycStatus;

    public UserRecord(String email, String name, String phone, String kycStatus) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.kycStatus = kycStatus;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(String kycStatus) {
        this.kycStatus = kycStatus;
    }
}
