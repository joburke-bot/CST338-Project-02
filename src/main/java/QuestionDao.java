package main.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Course: CST338 - Software Design
 * Project: Trivia and Study Application
 * Component: Slice 2 (Question Bank) - Data access for the questions table
 *
 * all the sql for questions is in here. Takes the shared SQLite
 * connection like AccountService does and creates
 * its own table on construction. Wrong answers are columns not a join table
 * so the quiz engine (slice 3) gets a whole question in one SELECT.
 * Author: William Delgado
 * Date: August 4, 2026
 */
public class QuestionDao {

    private final Connection connection;

    public QuestionDao(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS questions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "prompt TEXT NOT NULL, " +
                "answer TEXT NOT NULL, " +
                "wrong1 TEXT, " +
                "wrong2 TEXT, " +
                "wrong3 TEXT, " +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize questions table", e);
        }
    }

    // insert, then return the same question carrying its generated id
    public Question create(Question question) {
        String sql = "INSERT INTO questions (user_id, prompt, answer, wrong1, wrong2, wrong3) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, question.getUserId());
            stmt.setString(2, question.getPrompt());
            stmt.setString(3, question.getAnswer());
            stmt.setString(4, question.getWrong1());
            stmt.setString(5, question.getWrong2());
            stmt.setString(6, question.getWrong3());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Question(keys.getInt(1), question.getUserId(),
                            question.getPrompt(), question.getAnswer(),
                            question.getWrong1(), question.getWrong2(), question.getWrong3());
                }
                throw new SQLException("Creating question failed, no ID obtained.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create question", e);
        }
    }

    public Optional<Question> read(int id) {
        String sql = "SELECT * FROM questions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read question " + id, e);
        }
    }

    public List<Question> readAll() {
        String sql = "SELECT * FROM questions";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Question> questions = new ArrayList<>();
            while (rs.next()) {
                questions.add(mapRow(rs));
            }
            return questions;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read questions", e);
        }
    }

    // one user's questions, for the manage-my-questions screen
    public List<Question> readAllByUser(int userId) {
        String sql = "SELECT * FROM questions WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Question> questions = new ArrayList<>();
                while (rs.next()) {
                    questions.add(mapRow(rs));
                }
                return questions;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read questions for user " + userId, e);
        }
    }

    public boolean update(Question question) {
        String sql = "UPDATE questions SET prompt = ?, answer = ?, wrong1 = ?, wrong2 = ?, wrong3 = ? " +
                "WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, question.getPrompt());
            stmt.setString(2, question.getAnswer());
            stmt.setString(3, question.getWrong1());
            stmt.setString(4, question.getWrong2());
            stmt.setString(5, question.getWrong3());
            stmt.setInt(6, question.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update question " + question.getId(), e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM questions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete question " + id, e);
        }
    }

    private Question mapRow(ResultSet rs) throws SQLException {
        return new Question(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("prompt"),
                rs.getString("answer"),
                rs.getString("wrong1"),
                rs.getString("wrong2"),
                rs.getString("wrong3"));
    }
}
