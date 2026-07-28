package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.net.URL;
/**
 * Course: CST338 - Software Design
 * Project: Trivia and Study Application
 * Component: Slice 1 (Accounts) - Main Application Configuration Window
 *
 * Purpose: Manages the base lifecycle of the JavaFX desktop stage.
 * This class establishes the initial secure connection to the SQLite local database file,
 * registers runtime driver instances, programmatically builds loaders to decouple
 * layout concerns from business logic layers, and opens the initial Login screen layout.
 * Author: Josh Burke
 * Date: 2026.7.28
*/
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("[DIAGNOSTIC] Starting application setup...");

            // 1. Force the JVM to load the SQLite driver class explicitly into memory
            System.out.println("[DIAGNOSTIC] Registering SQLite JDBC driver class...");
            Class.forName("org.sqlite.JDBC");

            // 2. Connect database
            System.out.println("[DIAGNOSTIC] Connecting to SQLite...");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:trivia.db");
            AccountService accountService = new AccountService(conn);
            System.out.println("[DIAGNOSTIC] SQLite Database successfully connected.");

            // 3. Robust FXML Location Resolution Matrix
            System.out.println("[DIAGNOSTIC] Locating Login.fxml layout file...");
            URL fxmlUrl = null;

            // Strategy A: Try loading relative to the current class location
            fxmlUrl = MainApp.class.getResource("Login.fxml");

            // Strategy B: Try loading using an absolute package namespace path context
            if (fxmlUrl == null) {
                fxmlUrl = MainApp.class.getResource("/main/java/Login.fxml");
            }

            // Strategy C: Try loading from a nested view subdirectory structure
            if (fxmlUrl == null) {
                fxmlUrl = MainApp.class.getResource("/view/Login.fxml");
            }

            // Strategy D: Fallback to root global execution directory context
            if (fxmlUrl == null) {
                fxmlUrl = MainApp.class.getResource("/Login.fxml");
            }

            // If completely missing from production target paths, display error
            if (fxmlUrl == null) {
                throw new java.io.FileNotFoundException(
                        "CRITICAL ERROR: Could not find 'Login.fxml'.\n" +
                                "-> Ensure 'Login.fxml' is placed in your source directory next to MainApp.java.\n" +
                                "-> Try clicking: Top Menu -> Build -> Rebuild Project."
                );
            }
            System.out.println("[DIAGNOSTIC] Found layout file at: " + fxmlUrl);

            // 4. Initialize loader and controller programmatically
            FXMLLoader loader = new FXMLLoader(fxmlUrl);

            // Clean alternative: Let's pass dependencies programmatically without double-binding
            LoginController controller = new LoginController();
            controller.setAccountService(accountService);
            loader.setController(controller);

            // 5. Mount and Render Scene
            Parent root = loader.load();
            primaryStage.setTitle("Trivia & Study App - Login");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            System.out.println("[DIAGNOSTIC] Login window successfully rendered on screen.");

        } catch (Exception e) {
            System.err.println("\n=== CRITICAL APPLICATION LAUNCH FAILURE ===");
            e.printStackTrace();
            System.err.println("===========================================\n");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
