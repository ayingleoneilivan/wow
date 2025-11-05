#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX 100
#define FILE_NAME "grades.txt"

typedef struct {
    char id[20];
    char name[50];
    char course[20];
    int grade;
} Student;

void addRecord();
void viewRecords();
void updateRecord();
void deleteRecord();
int checkDuplicateID(const char *id);
void clearInputBuffer();

int main() {
    int choice;

    while (1) {
        printf("\n===== Student Grade Management System =====\n");
        printf("1. Add Record\n");
        printf("2. View Records\n");
        printf("3. Update Record\n");
        printf("4. Delete Record\n");
        printf("5. Exit\n");
        printf("Choose an option: ");
        scanf("%d", &choice);
        clearInputBuffer();

        switch (choice) {
            case 1:
                addRecord();
                break;
            case 2:
                viewRecords();
                break;
            case 3:
                updateRecord();
                break;
            case 4:
                deleteRecord();
                break;
            case 5:
                printf("Exiting program...\n");
                return 0;
            default:
                printf("Invalid option. Please try again.\n");
        }
    }
}

// --- Helper functions ---

void clearInputBuffer() {
    while (getchar() != '\n');
}

int checkDuplicateID(const char *id) {
    FILE *fp = fopen(FILE_NAME, "r");
    if (fp == NULL) return 0; // file doesn't exist yet

    Student s;
    char line[200];
    while (fgets(line, sizeof(line), fp)) {
        sscanf(line, "%[^,],%[^,],%[^,],%d", s.id, s.name, s.course, &s.grade);
        if (strcmp(s.id, id) == 0) {
            fclose(fp);
            return 1;
        }
    }

    fclose(fp);
    return 0;
}

// --- Core CRUD Functions ---

void addRecord() {
    Student s;
    FILE *fp;

    printf("\nEnter Student ID: ");
    scanf("%s", s.id);
    clearInputBuffer();

    if (checkDuplicateID(s.id)) {
        printf("Error: Student ID already exists!\n");
        return;
    }

    printf("Enter Name: ");
    fgets(s.name, sizeof(s.name), stdin);
    s.name[strcspn(s.name, "\n")] = '\0';

    printf("Enter Course: ");
    fgets(s.course, sizeof(s.course), stdin);
    s.course[strcspn(s.course, "\n")] = '\0';

    printf("Enter Final Grade (0-100): ");
    scanf("%d", &s.grade);
    clearInputBuffer();

    if (s.grade < 0 || s.grade > 100) {
        printf("Invalid grade. Must be between 0 and 100.\n");
        return;
    }

    fp = fopen(FILE_NAME, "a");
    if (fp == NULL) {
        printf("Error: Could not open file.\n");
        return;
    }

    fprintf(fp, "%s,%s,%s,%d\n", s.id, s.name, s.course, s.grade);
    fclose(fp);

    printf("Record added successfully!\n");
}

void viewRecords() {
    FILE *fp = fopen(FILE_NAME, "r");
    if (fp == NULL) {
        printf("No records found.\n");
        return;
    }

    Student s;
    char line[200];
    int found = 0;

    printf("\n%-10s %-20s %-10s %-10s\n", "StudentID", "Name", "Course", "Final Grade");
    printf("------------------------------------------------------\n");

    while (fgets(line, sizeof(line), fp)) {
        sscanf(line, "%[^,],%[^,],%[^,],%d", s.id, s.name, s.course, &s.grade);
        printf("%-10s %-20s %-10s %-10d\n", s.id, s.name, s.course, s.grade);
        found = 1;
    }

    if (!found)
        printf("No student records found.\n");

    fclose(fp);
}

void updateRecord() {
    FILE *fp = fopen(FILE_NAME, "r");
    if (fp == NULL) {
        printf("No records found.\n");
        return;
    }

    Student students[MAX];
    int count = 0;
    char line[200];

    while (fgets(line, sizeof(line), fp)) {
        sscanf(line, "%[^,],%[^,],%[^,],%d", students[count].id, students[count].name, students[count].course, &students[count].grade);
        count++;
    }
    fclose(fp);

    char id[20];
    printf("Enter Student ID to update: ");
    scanf("%s", id);
    clearInputBuffer();

    int found = 0;
    for (int i = 0; i < count; i++) {
        if (strcmp(students[i].id, id) == 0) {
            printf("Enter new Final Grade (0–100): ");
            scanf("%d", &students[i].grade);
            clearInputBuffer();

            if (students[i].grade < 0 || students[i].grade > 100) {
                printf("Invalid grade. Must be between 0 and 100.\n");
                return;
            }

            found = 1;
            break;
        }
    }

    if (!found) {
        printf("Student ID not found.\n");
        return;
    }

    fp = fopen(FILE_NAME, "w");
    for (int i = 0; i < count; i++) {
        fprintf(fp, "%s,%s,%s,%d\n", students[i].id, students[i].name, students[i].course, students[i].grade);
    }
    fclose(fp);

    printf("Record updated successfully!\n");
}

void deleteRecord() {
    FILE *fp = fopen(FILE_NAME, "r");
    if (fp == NULL) {
        printf("No records found.\n");
        return;
    }

    Student students[MAX];
    int count = 0;
    char line[200];

    while (fgets(line, sizeof(line), fp)) {
        sscanf(line, "%[^,],%[^,],%[^,],%d", students[count].id, students[count].name, students[count].course, &students[count].grade);
        count++;
    }
    fclose(fp);

    char id[20];
    printf("Enter Student ID to delete: ");
    scanf("%s", id);
    clearInputBuffer();

    int found = 0;
    FILE *temp = fopen("temp.txt", "w");

    for (int i = 0; i < count; i++) {
        if (strcmp(students[i].id, id) != 0) {
            fprintf(temp, "%s,%s,%s,%d\n", students[i].id, students[i].name, students[i].course, students[i].grade);
        } else {
            found = 1;
        }
    }

    fclose(temp);

    remove(FILE_NAME);
    rename("temp.txt", FILE_NAME);

    if (found)
        printf("Record deleted successfully!\n");
    else
        printf("Student ID not found.\n");
}
