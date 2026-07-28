package main.java;

/**
 * Course: CST338 - Software Design
 * Project: Trivia and Study Application
 * Component: Slice 1 (Accounts) - Global Singleton Session Manager
 *
 * Purpose: Provides a synchronized, thread-safe global session cache utilizing
 * the Singleton Pattern. It preserves the authenticated state of the logged-in User
 * across decoupled JavaFX UI views, enabling teammates to securely access the active
 * student profile context for logging quiz history, computing scoreboard rows, and
 * loading personalized settings.
 * Author: Josh Burke
 * Date: 2026.7.28
 */

public final class UserSession {

    private static UserSession instance;
    private User currentUser;

    // Private constructor blocks external construction initialization loops
    private UserSession() {}

    /**
     * Exposes the single synchronized access anchor point to pull session states.
     */
    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Links the profile record to the active lifecycle window upon successful validation login.
     */
    public void startSession(User user) {
        this.currentUser = user;
        System.out.println("[SESSION DIAGNOSTIC] Session started for user identity ID: " + user.getId());
    }

    /**
     * Yields the current active User entity properties.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Evaluates whether an authentication state is presently cached.
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Wipes active cached credentials upon explicit logout events.
     */
    public void cleanUserSession() {
        if (currentUser != null) {
            System.out.println("[SESSION DIAGNOSTIC] Purging active session state for user: " + currentUser.getUsername());
        }
        this.currentUser = null;
    }
}
