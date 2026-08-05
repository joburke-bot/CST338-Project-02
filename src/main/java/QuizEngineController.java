/**
 * CST 338
 * Project 2
 * Slice 3 (Quiz Engine)
 * QuizEngineController
 *
 * Author: Allan Orozco
 * Date August 4, 2026
 */
package main.java;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;


public class QuizEngineController {
    @FXML
    private Label statusLabel;

    @FXML
    private void handleSubmitAnswer() {
        statusLabel.setText("Answer submitted.");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz Engine");
        alert.setHeaderText("Answer received");
        alert.setContentText(
                "Scoring will be completed in the next milestone."
        );
        alert.showAndWait();
    }
}
