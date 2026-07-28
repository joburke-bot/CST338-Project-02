package main.java;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
/**
 * Course: CST338 - Software Design
 * Project: Trivia and Study Application
 * Component: Slice 1 (Accounts) - FXML Registration UI Event Logic Controller
 *
 * Purpose: Connects the front-end layout properties of the registration menu (Register.fxml)
 * straight into the service tier logic. It gathers registration data components, validates complexity
 * expectations across input variables, handles specific error message alerts, and recycles window stages
 * to coordinate screen swaps between workspace menus.
 * Author: Josh Burke
 * Date: July 28, 2026
 */

public class RegisterController {

    @FXML
    private TextField txtNewUsername;

    @FXML
    private PasswordField txtNewPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private Label lblValidationMessage;

    private AccountService accountService;

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Triggered when the user clicks the "Submit Registration" button.
     */
    @FXML
    public void handleSubmitRegistration() {
        lblValidationMessage.setText("");
        lblValidationMessage.setStyle("-fx-text-fill: red;");
        lblValidationMessage.setVisible(false);

        String username = txtNewUsername.getText();
        String password = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        try {
            // 1. Pass data to AccountService for validation and hashing insertion
            User newUser = accountService.register(username, password, confirmPassword);

            // 2. Display success message to user
            lblValidationMessage.setStyle("-fx-text-fill: green;");
            lblValidationMessage.setText("Registration successful for " + newUser.getUsername() + "! Redirecting...");
            lblValidationMessage.setVisible(true);

            // 3. Clear fields
            txtNewUsername.clear();
            txtNewPassword.clear();
            txtConfirmPassword.clear();

            // 4. Return to the Login screen after a brief moment or instantly
            handleCancel();

        } catch (IllegalArgumentException e) {
            // Handle mismatched passwords, empty text fields, or taken usernames
            lblValidationMessage.setText(e.getMessage());
            lblValidationMessage.setVisible(true);
        } catch (RuntimeException e) {
            lblValidationMessage.setText("A database error occurred. Please try again.");
            lblValidationMessage.setVisible(true);
        }
    }

    /**
     * Triggered when the user clicks "Cancel" or needs to return to login.
     */
    @FXML
    public void handleCancel() {
        try {
            var fxmlUrl = getClass().getResource("Login.fxml");
            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("/main/java/Login.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            LoginController loginController = new LoginController();
            loginController.setAccountService(this.accountService);
            loader.setController(loginController);

            Parent root = loader.load();
            Stage stage = (Stage) txtNewUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Trivia & Study App - Login");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            lblValidationMessage.setText("Failed to return to the login screen.");
            lblValidationMessage.setVisible(true);
        }
    }
}
