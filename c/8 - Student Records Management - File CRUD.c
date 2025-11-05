#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define FILE_NAME "students.txt"
#define MAX 200

// Structure for student record
typedef struct {
    char studentID[20];
    char name[50];
    char course[30];
    int yearLevel;
    float gpa;
} Student;

// Function prototypes
void addStudent();
void viewStudents();
void updateStudent();
void deleteStudent();
void searchStudent();
int studentExists(const char *studentID);
void saveStudents(Student students[], int count);

int main() {
    int choice;

    do {
        printf("\n==== STUDENT RECORD MANAGEMENT SYSTEM ====\n");
        printf("1. Add Student Record\n");
        printf("2. View All Records\n");
        printf("3. Update Student Record\n");
        printf("4. Delete Student Record\n");
        printf("5. Search Student (by Name or Course)\n");
        printf("6. Exit\n");
        printf("Enter choice: ");
        scanf("%d", &choice);
        getchar(); // consume newline

        switch (choice) {
            case 1: addStudent(); break;
            case 2: viewStudents(); break;
            case 3: updateStudent(); break;
            case 4: deleteStudent(); break;
            case 5: searchStudent(); break;
            case 6: printf("Exiting program...\n"); break;
            default: printf("Invalid choice! Try again.\n");
        }
    } while (choice != 6);

    return 0;
}

// Function to check if student already exists
int studentExists(const char *studentID) {
    FILE *file = fopen(FILE_NAME, "r");
    if (!file) return 0;

    Student s;
    while (fscanf(file, "%[^|]|%[^|]|%[^|]|%d|%f\n",
                  s.studentID, s.name, s.course, &s.yearLevel, &s.gpa) == 5) {
        if (strcmp(s.studentID, studentID) == 0) {
            fclose(file);
            return 1;
        }
    }

    fclose(file);
    return 0;
}

// Add new student
void addStudent() {
    FILE *file = fopen(FILE_NAME, "a+");
    if (!file) {
        printf("Error opening file!\n");
        return;
    }

    Student s;
    printf("Enter StudentID: ");
    fgets(s.studentID, sizeof(s.studentID), stdin);
    s.studentID[strcspn(s.studentID, "\n")] = 0;

    if (studentExists(s.studentID)) {
        printf("Error: StudentID already exists!\n");
        fclose(file);
        return;
    }

    printf("Enter Name: ");
    fgets(s.name, sizeof(s.name), stdin);
    s.name[strcspn(s.name, "\n")] = 0;

    printf("Enter Course: ");
    fgets(s.course, sizeof(s.course), stdin);
    s.course[strcspn(s.course, "\n")] = 0;

    printf("Enter Year Level: ");
    scanf("%d", &s.yearLevel);

    printf("Enter GPA: ");
    scanf("%f", &s.gpa);
    getchar(); // consume newline

    if (s.gpa < 0.00 || s.gpa > 5.00) {
        printf("Invalid GPA! Must be between 0.00 and 5.00.\n");
        fclose(file);
        return;
    }

    fprintf(file, "%s|%s|%s|%d|%.2f\n",
            s.studentID, s.name, s.course, s.yearLevel, s.gpa);

    fclose(file);
    printf("Record added successfully!\n");
}

// View all student records
void viewStudents() {
    FILE *file = fopen(FILE_NAME, "r");
    if (!file) {
        printf("No records found.\n");
        return;
    }

    Student s;
    int count = 0;
    printf("\n%-10s %-20s %-10s %-6s %-5s\n", "StudentID", "Name", "Course", "Year", "GPA");
    printf("--------------------------------------------------------------\n");

    while (fscanf(file, "%[^|]|%[^|]|%[^|]|%d|%f\n",
                  s.studentID, s.name, s.course, &s.yearLevel, &s.gpa) == 5) {
        printf("%-10s %-20s %-10s %-6d %-5.2f\n",
               s.studentID, s.name, s.course, s.yearLevel, s.gpa);
        count++;
    }

    if (count == 0)
        printf("No records found.\n");

    fclose(file);
}

// Update student record
void updateStudent() {
    FILE *file = fopen(FILE_NAME, "r");
    if (!file) {
        printf("No records found.\n");
        return;
    }

    Student students[MAX];
    int count = 0;
    while (fscanf(file, "%[^|]|%[^|]|%[^|]|%d|%f\n",
                  students[count].studentID, students[count].name,
                  students[count].course, &students[count].yearLevel,
                  &students[count].gpa) == 5) {
        count++;
    }
    fclose(file);

    char id[20];
    printf("Enter StudentID to update: ");
    fgets(id, sizeof(id), stdin);
    id[strcspn(id, "\n")] = 0;

    int found = 0;
    for (int i = 0; i < count; i++) {
        if (strcmp(students[i].studentID, id) == 0) {
            found = 1;
            printf("Enter new Name (leave blank to keep): ");
            char name[50];
            fgets(name, sizeof(name), stdin);
            name[strcspn(name, "\n")] = 0;
            if (strlen(name) > 0) strcpy(students[i].name, name);

            printf("Enter new Course (leave blank to keep): ");
            char course[30];
            fgets(course, sizeof(course), stdin);
            course[strcspn(course, "\n")] = 0;
            if (strlen(course) > 0) strcpy(students[i].course, course);

            printf("Enter new Year Level (0 to keep): ");
            int year;
            scanf("%d", &year);
            getchar();
            if (year != 0) students[i].yearLevel = year;

            printf("Enter new GPA (-1 to keep): ");
            float gpa;
            scanf("%f", &gpa);
            getchar();
            if (gpa >= 0 && gpa <= 5.00) students[i].gpa = gpa;

            printf("Record updated successfully!\n");
            break;
        }
    }

    if (!found)
        printf("StudentID not found.\n");
    else
        saveStudents(students, count);
}

// Delete a student record
void deleteStudent() {
    FILE *file = fopen(FILE_NAME, "r");
    if (!file) {
        printf("No records found.\n");
        return;
    }

    Student students[MAX];
    int count = 0;
    while (fscanf(file, "%[^|]|%[^|]|%[^|]|%d|%f\n",
                  students[count].studentID, students[count].name,
                  students[count].course, &students[count].yearLevel,
                  &students[count].gpa) == 5) {
        count++;
    }
    fclose(file);

    char id[20];
    printf("Enter StudentID to delete: ");
    fgets(id, sizeof(id), stdin);
    id[strcspn(id, "\n")] = 0;

    int found = 0, j = 0;
    Student updated[MAX];
    for (int i = 0; i < count; i++) {
        if (strcmp(students[i].studentID, id) == 0) {
            found = 1;
            continue;
        }
        updated[j++] = students[i];
    }

    if (!found)
        printf("StudentID not found.\n");
    else {
        saveStudents(updated, j);
        printf("Record deleted successfully!\n");
    }
}

// Save all records back to file
void saveStudents(Student students[], int count) {
    FILE *file = fopen(FILE_NAME, "w");
    if (!file) {
        printf("Error writing to file!\n");
        return;
    }

    for (int i = 0; i < count; i++) {
        fprintf(file, "%s|%s|%s|%d|%.2f\n",
                students[i].studentID, students[i].name, students[i].course,
                students[i].yearLevel, students[i].gpa);
    }

    fclose(file);
}

// Search student by name or course
void searchStudent() {
    FILE *file = fopen(FILE_NAME, "r");
    if (!file) {
        printf("No records found.\n");
        return;
    }

    char keyword[50];
    printf("Enter Name or Course to search: ");
    fgets(keyword, sizeof(keyword), stdin);
    keyword[strcspn(keyword, "\n")] = 0;

    Student s;
    int found = 0;
    printf("\nResults:\n");
    printf("%-10s %-20s %-10s %-6s %-5s\n", "StudentID", "Name", "Course", "Year", "GPA");
    printf("--------------------------------------------------------------\n");

    while (fscanf(file, "%[^|]|%[^|]|%[^|]|%d|%f\n",
                  s.studentID, s.name, s.course, &s.yearLevel, &s.gpa) == 5) {
        if (strstr(s.name, keyword) || strstr(s.course, keyword)) {
            printf("%-10s %-20s %-10s %-6d %-5.2f\n",
                   s.studentID, s.name, s.course, s.yearLevel, s.gpa);
            found = 1;
        }
    }

    if (!found)
        printf("No matching records found.\n");

    fclose(file);
}
