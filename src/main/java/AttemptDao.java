/**
 * CST 338
 * Project 2
 * Slice 3 (Quiz Engine) - Attempts data access
 * Handles CRUD operations for completed quiz attempts.
 *
 * Author: Allan Orozco
 * Date August 4, 2026
 */
package main.java;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AttemptDao {

    private final Connection connection;

    public AttemptDao(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS attempts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "score INTEGER NOT NULL, " +
                "total_questions INTEGER NOT NULL, " +
                "completed_at TEXT NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize attempts table", e);
        }
    }

    public Attempt create(Attempt attempt) {
        String sql = "INSERT INTO attempts " +
                "(user_id, score, total_questions, completed_at) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
        )) {
            stmt.setInt(1, attempt.getUserId());
            stmt.setInt(2, attempt.getScore());
            stmt.setInt(3, attempt.getTotalQuestions());
            stmt.setString(4, attempt.getCompletedAt());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Attempt(
                            keys.getInt(1),
                            attempt.getUserId(),
                            attempt.getScore(),
                            attempt.getTotalQuestions(),
                            attempt.getCompletedAt()
                    );
                }

                throw new SQLException("Creating attempt failed, no ID obtained.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create attempt", e);
        }
    }

    public Optional<Attempt> read(int id) {
        String sql = "SELECT * FROM attempts WHERE id = ?";

        try (PreparedStatement stmt =
                     connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read attempt " + id, e);
        }
    }

    public List<Attempt> readAll() {
        String sql = "SELECT * FROM attempts";

        try (PreparedStatement stmt =
                     connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<Attempt> attempts = new ArrayList<>();

            while (rs.next()) {
                attempts.add(mapRow(rs));
            }

            return attempts;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read attempts", e);
        }
    }

    public List<Attempt> readAllByUser(int userId) {
        String sql =
                "SELECT * FROM attempts WHERE user_id = ?";

        try (PreparedStatement stmt =
                     connection.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Attempt> attempts = new ArrayList<>();

                while (rs.next()) {
                    attempts.add(mapRow(rs));
                }

                return attempts;
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to read attempts for user " + userId, e);
        }
    }

    public boolean update(Attempt attempt) {
        String sql = "UPDATE attempts " +
                "SET score = ?, total_questions = ?, " +
                "completed_at = ? WHERE id = ?";

        try (PreparedStatement stmt =
                     connection.prepareStatement(sql)) {

            stmt.setInt(1, attempt.getScore());
            stmt.setInt(2, attempt.getTotalQuestions());
            stmt.setString(3, attempt.getCompletedAt());
            stmt.setInt(4, attempt.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to update attempt " + attempt.getId(), e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM attempts WHERE id = ?";

        try (PreparedStatement stmt =
                     connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete attempt " + id, e);
        }
    }

    private Attempt mapRow(ResultSet rs) throws SQLException {
        return new Attempt(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("score"),
                rs.getInt("total_questions"),
                rs.getString("completed_at")
        );
    }
}
