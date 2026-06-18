# Setup Guide  Rwanda National Digital Library Management System

## Prerequisites
- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher

## Database Setup

### Step 1: Install PostgreSQL
If you don't have PostgreSQL installed, download and install it from:
https://www.postgresql.org/download/

### Step 2: Create Database
Open PostgreSQL command line (psql) or pgAdmin and run:

```sql
CREATE DATABASE library_ms;
```

Or use the command line:
```bash
psql -U postgres
CREATE DATABASE library_ms;
\q
```

### Step 3: Database Configuration (Already Set)
The database connection is configured with:
- **URL**: jdbc:postgresql://localhost:5432/library_ms
- **Username**: postgres
- **Password**: 121402pr0732021
- **Database**: library_ms

If you need to change these, edit `src/main/java/health/database/DatabaseConnection.java`

## Running the Application

### Option 1: Using Maven

1. **Install dependencies:**
   ```bash
   mvn clean install
   ```

2. **Run the application:**
   ```bash
   mvn exec:java
   ```

   Or with the full command:
   ```bash
   mvn exec:java -Dexec.mainClass=health.Main
   ```

### Option 2: Using IDE (IntelliJ IDEA / Eclipse)

1. Import the project as a Maven project
2. Wait for dependencies to download
3. Right-click on `Main.java` and select "Run"

## Troubleshooting

### PostgreSQL Connection Error
If you get `Connection refused`, ensure:
- PostgreSQL server is running
- Port 5432 is not blocked
- Username and password are correct
- Database `library_ms` exists

Check PostgreSQL status:
```bash
# Windows
pg_ctl status

# Or check in Services
services.msc (look for postgresql service)
```

### Driver Not Found Error
If you get `No suitable driver found`, ensure:
- PostgreSQL JDBC driver dependency is in `pom.xml`
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

After running the application, you can verify the data in PostgreSQL:

Connect to database:
```bash
psql -U postgres -d library_ms
```

Then run queries:
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
