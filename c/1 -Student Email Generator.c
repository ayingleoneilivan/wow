#include <stdio.h>
#include <string.h>
#include <ctype.h>

#define MAX_STUDENTS 100
#define MAX_NAME_LEN 100
#define MAX_EMAIL_LEN 150

typedef struct {
    char name[MAX_NAME_LEN];
    char id[20];
    char email[MAX_EMAIL_LEN];
} Student;

Student students[MAX_STUDENTS];
int studentCount = 0;

// Function prototypes
int validateName(const char *name);
int validateID(const char *id);
void toLowercase(char *str);
void replaceSpacesWithDot(char *str);
void generateEmail(const char *name, const char *id, char *email);
void addStudent();
void searchStudentByID();
int emailExists(const char *email);
void menu();

// Validate name (letters and spaces only)
int validateName(const char *name) {
    for (int i = 0; name[i]; i++) {
        if (!isalpha(name[i]) && name[i] != ' ')
            return 0;
    }
    return 1;
}

// Validate ID (numeric only)
int validateID(const char *id) {
    for (int i = 0; id[i]; i++) {
        if (!isdigit(id[i]))
            return 0;
    }
    return 1;
}

// Convert string to lowercase
void toLowercase(char *str) {
    for (int i = 0; str[i]; i++) {
        str[i] = tolower(str[i]);
    }
}

// Replace spaces with dots
void replaceSpacesWithDot(char *str) {
    for (int i = 0; str[i]; i++) {
        if (str[i] == ' ')
            str[i] = '.';
    }
}

// Check if email already exists (for duplicate handling)
int emailExists(const char *email) {
    for (int i = 0; i < studentCount; i++) {
        if (strcmp(students[i].email, email) == 0)
            return 1;
    }
    return 0;
}

// Generate email
void generateEmail(const char *name, const char *id, char *email) {
    char tempName[MAX_NAME_LEN];
    strcpy(tempName, name);
    toLowercase(tempName);
    replaceSpacesWithDot(tempName);

    // Get last 2 digits of ID
    int len = strlen(id);
    char last2[3];
    if (len >= 2)
        strcpy(last2, &id[len - 2]);
    else
        strcpy(last2, id);

    // Build base email
    sprintf(email, "%s%s@university.edu", tempName, last2);

    // Handle duplicates
    char tempEmail[MAX_EMAIL_LEN];
    int duplicateCount = 1;
    while (emailExists(email)) {
        sprintf(tempEmail, "%s_%d@university.edu", tempName, duplicateCount++);
        strcpy(email, tempEmail);
    }
}

// Add new student
void addStudent() {
    if (studentCount >= MAX_STUDENTS) {
        printf("Error: Student list full.\n");
        return;
    }

    char name[MAX_NAME_LEN], id[20];
    printf("Enter full name: ");
    fgets(name, sizeof(name), stdin);
    name[strcspn(name, "\n")] = '\0';

    printf("Enter Student ID: ");
    fgets(id, sizeof(id), stdin);
    id[strcspn(id, "\n")] = '\0';

    // Validate inputs
    if (!validateName(name) || !validateID(id)) {
        printf("Error: Invalid name or ID.\n");
        return;
    }

    // Generate email
    char email[MAX_EMAIL_LEN];
    generateEmail(name, id, email);

    // Store in list
    strcpy(students[studentCount].name, name);
    strcpy(students[studentCount].id, id);
    strcpy(students[studentCount].email, email);
    studentCount++;

    printf("Generated Email: %s\n", email);
}

// Search by student ID
void searchStudentByID() {
    char id[20];
    printf("Enter Student ID to search: ");
    fgets(id, sizeof(id), stdin);
    id[strcspn(id, "\n")] = '\0';

    for (int i = 0; i < studentCount; i++) {
        if (strcmp(students[i].id, id) == 0) {
            printf("Result: %s\n", students[i].email);
            return;
        }
    }
    printf("No student found with ID %s.\n", id);
}

// Menu
void menu() {
    int choice;
    while (1) {
        printf("\n=== Student Email Generator ===\n");
        printf("1. Add Student\n");
        printf("2. Search Student by ID\n");
        printf("3. Exit\n");
        printf("Enter choice: ");
        scanf("%d", &choice);
        getchar(); // consume newline

        switch (choice) {
            case 1:
                addStudent();
                break;
            case 2:
                searchStudentByID();
                break;
            case 3:
                printf("Exiting program...\n");
                return;
            default:
                printf("Invalid choice. Try again.\n");
        }
    }
}

int main() {
    menu();
    return 0;
}
