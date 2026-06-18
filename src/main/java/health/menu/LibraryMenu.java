package health.menu;

import health.model.Book;
import health.model.Member;
import health.service.LibraryService;
import health.task.BorrowTask;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Interactive Menu System for Library Management
 * Provides manual operations and simulation options
 */
public class LibraryMenu {
    private final LibraryService libraryService;
    private final Scanner scanner;

    public LibraryMenu(LibraryService libraryService) {
        this.libraryService = libraryService;
        this.scanner = new Scanner(System.in);
    }

    public void displayMainMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("RWANDA NATIONAL DIGITAL LIBRARY - MANAGEMENT SYSTEM");
            System.out.println("=".repeat(80));
            System.out.println("1. Add Book");
            System.out.println("2. Register Member");
            System.out.println("3. Borrow Book");
            System.out.println("4. Return Book");
            System.out.println("5. View Available Books");
            System.out.println("6. View Member's Borrowed Books");
            System.out.println("7. Run Simulation (Demo Mode)");
            System.out.println("8. Run Concurrent Borrowing Simulation");
            System.out.println("0. Exit");
            System.out.println("=".repeat(80));
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addBook();
                case "2" -> registerMember();
                case "3" -> borrowBook();
                case "4" -> returnBook();
                case "5" -> viewAvailableBooks();
                case "6" -> viewMemberBorrowedBooks();
                case "7" -> runSimulation();
                case "8" -> runConcurrentSimulation();
                case "0" -> {
                    System.out.println("\n✓ Thank you for using RNDL Library Management System!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("\n✗ Invalid option! Please choose 0-8.");
            }
        }
    }

    private void addBook() {
        System.out.println("\n--- Add New Book ---");
        
        String isbn = getValidatedInput("Enter ISBN (e.g., 978-0-13-468599-1): ", 
            input -> !input.isEmpty(), "ISBN cannot be empty!");
        
        String title = getValidatedInput("Enter Title: ", 
            input -> !input.isEmpty(), "Title cannot be empty!");
        
        String author = getValidatedInput("Enter Author: ", 
            input -> !input.isEmpty(), "Author cannot be empty!");
        
        int year = getValidatedYear("Enter Publication Year (e.g., 2020): ");

        try {
            Book book = new Book(isbn, title, author, year);
            libraryService.addBook(book);
            System.out.println("\n✓ Book added successfully!");
        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate key") || e.getMessage().contains("unique constraint")) {
                System.out.println("\n✗ Error: A book with ISBN '" + isbn + "' already exists!");
            } else {
                System.out.println("\n✗ Error adding book: " + e.getMessage());
            }
        }
    }

    private void registerMember() {
        System.out.println("\n--- Register New Member ---");
        
        String memberId = getValidatedInput("Enter Member ID (e.g., M001): ", 
            input -> !input.isEmpty() && input.matches("M\\d{3}"), 
            "Member ID must be in format M### (e.g., M001)!");
        
        String name = getValidatedInput("Enter Member Name: ", 
            input -> !input.isEmpty(), "Name cannot be empty!");

        try {
            Member member = new Member(memberId, name);
            libraryService.registerMember(member);
            System.out.println("\n✓ Member registered successfully!");
        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate key") || e.getMessage().contains("unique constraint")) {
                System.out.println("\n✗ Error: Member ID '" + memberId + "' already exists!");
            } else {
                System.out.println("\n✗ Error registering member: " + e.getMessage());
            }
        }
    }

    private void borrowBook() {
        System.out.println("\n--- Borrow Book ---");
        
        String memberId = getValidatedInput("Enter Member ID: ", 
            input -> !input.isEmpty(), "Member ID cannot be empty!");
        
        String isbn = getValidatedInput("Enter Book ISBN: ", 
            input -> !input.isEmpty(), "ISBN cannot be empty!");

        boolean success = libraryService.borrowBook(memberId, isbn);
        
        if (success) {
            System.out.println("\n✓ Book borrowed successfully!");
        } else {
            System.out.println("\n✗ Failed to borrow book. Please check the error message above.");
        }
    }

    private void returnBook() {
        System.out.println("\n--- Return Book ---");
        
        String memberId = getValidatedInput("Enter Member ID: ", 
            input -> !input.isEmpty(), "Member ID cannot be empty!");
        
        String isbn = getValidatedInput("Enter Book ISBN: ", 
            input -> !input.isEmpty(), "ISBN cannot be empty!");

        boolean success = libraryService.returnBook(memberId, isbn);
        
        if (success) {
            System.out.println("\n✓ Book returned successfully!");
        } else {
            System.out.println("\n✗ Failed to return book. Please check the error message above.");
        }
    }

    private void viewAvailableBooks() {
        System.out.println("\n--- Available Books ---");
        List<Book> books = libraryService.getAvailableBooks();
        
        if (books.isEmpty()) {
            System.out.println("No books are currently available.");
        } else {
            System.out.println("Total available books: " + books.size());
            System.out.println("-".repeat(80));
            System.out.printf("%-20s %-35s %-25s %s%n", "ISBN", "Title", "Author", "Year");
            System.out.println("-".repeat(80));
            for (Book book : books) {
                System.out.printf("%-20s %-35s %-25s %d%n", 
                    book.getIsbn(), 
                    truncate(book.getTitle(), 35), 
                    truncate(book.getAuthor(), 25), 
                    book.getPublicationYear());
            }
            System.out.println("-".repeat(80));
        }
    }

    private void viewMemberBorrowedBooks() {
        System.out.println("\n--- View Member's Borrowed Books ---");
        
        String memberId = getValidatedInput("Enter Member ID: ", 
            input -> !input.isEmpty(), "Member ID cannot be empty!");

        List<Book> books = libraryService.getMemberBorrowedBooks(memberId);
        
        if (books.isEmpty()) {
            System.out.println("\nMember '" + memberId + "' has no borrowed books.");
        } else {
            System.out.println("\nMember '" + memberId + "' has borrowed " + books.size() + " book(s):");
            System.out.println("-".repeat(80));
            System.out.printf("%-20s %-35s %-25s %s%n", "ISBN", "Title", "Author", "Year");
            System.out.println("-".repeat(80));
            for (Book book : books) {
                System.out.printf("%-20s %-35s %-25s %d%n", 
                    book.getIsbn(), 
                    truncate(book.getTitle(), 35), 
                    truncate(book.getAuthor(), 25), 
                    book.getPublicationYear());
            }
            System.out.println("-".repeat(80));
        }
    }

    private void runSimulation() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("RUNNING SIMULATION MODE");
        System.out.println("=".repeat(80));
        System.out.print("\nThis will add sample books and members, and simulate borrowing/returning.\nContinue? (y/n): ");
        
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("y")) {
            System.out.println("Simulation cancelled.");
            return;
        }

        try {
            System.out.println("\n--- Adding Sample Books ---");
            libraryService.addBook(new Book("978-0-13-468599-1", "Effective Java", "Joshua Bloch", 2018));
            libraryService.addBook(new Book("978-0-13-235088-4", "Clean Code", "Robert C. Martin", 2008));
            libraryService.addBook(new Book("978-0-13-597825-6", "Java Concurrency in Practice", "Brian Goetz", 2006));
            libraryService.addBook(new Book("978-0-13-468729-2", "Design Patterns", "Gang of Four", 1994));
            libraryService.addBook(new Book("978-0-13-657842-9", "Head First Java", "Kathy Sierra", 2022));
            libraryService.addBook(new Book("978-0-13-857432-1", "Spring in Action", "Craig Walls", 2022));

            System.out.println("\n--- Registering Sample Members ---");
            libraryService.registerMember(new Member("M001", "John Doe"));
            libraryService.registerMember(new Member("M002", "Jane Smith"));
            libraryService.registerMember(new Member("M003", "Alice Johnson"));

            System.out.println("\n--- Simulating Borrowing Operations ---");
            libraryService.borrowBook("M001", "978-0-13-468599-1");
            libraryService.borrowBook("M001", "978-0-13-235088-4");
            libraryService.borrowBook("M002", "978-0-13-597825-6");

            System.out.println("\n--- Testing Borrowing Limit (Max 5 Books) ---");
            libraryService.borrowBook("M001", "978-0-13-468729-2");
            libraryService.borrowBook("M001", "978-0-13-657842-9");
            libraryService.borrowBook("M001", "978-0-13-857432-1");
            libraryService.borrowBook("M001", "978-0-13-597825-6"); // Should fail

            System.out.println("\n--- Simulating Book Return ---");
            libraryService.returnBook("M001", "978-0-13-468599-1");

            System.out.println("\n✓ Simulation completed successfully!");

        } catch (SQLException e) {
            System.out.println("\n⚠ Note: Some data may already exist. Continuing with existing data...");
        }
    }

    private void runConcurrentSimulation() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("RUNNING CONCURRENT BORROWING SIMULATION");
        System.out.println("=".repeat(80));
        System.out.print("\nThis will simulate multiple librarians processing borrowing requests simultaneously.\nContinue? (y/n): ");
        
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("y")) {
            System.out.println("Simulation cancelled.");
            return;
        }

        System.out.println("\nSimulating 3 librarians processing concurrent requests...");
        
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // Two librarians trying to borrow the same book - only one should succeed
        executorService.submit(new BorrowTask(libraryService, "M003", "978-0-13-468599-1", "Librarian-A"));
        executorService.submit(new BorrowTask(libraryService, "M002", "978-0-13-468599-1", "Librarian-B"));
        
        // Another librarian processing a different request
        executorService.submit(new BorrowTask(libraryService, "M003", "978-0-13-235088-4", "Librarian-C"));

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            System.out.println("\n✓ Concurrent simulation completed!");
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            System.err.println("Simulation interrupted: " + e.getMessage());
        }
    }

    // Helper methods for validation
    private String getValidatedInput(String prompt, InputValidator validator, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (validator.validate(input)) {
                return input;
            }
            System.out.println("✗ " + errorMessage);
        }
    }

    private int getValidatedYear(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            try {
                int year = Integer.parseInt(input);
                if (year >= 1000 && year <= 2100) {
                    return year;
                }
                System.out.println("✗ Year must be between 1000 and 2100!");
            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid year! Please enter a valid number.");
            }
        }
    }

    private String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    @FunctionalInterface
    interface InputValidator {
        boolean validate(String input);
    }
}
