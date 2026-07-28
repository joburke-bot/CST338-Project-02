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
import javafx.scene.layout.VBox; // Add this import
import javafx.geometry.Insets;   // Add this import
import javafx.geometry.Pos;      // Add this import
/**
 * Course: CST338 - Software Design
 * Project: Trivia and Study Application
 * Component: Slice 1 (Accounts) - FXML UI Event Logic Controller
 *
 * Purpose: Binds user event interactions captured by the graphical interface (Login.fxml)
 * to backend business capabilities. It safely evaluates user text selections, dynamically routes
 * validation exceptions back into user warning components, commands authentication validations,
 * manages safe session instantiation setups, and controls application layout scene shifts.
 * Author: Josh Burke
 * Date: July 28, 2026
 */

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblErrorMessage;

    private AccountService accountService;

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @FXML
    public void handleLogin() {
        lblErrorMessage.setText("");
        lblErrorMessage.setVisible(false);

        String username = txtUsername.getText();
        String password = txtPassword.getText();

        try {
            // 1. Safe SQLite Check & BCrypt Verification Flow
            User authenticatedUser = accountService.login(username, password);

            // 2. Establish Global Thread-Safe User Session
            UserSession.getInstance().startSession(authenticatedUser);

            // 3. Move to dashboard screen layout
            navigateToDashboard();
        } catch (IllegalArgumentException e) {
            showError(e.getLocalizedMessage());
        } catch (RuntimeException e) {
            showError("A database connection error occurred. Please try again.");
        }
    }

    @FXML
    public void handleNavigateToRegister() {
        try {
            var fxmlUrl = getClass().getResource("Register.fxml");
            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("/main/java/Register.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            RegisterController registerController = new RegisterController();
            registerController.setAccountService(this.accountService);
            loader.setController(registerController);

            Parent root = loader.load();
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Trivia & Study App - Register");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to open registration view window.");
        }
    }

    private void showError(String message) {
        lblErrorMessage.setText(message);
        lblErrorMessage.setVisible(true);
    }

    /**
     * Programmatic landing layout placeholder to isolate your login test loop safely
     */
    private void navigateToDashboard() {
        // Fetch the currently active logged-in user context from your global session state
        User activeUser = UserSession.getInstance().getCurrentUser();

        // Build a temporary container programmatically to avoid missing FXML crashes
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        Label lblWelcome = new Label("Welcome back, " + activeUser.getUsername() + "!");
        lblWelcome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label lblTeammates = new Label("Dashboard and Trivia Quiz engine modules will load here.\n(Slice 1 Authentication verified successfully!)");
        lblTeammates.setStyle("-fx-font-size: 13px; -fx-text-alignment: center; -fx-text-fill: #7f8c8d;");

        javafx.scene.control.Button btnLogout = new javafx.scene.control.Button("Log Out");
        btnLogout.setPrefWidth(120);

        // FIX: Extract the active window stage directly from the button click event
        btnLogout.setOnAction(e -> {
            UserSession.getInstance().cleanUserSession(); // Clear session context

            // Get the active stage window using the logout button that was just clicked
            Stage activeStage = (Stage) btnLogout.getScene().getWindow();

            // Pass that stage directly to our fallback method
            handleCancelWithStage(activeStage);
        });

        layout.getChildren().addAll(lblWelcome, lblTeammates, btnLogout);

        // Mount and Render the placeholder scene
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.setScene(new Scene(layout, 400, 300));
        stage.setTitle("Trivia App - Main Dashboard Placeholder");
        stage.show();
    }

    /**
     * Reliable scene recycler that uses the active stage reference
     */
    private void handleCancelWithStage(Stage stage) {
        try {
            var fxmlUrl = getClass().getResource("Login.fxml");
            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("/main/java/Login.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setController(this); // Re-bind this controller instance programmatically
            Parent root = loader.load();

            // Use the verified stage passed from our button click listener
            stage.setScene(new Scene(root));
            stage.setTitle("Trivia & Study App - Login");
            stage.show();

            System.out.println("[DIAGNOSTIC] Log out successful. Returned to Login screen.");

        } catch (Exception ex) {
            System.err.println("[DIAGNOSTIC ERROR] Failed to switch back to Login screen during logout.");
            ex.printStackTrace();
        }
    }


    // Update this method at the bottom of LoginController.java
    private void handleCancelFallback() {
        try {
            // 1. Locate the FXML layout file again
            var fxmlUrl = getClass().getResource("Login.fxml");
            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("/main/java/Login.fxml");
            }

            // 2. Initialize a fresh FXML Loader
            FXMLLoader loader = new FXMLLoader(fxmlUrl);

            // 3. Re-bind THIS existing controller instance to the layout programmatically
            loader.setController(this);

            // 4. Load the view node tree layout
            Parent root = loader.load();

            // 5. Swap the active window screen scene back to the login page
            Stage stage = (Stage) lblErrorMessage.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Trivia & Study App - Login");
            stage.show();

            System.out.println("[DIAGNOSTIC] Log out successful. Returned to Login screen.");

        } catch (Exception ex) {
            System.err.println("[DIAGNOSTIC ERROR] Failed to switch back to Login screen during logout.");
            ex.printStackTrace();
        }
    }
}
