package main.java;

/**
 * Course: CST338 - Software Design
 * Project: Trivia and Study Application
 * Component: Slice 2 (Question Bank) - Question domain entity
 *
 * Immutable DTO for one trivia question. Mirrors the shape of User.java.
 * No setters, build a new one if a question changes.
 * Author: William Delgado
 * Date: August 4, 2026
 */
public class Question {
    private final int id;
    private final int userId;
    private final String prompt;
    private final String answer;
    private final String wrong1;
    private final String wrong2;
    private final String wrong3;

    public Question(int id, int userId, String prompt, String answer,
                    String wrong1, String wrong2, String wrong3) {
        this.id = id;
        this.userId = userId;
        this.prompt = prompt;
        this.answer = answer;
        this.wrong1 = wrong1;
        this.wrong2 = wrong2;
        this.wrong3 = wrong3;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getPrompt() { return prompt; }
    public String getAnswer() { return answer; }
    public String getWrong1() { return wrong1; }
    public String getWrong2() { return wrong2; }
    public String getWrong3() { return wrong3; }
}
