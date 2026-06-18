-- Rwanda National Digital Library - Database Schema
-- Task 1: Database Schema Design

-- Create database
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Table 1: books
-- Stores information about books in the library
CREATE TABLE IF NOT EXISTS books (
    ISBN VARCHAR(20) PRIMARY KEY,
    Title VARCHAR(255) NOT NULL,
    Author VARCHAR(255) NOT NULL,
    PublicationYear INT NOT NULL,
    Available BOOLEAN DEFAULT TRUE
);

-- Table 2: members
-- Stores information about library members
CREATE TABLE IF NOT EXISTS members (
    MemberID VARCHAR(20) PRIMARY KEY,
    Name VARCHAR(255) NOT NULL
);

-- Table 3: borrowing_records
-- Stores all borrowing transactions (permanent records)
CREATE TABLE IF NOT EXISTS borrowing_records (
    RecordID INT PRIMARY KEY AUTO_INCREMENT,
    MemberID VARCHAR(20) NOT NULL,
    ISBN VARCHAR(20) NOT NULL,
    BorrowDate DATE NOT NULL,
    ReturnDate DATE,
    FOREIGN KEY (MemberID) REFERENCES members(MemberID),
    FOREIGN KEY (ISBN) REFERENCES books(ISBN)
);

-- Sample data for testing (optional)
INSERT INTO books (ISBN, Title, Author, PublicationYear, Available) VALUES
('978-0-13-468599-1', 'Effective Java', 'Joshua Bloch', 2018, TRUE),
('978-0-13-235088-4', 'Clean Code', 'Robert C. Martin', 2008, TRUE),
('978-0-13-597825-6', 'Java Concurrency in Practice', 'Brian Goetz', 2006, TRUE);

INSERT INTO members (MemberID, Name) VALUES
('M001', 'John Doe'),
('M002', 'Jane Smith');
