package health.service;

import health.database.DatabaseConnection;
import health.model.Book;
import health.model.Member;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * LibraryService - Task 2: Encapsulation & Task 5: Data Persistence
 * Service/DAO class responsible for managing system operations
 */
public class LibraryService {
    private static final int MAX_BOOKS_PER_MEMBER = 5; // Task 3: Borrowing limit

    /**
     * Add a new book to the library
     */
    public synchronized void addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (ISBN, Title, Author, PublicationYear, Available) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, book.getIsbn());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setInt(4, book.getPublicationYear());
            pstmt.setBoolean(5, book.isAvailable());
            
            pstmt.executeUpdate();
            System.out.println("✓ Book added: " + book.getTitle());
            
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Register a new member
     */
    public synchronized void registerMember(Member member) throws SQLException {
        String sql = "INSERT INTO members (MemberID, Name) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, member.getMemberId());
            pstmt.setString(2, member.getName());
            
            pstmt.executeUpdate();
            System.out.println("✓ Member registered: " + member.getName());
            
        } catch (SQLException e) {
            System.err.println("Error registering member: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Task 3 & 5: Borrowing Logic with Collections validation and JDBC persistence
     * Thread-safe method to borrow a book
     */
    public synchronized boolean borrowBook(String memberId, String isbn) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Check if member exists
            if (!memberExists(conn, memberId)) {
                System.err.println("✗ Member not found: " + memberId);
                return false;
            }

            // Check if book exists and is available
            if (!isBookAvailable(conn, isbn)) {
                System.err.println("✗ Book not available: " + isbn);
                return false;
            }

            // Task 3: Check borrowing limit (max 5 books)
            int currentBorrowedCount = getMemberBorrowedCount(conn, memberId);
            if (currentBorrowedCount >= MAX_BOOKS_PER_MEMBER) {
                System.err.println("✗ Member " + memberId + " has reached the borrowing limit of " + MAX_BOOKS_PER_MEMBER + " books");
                return false;
            }

            // Start transaction
            conn.setAutoCommit(false);

            try {
                // Insert borrowing record
                String insertRecord = "INSERT INTO borrowing_records (MemberID, ISBN, BorrowDate) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertRecord)) {
                    pstmt.setString(1, memberId);
                    pstmt.setString(2, isbn);
                    pstmt.setDate(3, Date.valueOf(LocalDate.now()));
                    pstmt.executeUpdate();
                }

                // Mark book as unavailable
                String updateBook = "UPDATE books SET Available = FALSE WHERE ISBN = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateBook)) {
                    pstmt.setString(1, isbn);
                    pstmt.executeUpdate();
                }

                // Commit transaction
                conn.commit();
                System.out.println("✓ Book borrowed successfully: Member=" + memberId + ", ISBN=" + isbn);
                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("✗ Error during borrowing transaction: " + e.getMessage());
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("✗ Database error during borrowing: " + e.getMessage());
            return false;
        }
    }

    /**
     * Task 5: Returning Logic - Return a book and update database
     */
    public synchronized boolean returnBook(String memberId, String isbn) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Check if there's an active borrowing record
            String checkRecord = "SELECT RecordID FROM borrowing_records WHERE MemberID = ? AND ISBN = ? AND ReturnDate IS NULL";
            int recordId = -1;
            
            try (PreparedStatement pstmt = conn.prepareStatement(checkRecord)) {
                pstmt.setString(1, memberId);
                pstmt.setString(2, isbn);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        recordId = rs.getInt("RecordID");
                    } else {
                        System.err.println("✗ No active borrowing record found");
                        return false;
                    }
                }
            }

            // Start transaction
            conn.setAutoCommit(false);

            try {
                // Update borrowing record with return date
                String updateRecord = "UPDATE borrowing_records SET ReturnDate = ? WHERE RecordID = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateRecord)) {
                    pstmt.setDate(1, Date.valueOf(LocalDate.now()));
                    pstmt.setInt(2, recordId);
                    pstmt.executeUpdate();
                }

                // Mark book as available
                String updateBook = "UPDATE books SET Available = TRUE WHERE ISBN = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateBook)) {
                    pstmt.setString(1, isbn);
                    pstmt.executeUpdate();
                }

                // Commit transaction
                conn.commit();
                System.out.println("✓ Book returned successfully: Member=" + memberId + ", ISBN=" + isbn);
                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("✗ Error during return transaction: " + e.getMessage());
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("✗ Database error during return: " + e.getMessage());
            return false;
        }
    }

    /**
     * Task 3: Display list of all available books using Collections
     */
    public List<Book> getAvailableBooks() {
        List<Book> availableBooks = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE Available = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Book book = new Book(
                    rs.getString("ISBN"),
                    rs.getString("Title"),
                    rs.getString("Author"),
                    rs.getInt("PublicationYear")
                );
                book.setAvailable(rs.getBoolean("Available"));
                availableBooks.add(book);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving available books: " + e.getMessage());
        }

        return availableBooks;
    }

    /**
     * Get books currently borrowed by a member
     */
    public List<Book> getMemberBorrowedBooks(String memberId) {
        List<Book> borrowedBooks = new ArrayList<>();
        String sql = """
            SELECT b.* FROM books b
            INNER JOIN borrowing_records br ON b.ISBN = br.ISBN
            WHERE br.MemberID = ? AND br.ReturnDate IS NULL
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book(
                        rs.getString("ISBN"),
                        rs.getString("Title"),
                        rs.getString("Author"),
                        rs.getInt("PublicationYear")
                    );
                    book.setAvailable(rs.getBoolean("Available"));
                    borrowedBooks.add(book);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving member's borrowed books: " + e.getMessage());
        }

        return borrowedBooks;
    }

    // Helper methods

    private boolean memberExists(Connection conn, String memberId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM members WHERE MemberID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private boolean isBookAvailable(Connection conn, String isbn) throws SQLException {
        String sql = "SELECT Available FROM books WHERE ISBN = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("Available");
                }
            }
        }
        return false;
    }

    private int getMemberBorrowedCount(Connection conn, String memberId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM borrowing_records WHERE MemberID = ? AND ReturnDate IS NULL";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}
