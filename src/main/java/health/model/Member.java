package health.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Member entity class - Task 2: Encapsulation
 * Encapsulates Member ID, Name, and a List of currently borrowed books
 */
public class Member {
    private String memberId;
    private String name;
    private List<Book> borrowedBooks;

    public Member(String memberId, String name) {
        this.memberId = memberId;
        this.name = name;
        this.borrowedBooks = new ArrayList<>();
    }

    // Getters and Setters (Encapsulation)
    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBorrowedBooks() {
        return new ArrayList<>(borrowedBooks); // Return copy for encapsulation
    }

    public void setBorrowedBooks(List<Book> borrowedBooks) {
        this.borrowedBooks = new ArrayList<>(borrowedBooks);
    }

    // Helper method to add borrowed book
    public void addBorrowedBook(Book book) {
        this.borrowedBooks.add(book);
    }

    // Helper method to remove borrowed book
    public void removeBorrowedBook(Book book) {
        this.borrowedBooks.removeIf(b -> b.getIsbn().equals(book.getIsbn()));
    }

    public int getBorrowedBooksCount() {
        return borrowedBooks.size();
    }

    @Override
    public String toString() {
        return "Member{" +
                "ID='" + memberId + '\'' +
                ", Name='" + name + '\'' +
                ", BorrowedBooks=" + borrowedBooks.size() +
                '}';
    }
}
