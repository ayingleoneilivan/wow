//build
javac -cp ".;sqlite-jdbc-3.51.0.0.jar" LibrarySystem.java
java -cp ".;sqlite-jdbc-3.51.0.0.jar" LibrarySystem.java

//check tables via cmd
sqlite3
.open "C:\Users\My Pc\Documents\Code\Code Blocks\ite19\java with database\library.db"
.tables
.schema TABLENAME

//optional

CREATE TABLE IF NOT EXISTS Books (
    BookID TEXT PRIMARY KEY,
    Title TEXT NOT NULL,
    Author TEXT NOT NULL,
    Year INTEGER,
    Status TEXT DEFAULT 'Available'
);

CREATE TABLE IF NOT EXISTS Transactions (
    TransID INTEGER PRIMARY KEY AUTOINCREMENT,
    BookID TEXT,
    BorrowerName TEXT,
    DateBorrowed TEXT,
    DateReturned TEXT,
    FOREIGN KEY(BookID) REFERENCES Books(BookID)
);
