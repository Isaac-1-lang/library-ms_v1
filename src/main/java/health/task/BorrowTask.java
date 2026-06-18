package health.task;

import health.service.LibraryService;

/**
 * Task 4: BorrowTask implements Runnable
 * Simulates concurrent borrowing operations by multiple librarians
 */
public class BorrowTask implements Runnable {
    private final LibraryService libraryService;
    private final String memberId;
    private final String isbn;
    private final String librarianName;

    public BorrowTask(LibraryService libraryService, String memberId, String isbn, String librarianName) {
        this.libraryService = libraryService;
        this.memberId = memberId;
        this.isbn = isbn;
        this.librarianName = librarianName;
    }

    @Override
    public void run() {
        System.out.println("[" + librarianName + "] Processing borrow request: Member=" + memberId + ", ISBN=" + isbn);
        
        // The synchronized method in LibraryService ensures thread safety
        boolean success = libraryService.borrowBook(memberId, isbn);
        
        if (success) {
            System.out.println("[" + librarianName + "] ✓ Successfully processed borrow request");
        } else {
            System.out.println("[" + librarianName + "] ✗ Failed to process borrow request");
        }
    }
}
