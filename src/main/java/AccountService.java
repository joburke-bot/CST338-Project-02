package main.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.mindrot.jbcrypt.BCrypt; // Requires jbcrypt dependency
/**
 * Course: CST338 - Software Design
 * Project: Trivia and Study Application
 * Component: Slice 1 (Accounts) - Core Data Access and Business Logic Service
 * Encapsulates all authentication data transactions and security policies.
 * This class validates registration rules, enforces global unique index checks inside SQLite
 * via parameterized PreparedStatement templates to eliminate SQL injection vulnerabilities,
 * and utilizes the industrial-grade BCrypt cryptographic algorithm for hashing and verifying passwords.
 * Author: Josh Burke
 * Date: July 28, 2026
 */

public final class AccountService {

    private final Connection connection;

    // Pass the active SQLite database connection into the service
    public AccountService(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password_hash TEXT NOT NULL" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database tables", e);
        }
    }

    public User register(String username, String password, String confirmedPassword) {
        // 1. Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.length() < 6) { // Example constraint
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        if (!password.equals(confirmedPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        // 2. Check username availability
        String checkSql = "SELECT 1 FROM users WHERE username = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setString(1, username.trim().toLowerCase());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new IllegalArgumentException("Username is already taken.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during validation", e);
        }

        // 3. Hash password using BCrypt
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        // 4. Insert user
        String insertSql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, username.trim().toLowerCase());
            insertStmt.setString(2, passwordHash);
            insertStmt.executeUpdate();

            // 5. Return created user with generated ID
            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    return new User(id, username);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to register user", e);
        }
    }

    public User login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null.");
        }

        // 1. Find user
        String sql = "SELECT id, password_hash FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username.trim().toLowerCase());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String passwordHash = rs.getString("password_hash");

                    // 2. Verify password hash
                    if (BCrypt.checkpw(password, passwordHash)) {
                        // 3. Return authenticated user
                        return new User(id, username);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during login", e);
        }

        // Throw generic message to prevent username enumeration exploits
        throw new IllegalArgumentException("Invalid username or password.");
    }
}
