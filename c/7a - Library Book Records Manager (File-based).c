#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <time.h>

#define FILE_NAME "books.txt"
#define TEMP_FILE "temp.txt"
#define MAX_LINE 512

typedef struct {
    char bookID[20];
    char title[100];
    char author[100];
    int year;
    char status[20];
} Book;

void addBook();
void viewBooks();
void updateBook();
void deleteBook();
void searchBook();
int isUniqueBookID(const char *bookID);
int getCurrentYear();
void trimNewline(char *str);

int main() {
    int choice;

    while (1) {
        printf("\n=== Library Book Records Manager ===\n");
        printf("1. Add Book Record\n");
        printf("2. View All Books\n");
        printf("3. Update Book Record\n");
        printf("4. Delete Book Record\n");
        printf("5. Search Book\n");
        printf("6. Exit\n");
        printf("Enter your choice: ");
        scanf("%d", &choice);
        getchar();

        switch (choice) {
            case 1: addBook(); break;
            case 2: viewBooks(); break;
            case 3: updateBook(); break;
            case 4: deleteBook(); break;
            case 5: searchBook(); break;
            case 6:
                printf("Exiting program...\n");
                return 0;
            default:
                printf("Invalid choice! Please enter 1–6.\n");
        }
    }
}

void trimNewline(char *str) {
    size_t len = strlen(str);
    if (len > 0 && str[len-1] == '\n')
        str[len-1] = '\0';
}

int getCurrentYear() {
    time_t t = time(NULL);
    struct tm tm = *localtime(&t);
    return tm.tm_year + 1900;
}

int isUniqueBookID(const char *bookID) {
    FILE *fp = fopen(FILE_NAME, "r");
    if (!fp) return 1;
    char line[MAX_LINE];
    while (fgets(line, sizeof(line), fp)) {
        char id[20];
        sscanf(line, "%[^|]", id);
        if (strcmp(id, bookID) == 0) {
            fclose(fp);
            return 0;
        }
    }
    fclose(fp);
    return 1;
}

void addBook() {
    Book b;
    printf("\nEnter BookID: ");
    fgets(b.bookID, sizeof(b.bookID), stdin);
    trimNewline(b.bookID);

    if (!isUniqueBookID(b.bookID)) {
        printf("Error: BookID already exists!\n");
        return;
    }

    printf("Enter Title: ");
    fgets(b.title, sizeof(b.title), stdin);
    trimNewline(b.title);

    printf("Enter Author: ");
    fgets(b.author, sizeof(b.author), stdin);
    trimNewline(b.author);

    printf("Enter Year: ");
    scanf("%d", &b.year);
    getchar();

    int currentYear = getCurrentYear();
    if (b.year < 1500 || b.year > currentYear) {
        printf("Error: Invalid year. Must be between 1500 and %d.\n", currentYear);
        return;
    }

    printf("Enter Status (Available/Borrowed): ");
    fgets(b.status, sizeof(b.status), stdin);
    trimNewline(b.status);

    if (strcmp(b.status, "Available") != 0 && strcmp(b.status, "Borrowed") != 0) {
        printf("Error: Status must be 'Available' or 'Borrowed'.\n");
        return;
    }

    FILE *fp = fopen(FILE_NAME, "a");
    if (!fp) {
        printf("Error: Cannot open %s\n", FILE_NAME);
        return;
    }
    fprintf(fp, "%s|%s|%s|%d|%s\n", b.bookID, b.title, b.author, b.year, b.status);
    fclose(fp);

    printf("Book added successfully!\n");
}

void viewBooks() {
    FILE *fp = fopen(FILE_NAME, "r");
    if (!fp) {
        printf("No records found.\n");
        return;
    }

    char line[MAX_LINE];
    int count = 0;

    printf("\n%-10s %-30s %-20s %-6s %-10s\n", "BookID", "Title", "Author", "Year", "Status");
    printf("--------------------------------------------------------------------------------------\n");

    while (fgets(line, sizeof(line), fp)) {
        Book b;
        sscanf(line, "%[^|]|%[^|]|%[^|]|%d|%[^|\n]", b.bookID, b.title, b.author, &b.year, b.status);
        printf("%-10s %-30s %-20s %-6d %-10s\n", b.bookID, b.title, b.author, b.year, b.status);
        count++;
    }

    if (count == 0)
        printf("No books found.\n");

    fclose(fp);
}

void updateBook() {
    char searchID[20];
    printf("\nEnter BookID to update: ");
    fgets(searchID, sizeof(searchID), stdin);
    trimNewline(searchID);

    FILE *fp = fopen(FILE_NAME, "r");
    FILE *temp = fopen(TEMP_FILE, "w");

    if (!fp || !temp) {
        printf("Error: Cannot open file.\n");
        return;
    }

    char line[MAX_LINE];
    int found = 0;

    while (fgets(line, sizeof(line), fp)) {
        Book b;
        sscanf(line, "%[^|]|%[^|]|%[^|]|%d|%[^|\n]", b.bookID, b.title, b.author, &b.year, b.status);

        if (strcmp(b.bookID, searchID) == 0) {
            found = 1;
            printf("Book found. Enter new details (leave blank to keep current):\n");

            char input[100];
            printf("New Title [%s]: ", b.title);
            fgets(input, sizeof(input), stdin);
            trimNewline(input);
            if (strlen(input) > 0) strcpy(b.title, input);

            printf("New Author [%s]: ", b.author);
            fgets(input, sizeof(input), stdin);
            trimNewline(input);
            if (strlen(input) > 0) strcpy(b.author, input);

            printf("New Year [%d]: ", b.year);
            fgets(input, sizeof(input), stdin);
            trimNewline(input);
            if (strlen(input) > 0) b.year = atoi(input);

            printf("New Status [%s]: ", b.status);
            fgets(input, sizeof(input), stdin);
            trimNewline(input);
            if (strlen(input) > 0) strcpy(b.status, input);

            fprintf(temp, "%s|%s|%s|%d|%s\n", b.bookID, b.title, b.author, b.year, b.status);
            printf("Book updated successfully!\n");
        } else {
            fputs(line, temp);
        }
    }

    fclose(fp);
    fclose(temp);

    remove(FILE_NAME);
    rename(TEMP_FILE, FILE_NAME);

    if (!found)
        printf("BookID not found!\n");
}

void deleteBook() {
    char searchID[20];
    printf("\nEnter BookID to delete: ");
    fgets(searchID, sizeof(searchID), stdin);
    trimNewline(searchID);

    FILE *fp = fopen(FILE_NAME, "r");
    FILE *temp = fopen(TEMP_FILE, "w");
    if (!fp || !temp) {
        printf("Error: Cannot open file.\n");
        return;
    }

    char line[MAX_LINE];
    int found = 0;

    while (fgets(line, sizeof(line), fp)) {
        Book b;
        sscanf(line, "%[^|]|%[^|]|%[^|]|%d|%[^|\n]", b.bookID, b.title, b.author, &b.year, b.status);
        if (strcmp(b.bookID, searchID) == 0) {
            found = 1;
            printf("Book deleted successfully!\n");
            continue;
        }
        fputs(line, temp);
    }

    fclose(fp);
    fclose(temp);

    remove(FILE_NAME);
    rename(TEMP_FILE, FILE_NAME);

    if (!found)
        printf("BookID not found!\n");
}

void searchBook() {
    char keyword[100];
    printf("\nEnter Title or Author to search: ");
    fgets(keyword, sizeof(keyword), stdin);
    trimNewline(keyword);

    FILE *fp = fopen(FILE_NAME, "r");
    if (!fp) {
        printf("No records found.\n");
        return;
    }

    char line[MAX_LINE];
    int found = 0;

    printf("\nResults:\n");
    printf("--------------------------------------------------------------------------------------\n");

    while (fgets(line, sizeof(line), fp)) {
        Book b;
        sscanf(line, "%[^|]|%[^|]|%[^|]|%d|%[^|\n]", b.bookID, b.title, b.author, &b.year, b.status);

        if (strstr(b.title, keyword) || strstr(b.author, keyword)) {
            printf("%s | %s | %s | %d | %s\n", b.bookID, b.title, b.author, b.year, b.status);
            found = 1;
        }
    }

    if (!found)
        printf("No matching books found.\n");

    fclose(fp);
}
