package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Course: CST338 - Software Design
 * Project: Trivia and Study Application
 * Component: Slice 2 (Question Bank) - standalone window to run the bank
 *
 * runs the question bank as its own window for now. later the dashboard
 * from slice 1 can open the same fxml.
 * Author: William Delgado
 * Date: August 4, 2026
 */
public class QuestionBankApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // same setup MainApp does, load the driver then open the db
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:trivia.db");
            QuestionDao questionDao = new QuestionDao(connection);

            // no login in front of this window yet, so if nobody is signed in
            // just use user 1 until this hooks into the login flow
            int userId = 1;
            if (UserSession.getInstance().isLoggedIn()) {
                userId = UserSession.getInstance().getCurrentUser().getId();
            }

            URL fxmlUrl = QuestionBankApp.class.getResource("QuestionBank.fxml");
            if (fxmlUrl == null) {
                fxmlUrl = QuestionBankApp.class.getResource("/main/java/QuestionBank.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            QuestionBankController controller = new QuestionBankController();
            controller.setQuestionDao(questionDao);
            controller.setUserId(userId);
            loader.setController(controller);

            Parent root = loader.load();

            // load whats already saved
            controller.loadQuestions();

            primaryStage.setTitle("Trivia & Study App - Question Bank");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
