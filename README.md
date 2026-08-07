# CashMate

A JavaFX desktop loan application with a companion admin dashboard. Users sign up, verify their identity, request loans, and track their wallet — while admins review KYC submissions and manage loan decisions from a separate module.

## Features

**User app (`loanappproject`)**
- Sign up / login with email + OTP verification before an account is created
- Real deliverability check on the email address (not just format validation) before OTP is sent
- KYC verification flow with a status indicator on the dashboard
- Request a loan, view loan details, and track repayment (`RequestLoan`, `MyLoan`, `LoanDetails`)
- Wallet breakdown view
- Forgot/reset login flow

**Admin app (`loanappprojectadmin`)**
- Separate admin login (`AdminAccess`) and dashboard
- Review and approve/reject KYC submissions (`KYCDecisionPopup`)
- Review and decide on loan requests (`DecisionPopup`)
- Admin password reset flow

**Email verification module (`emailverication`)**
- `EmailExistenceChecker` — checks whether an email address is actually deliverable using [AbstractAPI's Email Reputation API](https://www.abstractapi.com/api/email-reputation-api), rather than just an MX-record lookup
- `EmailService` / `EmailTemplate` — sends verification emails over SendGrid
- `OTPGenerator` / `OTPManager` — generates and validates one-time passcodes

## Tech stack

- **Java** (JavaFX for UI, FXML for layouts) — NetBeans/Ant project
- **MySQL** for persistence (`DBConnect`)
- **AbstractAPI** — Email Reputation API for deliverability checks
- **SendGrid** — transactional email delivery

## Project structure

```
CashMate/
├── loanappproject/       # User-facing JavaFX app (entry point: LoanAppProject.java)
│   └── IMAGES/           # UI assets/icons
├── loanappprojectadmin/  # Admin JavaFX app (entry point: AdminApp.java)
└── emailverication/      # Shared email verification (deliverability check, OTP, SendGrid)
```

## Setup

### 1. Database
Create a MySQL database named `loan_app` and update the connection details in `loanappproject/DBConnect.java`:

```java
String url = "jdbc:mysql://localhost:3306/loan_app";
String user = "root";
String password = "your-password";
```

### 2. Email verification (SendGrid + AbstractAPI)
Both are read from a system property or environment variable — nothing is hardcoded here. Set these before running:

| Purpose | System property | Environment variable |
|---|---|---|
| SendGrid API key | `sendgrid.api.key` | `SENDGRID_API_KEY` |
| SendGrid sender address | `cashmate.mail.user` | `CASHMATE_MAIL_USER` |
| AbstractAPI Email Reputation key | `abstractapi.email.key` | `ABSTRACTAPI_EMAIL_KEY` |

In NetBeans: right-click the project → **Properties → Run → VM Options**, and add:
```
-Dsendgrid.api.key=SG.yourkeyhere -Dcashmate.mail.user=you@example.com -Dabstractapi.email.key=yourkeyhere
```

- Get a SendGrid key: **Settings → API Keys** (enable Mail Send), then verify a sender under **Settings → Sender Authentication**.
- Get an AbstractAPI key: [abstractapi.com](https://www.abstractapi.com) → Email Reputation product → View API keys (free tier: 100 requests/month at time of writing).

### 3. Run
Open `loanappproject` in NetBeans (or your Ant-compatible IDE) and run `LoanAppProject.java` for the user app. Open `loanappprojectadmin` and run `AdminApp.java` for the admin dashboard.

## Screenshots

<img width="1920" height="1045" alt="image" src="https://github.com/user-attachments/assets/53826f24-a255-4498-a215-a67f753464bf" />
<img width="1919" height="1048" alt="image" src="https://github.com/user-attachments/assets/f7cd8907-6c32-4de1-af2b-986e968d9853" />
<img width="1920" height="1048" alt="image" src="https://github.com/user-attachments/assets/d12b0f82-3611-499d-a465-2bb0ac50f377" />
<img width="1920" height="1047" alt="image" src="https://github.com/user-attachments/assets/fcca1d92-0b1a-474e-a0a3-c7b36a718592" />
<img width="1920" height="1046" alt="image" src="https://github.com/user-attachments/assets/3050cbe8-06da-420d-91d8-da6315d17319" />
<img width="1920" height="1047" alt="image" src="https://github.com/user-attachments/assets/d28b58a5-3717-45fb-a150-f25783d9495b" />
<img width="1920" height="1046" alt="image" src="https://github.com/user-attachments/assets/365f90be-9c04-4384-94ac-2e9bdd6351dd" />
<img width="1920" height="1045" alt="image" src="https://github.com/user-attachments/assets/8e923129-d159-4d9c-a34e-b8358a77d15c" />
<img width="1920" height="1047" alt="image" src="https://github.com/user-attachments/assets/a8e96484-9eb8-4283-8a33-b0af734ee8f1" />
<img width="1920" height="1046" alt="Screenshot 2026-08-07 015940" src="https://github.com/user-attachments/assets/14bb4ecd-220d-43d3-a64a-2c6ecb38eb9e" />
<img width="1920" height="1043" alt="image" src="https://github.com/user-attachments/assets/53a65bb3-0124-4c28-b703-7b3250d03ad7" />
<img width="1920" height="1043" alt="image" src="https://github.com/user-attachments/assets/933d6306-2861-4fd9-b6f0-90a9a916df68" />
<img width="1920" height="1046" alt="image" src="https://github.com/user-attachments/assets/9a145b33-af0b-4d4a-a367-5ac8cd4cd30e" />
