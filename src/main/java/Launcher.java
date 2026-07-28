package main.java; // Tells the IDE exactly what folder it is in
/**
 * Course: CST338 - Software Design
 * Project: Trivia and Study Application
 * Component: Slice 1 (Accounts) - Application Bootstrap Launcher
 * Acts as a clean, decoupled entry point for the application.
 * By separating the main method from the Application class, this launcher
 * tricks the JVM into bypassing strict JavaFX runtime module checks,
 * ensuring seamless runtime execution without verbose VM configuration arguments.
 * Author: Josh Burke
 * Date: July 28, 2026
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
