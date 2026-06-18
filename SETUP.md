# Setup Guide - Rwanda National Digital Library Management System

## Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Database Setup

### Step 1: Install MySQL
If you don't have MySQL installed, download and install it from:
https://dev.mysql.com/downloads/mysql/

### Step 2: Create Database
Open MySQL command line or MySQL Workbench and run:

```sql
CREATE DATABASE library_db;
```

### Step 3: Configure Database Connection
Edit `src/main/java/health/database/DatabaseConnection.java` and update the following variables if needed:

```java
private static final String URL = "jdbc:mysql://localhost:3306/library_db";
private static final String USER = "root";  // Your MySQL username
private static final String PASSWORD = "password";  // Your MySQL password
```

## Running the Application

### Option 1: Using Maven

1. **Install dependencies:**
   ```bash
   mvn clean install
   ```

2. **Run the application:**
   ```bash
   mvn exec:java -Dexec.mainClass="health.Main"
   ```

### Option 2: Using IDE (IntelliJ IDEA / Eclipse)

1. Import the project as a Maven project
2. Wait for dependencies to download
3. Right-click on `Main.java` and select "Run"

## Troubleshooting

### MySQL Connection Error
If you get `Communications link failure`, ensure:
- MySQL server is running
- Port 3306 is not blocked
- Username and password are correct

### Driver Not Found Error
If you get `No suitable driver found`, ensure:
- MySQL connector dependency is in `pom.xml`
- Run `mvn clean install` to download dependencies

### Foreign Key Constraint Error
The application creates tables automatically. If you're manually creating tables, ensure you create them in this order:
1. books
2. members
3. borrowing_records

## Testing the System

The application demonstrates:
1. **Database initialization** - Creates all tables
2. **Book registration** - Adds 6 books
3. **Member registration** - Registers 3 members
4. **Single-threaded borrowing** - Normal borrowing operations
5. **Borrowing limit validation** - Tests 5-book limit
6. **Book return** - Returns borrowed books
7. **Concurrent borrowing** - Simulates multiple librarians using threads

## Database Queries for Verification

After running the application, you can verify the data in MySQL:

```sql
-- View all books
SELECT * FROM books;

-- View all members
SELECT * FROM members;

-- View all borrowing records
SELECT * FROM borrowing_records;

-- View currently borrowed books (not yet returned)
SELECT br.RecordID, m.Name, b.Title, br.BorrowDate
FROM borrowing_records br
JOIN members m ON br.MemberID = m.MemberID
JOIN books b ON br.ISBN = b.ISBN
WHERE br.ReturnDate IS NULL;

-- View member's borrowing history
SELECT m.Name, b.Title, br.BorrowDate, br.ReturnDate
FROM borrowing_records br
JOIN members m ON br.MemberID = m.MemberID
JOIN books b ON br.ISBN = b.ISBN
WHERE m.MemberID = 'M001';
```

## Project Structure

```
library-ms/
├── src/
│   └── main/
│       └── java/
│           └── health/
│               ├── Main.java                    # Application entry point
│               ├── database/
│               │   └── DatabaseConnection.java  # DB connection management
│               ├── model/
│               │   ├── Book.java               # Book entity
│               │   └── Member.java             # Member entity
│               ├── service/
│               │   └── LibraryService.java     # Business logic & DAO
│               └── task/
│                   └── BorrowTask.java         # Multithreading task
├── pom.xml                                     # Maven configuration
├── README.md                                   # Project documentation
├── SETUP.md                                    # This file
└── database_schema.sql                         # SQL schema reference
```

## Additional Notes

- The system uses **synchronized methods** to ensure thread safety during concurrent operations
- All borrowing transactions are **permanently stored** in the database
- Books are automatically marked as available/unavailable during borrow/return operations
- The **5-book limit** is enforced at the service layer before database operations
