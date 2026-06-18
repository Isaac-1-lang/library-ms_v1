# Rwanda National Digital Library (RNDL) - Library Management System

## Overview
The Rwanda National Digital Library Management System is a computerized solution designed to modernize how books are managed and accessed by library members. The system allows librarians to register books and members, track borrowing activities, and ensure accurate record keeping in a relational database.

## System Rules and Constraints

### Borrowing Limits
- **Maximum Books Per Member**: 5 books
- Members cannot borrow more than 5 books at a time
- The system will prevent borrowing attempts that exceed this limit

### Book Availability
- Books are automatically marked as unavailable when borrowed
- Books are marked as available again when returned
- Concurrent borrowing requests are handled safely to prevent double-booking

### Transaction Recording
- All borrowing transactions are permanently stored in the database
- Each transaction includes: Record ID, Member ID, ISBN, Borrow Date, and Return Date
- Return dates are nullable until the book is actually returned

## Database Schema

### Tables Structure

#### books
- **ISBN** (VARCHAR, Primary Key) - Unique book identifier
- **Title** (VARCHAR) - Book title
- **Author** (VARCHAR) - Book author
- **PublicationYear** (INT) - Year of publication
- **Available** (BOOLEAN) - Book availability status

#### members
- **MemberID** (VARCHAR, Primary Key) - Unique member identifier
- **Name** (VARCHAR) - Member name

#### borrowing_records
- **RecordID** (INT, Primary Key, Auto Increment) - Unique transaction identifier
- **MemberID** (VARCHAR, Foreign Key) - References members table
- **ISBN** (VARCHAR, Foreign Key) - References books table
- **BorrowDate** (DATE) - Date when book was borrowed
- **ReturnDate** (DATE, Nullable) - Date when book was returned (NULL if not yet returned)

## Features

### Core Functionality
1. **Book Registration**: Add new books to the library system
2. **Member Registration**: Register new library members
3. **Book Borrowing**: Process book borrowing requests with validation
4. **Book Returning**: Handle book returns and update records
5. **Available Books Listing**: View all currently available books
6. **Member Borrowed Books**: View books currently borrowed by a member

### Technical Features
- **Object-Oriented Design**: Encapsulated classes (Book, Member, LibraryService)
- **Collections Framework**: Uses Java Collections for managing books and members
- **Multithreading Support**: Handles concurrent borrowing operations safely
- **Thread Safety**: Synchronized operations prevent double-booking
- **JDBC Integration**: Complete database persistence layer
- **Exception Handling**: Comprehensive error handling for all operations

## Technology Stack
- **Java 17**: Core programming language
- **JDBC**: Database connectivity
- **MySQL**: Relational database (or any JDBC-compatible database)
- **Maven**: Build and dependency management
- **ExecutorService**: Thread pool management for concurrent operations

## Setup Instructions

### Database Setup
1. Create a MySQL database named `library_db`
2. Run the schema creation script (automatically handled by the application on first run)
3. Update database connection details in `DatabaseConnection.java` if needed

### Configuration
Default database configuration:
- **URL**: jdbc:mysql://localhost:3306/library_db
- **Username**: root
- **Password**: password

Modify these in the `DatabaseConnection` class as needed.

### Running the Application
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="health.Main"
```

## Usage Examples

The system demonstrates:
- Adding books to the library
- Registering members
- Borrowing books (single-threaded and multi-threaded)
- Returning books
- Listing available books
- Viewing member borrowed books

## Thread Safety
The system uses synchronized methods to ensure thread safety when multiple librarians process borrowing requests simultaneously. This prevents race conditions and ensures data consistency.

## Error Handling
- SQLException handling for database operations
- Business logic validation (borrowing limits, book availability)
- Proper resource cleanup with try-with-resources
- Meaningful error messages for all failure scenarios

## Project Structure
```
src/main/java/health/
├── Main.java                    # Application entry point with demo scenarios
├── model/
│   ├── Book.java               # Book entity (encapsulated)
│   └── Member.java             # Member entity (encapsulated)
├── service/
│   └── LibraryService.java     # Core business logic and DAO operations
├── database/
│   └── DatabaseConnection.java # Database connection management
└── task/
    └── BorrowTask.java         # Runnable task for concurrent borrowing
```

## Author
Developed for the Rwanda National Digital Library (RNDL)
