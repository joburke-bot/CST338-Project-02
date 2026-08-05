/**
 * CST 338
 * Project 2
 * Slice 3 (Quiz Engine)
 * Author: Allan Orozco
 * Date August 4, 2026
 */
package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;

public class QuizEngineApp extends Application {
    private Connection connection;

    @Override
    public void start(Stage primaryStage) {
        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection(
                    "jdbc:sqlite:trivia.db"
            );

            // Initializes the attempts table.
            new AttemptDao(connection);

            URL fxmlUrl =
                    QuizEngineApp.class.getResource("QuizEngine.fxml");

            if (fxmlUrl == null) {
                fxmlUrl = QuizEngineApp.class.getResource(
                        "/main/java/QuizEngine.fxml"
                );
            }

            if (fxmlUrl == null) {
                throw new IllegalStateException(
                        "Could not locate QuizEngine.fxml"
                );
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            primaryStage.setTitle(
                    "Trivia & Study App - Quiz Engine"
            );
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }

        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
