package health;

import health.database.DatabaseConnection;
import health.model.Book;
import health.model.Member;
import health.service.LibraryService;
import health.task.BorrowTask;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Rwanda National Digital Library Management System
 * Main class demonstrating all features
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("RWANDA NATIONAL DIGITAL LIBRARY - LIBRARY MANAGEMENT SYSTEM");
        System.out.println("=".repeat(80));
        System.out.println();

        // Initialize database schema
        System.out.println("--- Initializing Database ---");
        DatabaseConnection.initializeDatabase();
        System.out.println();

        LibraryService libraryService = new LibraryService();

        try {
            // Task 2 & 5: Add books to the library (Encapsulation & JDBC)
            System.out.println("--- Adding Books to Library ---");
            libraryService.addBook(new Book("978-0-13-468599-1", "Effective Java", "Joshua Bloch", 2018));
            libraryService.addBook(new Book("978-0-13-235088-4", "Clean Code", "Robert C. Martin", 2008));
            libraryService.addBook(new Book("978-0-13-597825-6", "Java Concurrency in Practice", "Brian Goetz", 2006));
            libraryService.addBook(new Book("978-0-13-468729-2", "Design Patterns", "Gang of Four", 1994));
            libraryService.addBook(new Book("978-0-13-657842-9", "Head First Java", "Kathy Sierra", 2022));
            libraryService.addBook(new Book("978-0-13-857432-1", "Spring in Action", "Craig Walls", 2022));
            System.out.println();

            // Task 2: Register members (Encapsulation)
            System.out.println("--- Registering Library Members ---");
            libraryService.registerMember(new Member("M001", "John Doe"));
            libraryService.registerMember(new Member("M002", "Jane Smith"));
            libraryService.registerMember(new Member("M003", "Alice Johnson"));
            System.out.println();

            // Task 3: Display available books (Collections)
            System.out.println("--- Available Books in Library ---");
            displayAvailableBooks(libraryService);
            System.out.println();

            // Task 3 & 5: Single-threaded borrowing (Collections validation & JDBC)
            System.out.println("--- Single-Threaded Borrowing Operations ---");
            libraryService.borrowBook("M001", "978-0-13-468599-1");
            libraryService.borrowBook("M001", "978-0-13-235088-4");
            libraryService.borrowBook("M002", "978-0-13-597825-6");
            System.out.println();

            // Display member's borrowed books
            System.out.println("--- Member M001 Borrowed Books ---");
            displayMemberBorrowedBooks(libraryService, "M001");
            System.out.println();

            // Task 3: Test borrowing limit (max 5 books)
            System.out.println("--- Testing Borrowing Limit (Max 5 Books) ---");
            libraryService.borrowBook("M001", "978-0-13-468729-2");
            libraryService.borrowBook("M001", "978-0-13-657842-9");
            libraryService.borrowBook("M001", "978-0-13-857432-1");
            // This should fail - member already has 5 books
            libraryService.borrowBook("M001", "978-0-13-597825-6");
            System.out.println();

            // Task 5: Return a book
            System.out.println("--- Returning Books ---");
            libraryService.returnBook("M001", "978-0-13-468599-1");
            System.out.println();

            // Display updated available books
            System.out.println("--- Updated Available Books ---");
            displayAvailableBooks(libraryService);
            System.out.println();

            // Task 4: Multi-threaded borrowing simulation using ExecutorService
            System.out.println("--- Multi-Threaded Borrowing Simulation ---");
            System.out.println("Simulating concurrent borrowing requests by multiple librarians...");
            simulateConcurrentBorrowing(libraryService);
            System.out.println();

            // Final status
            System.out.println("--- Final Available Books ---");
            displayAvailableBooks(libraryService);
            System.out.println();

            System.out.println("--- Final Member M001 Status ---");
            displayMemberBorrowedBooks(libraryService, "M001");
            System.out.println();

            System.out.println("=".repeat(80));
            System.out.println("SYSTEM DEMONSTRATION COMPLETED SUCCESSFULLY");
            System.out.println("=".repeat(80));

        } catch (SQLException e) {
            System.err.println("Database error occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Display all available books
     */
    private static void displayAvailableBooks(LibraryService libraryService) {
        List<Book> availableBooks = libraryService.getAvailableBooks();
        if (availableBooks.isEmpty()) {
            System.out.println("No books currently available.");
        } else {
            System.out.println("Total available books: " + availableBooks.size());
            for (Book book : availableBooks) {
                System.out.println("  - " + book);
            }
        }
    }

    /**
     * Display books borrowed by a specific member
     */
    private static void displayMemberBorrowedBooks(LibraryService libraryService, String memberId) {
        List<Book> borrowedBooks = libraryService.getMemberBorrowedBooks(memberId);
        if (borrowedBooks.isEmpty()) {
            System.out.println("Member " + memberId + " has no borrowed books.");
        } else {
            System.out.println("Member " + memberId + " has borrowed " + borrowedBooks.size() + " book(s):");
            for (Book book : borrowedBooks) {
                System.out.println("  - " + book);
            }
        }
    }

    /**
     * Task 4: Simulate concurrent borrowing using ExecutorService and multiple threads
     * Ensures thread safety with synchronized methods
     */
    private static void simulateConcurrentBorrowing(LibraryService libraryService) {
        // Create a thread pool with 3 librarian threads
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // Create multiple borrow tasks
        // Two librarians trying to borrow the same book - only one should succeed
        executorService.submit(new BorrowTask(libraryService, "M003", "978-0-13-468599-1", "Librarian-A"));
        executorService.submit(new BorrowTask(libraryService, "M002", "978-0-13-468599-1", "Librarian-B"));
        
        // Multiple librarians processing different requests
        executorService.submit(new BorrowTask(libraryService, "M003", "978-0-13-235088-4", "Librarian-C"));

        // Shutdown the executor and wait for tasks to complete
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            System.out.println("✓ All concurrent borrowing operations completed");
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            System.err.println("Thread pool interrupted: " + e.getMessage());
        }
    }
}
