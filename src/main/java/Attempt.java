/**
 * CST 338
 * Project 2
 * Slice 3 (Quiz Engine) - Quiz attempt
 * Represents one completed quiz attempt.
 *
 * Author: Allan Orozco
 * Date August 4, 2026
 */
package main.java;

public class Attempt {
    private final int id;
    private final int userId;
    private final int score;
    private final int totalQuestions;
    private final String completedAt;

    public Attempt(int id, int userId, int score, int totalQuestions, String completedAt) {
        this.id = id;
        this.userId = userId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.completedAt = completedAt;
    }

    public int getId() {
        return id;
    }
    public int getUserId() {
        return userId;
    }
    public int getScore() {
        return score;
    }
    public int getTotalQuestions() {
        return totalQuestions;
    }
    public String getCompletedAt() {
        return completedAt;
    }
}
