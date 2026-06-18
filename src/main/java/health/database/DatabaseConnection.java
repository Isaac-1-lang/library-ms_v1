package health.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database Connection Management Class
 * Handles database connection and schema initialization
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    /**
     * Get a database connection
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Initialize database schema - Task 1: Database Schema Design
     * Creates tables: books, members, borrowing_records
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create books table
            String createBooksTable = """
                CREATE TABLE IF NOT EXISTS books (
                    ISBN VARCHAR(20) PRIMARY KEY,
                    Title VARCHAR(255) NOT NULL,
                    Author VARCHAR(255) NOT NULL,
                    PublicationYear INT NOT NULL,
                    Available BOOLEAN DEFAULT TRUE
                )
                """;
            stmt.execute(createBooksTable);

            // Create members table
            String createMembersTable = """
                CREATE TABLE IF NOT EXISTS members (
                    MemberID VARCHAR(20) PRIMARY KEY,
                    Name VARCHAR(255) NOT NULL
                )
                """;
            stmt.execute(createMembersTable);

            // Create borrowing_records table
            String createBorrowingRecordsTable = """
                CREATE TABLE IF NOT EXISTS borrowing_records (
                    RecordID INT PRIMARY KEY AUTO_INCREMENT,
                    MemberID VARCHAR(20) NOT NULL,
                    ISBN VARCHAR(20) NOT NULL,
                    BorrowDate DATE NOT NULL,
                    ReturnDate DATE,
                    FOREIGN KEY (MemberID) REFERENCES members(MemberID),
                    FOREIGN KEY (ISBN) REFERENCES books(ISBN)
                )
                """;
            stmt.execute(createBorrowingRecordsTable);

            System.out.println("✓ Database schema initialized successfully");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
