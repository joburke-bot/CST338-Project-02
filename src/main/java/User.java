package main.java;
/**
 * Course: CST338 - Software Design
 * Project: Trivia and Study Application
 * Component: Slice 1 (Accounts) - User Domain Entity Model
 *
 * Purpose: A immutable data transfer object (DTO) that models an individual
 * user account profile. It holds foundational identifier records retrieved from the database,
 * protecting account details from raw SQL column operations throughout runtime views.
 * Author: Josh Burke
 * Date: 2026.7.28
 */

public class User {
    private final int id;
    private final String username;

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
}
