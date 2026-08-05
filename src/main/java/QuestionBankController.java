package main.java;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import java.util.List;

/**
 * Course: CST338 - Software Design
 * Project: Trivia and Study Application
 * Component: Slice 2 (Question Bank) - controller for the question bank window
 *
 * the screen where a user manages their questions. shows them in a list,
 * the form adds a new one, delete removes the selected one.
 * Author: William Delgado
 * Date: August 4, 2026
 */
public class QuestionBankController {

    @FXML
    private ListView<String> listQuestions;

    @FXML
    private TextField txtPrompt;

    @FXML
    private TextField txtAnswer;

    @FXML
    private TextField txtWrong1;

    @FXML
    private TextField txtWrong2;

    @FXML
    private TextField txtWrong3;

    @FXML
    private Label lblMessage;

    private QuestionDao questionDao;
    private int userId;

    // the questions currently on screen, same order as the list rows
    private List<Question> loadedQuestions = new ArrayList<>();

    public void setQuestionDao(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // grab my questions from the db and refill the list
    public void loadQuestions() {
        loadedQuestions = questionDao.readAllByUser(userId);
        listQuestions.getItems().clear();
        for (Question question : loadedQuestions) {
            listQuestions.getItems().add(question.getPrompt());
        }
    }

    @FXML
    public void handleAdd() {
        lblMessage.setVisible(false);

        String prompt = txtPrompt.getText();
        String answer = txtAnswer.getText();

        // dont save empty questions
        if (prompt.trim().isEmpty()) {
            lblMessage.setText("prompt cant be empty");
            lblMessage.setVisible(true);
            return;
        }
        if (answer.trim().isEmpty()) {
            lblMessage.setText("answer cant be empty");
            lblMessage.setVisible(true);
            return;
        }

        // new question, the db picks the id
        Question newQuestion = new Question(0, userId, prompt, answer,
                txtWrong1.getText(), txtWrong2.getText(), txtWrong3.getText());
        questionDao.create(newQuestion);

        // clear the form so its ready for the next one
        txtPrompt.clear();
        txtAnswer.clear();
        txtWrong1.clear();
        txtWrong2.clear();
        txtWrong3.clear();

        loadQuestions();
    }

    @FXML
    public void handleDelete() {
        lblMessage.setVisible(false);

        int selectedIndex = listQuestions.getSelectionModel().getSelectedIndex();

        if (selectedIndex == -1) {
            lblMessage.setText("pick a question first");
            lblMessage.setVisible(true);
            return;
        }

        // the list rows and loadedQuestions are in the same order
        Question selected = loadedQuestions.get(selectedIndex);
        questionDao.delete(selected.getId());
        loadQuestions();
    }
}
