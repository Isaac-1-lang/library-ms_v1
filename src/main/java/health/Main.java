package health;

import health.database.DatabaseConnection;
import health.menu.LibraryMenu;
import health.service.LibraryService;

/**
 * Rwanda National Digital Library Management System
 * Main class with interactive menu
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("RWANDA NATIONAL DIGITAL LIBRARY - LIBRARY MANAGEMENT SYSTEM");
        System.out.println("=".repeat(80));
        System.out.println("Initializing database...");

        // Initialize database schema
        DatabaseConnection.initializeDatabase();
        System.out.println("✓ Database initialized successfully!\n");

        // Create library service
        LibraryService libraryService = new LibraryService();

        // Start interactive menu
        LibraryMenu menu = new LibraryMenu(libraryService);
        menu.displayMainMenu();
    }
}
