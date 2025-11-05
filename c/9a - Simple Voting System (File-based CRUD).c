#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX 100
#define FILE_NAME "votes.txt"

typedef struct {
    char studentID[20];
    char candidate[50];
} VoteRecord;

const char *candidates[] = {"Alice Santos", "Bryan Cruz", "Carla Reyes"};
const int numCandidates = 3;

// Function declarations
void registerAndVote();
void adminMenu();
void viewAllVotes();
void updateVote();
void deleteVote();
void displayResults();
int checkDuplicateID(const char *studentID);
void clearInputBuffer();

int main() {
    int choice;
    while (1) {
        printf("\n==== Simple Voting System ====\n");
        printf("1. Register & Vote\n");
        printf("2. Admin Login\n");
        printf("3. Exit\n");
        printf("Enter choice: ");
        scanf("%d", &choice);
        clearInputBuffer();

        switch (choice) {
            case 1:
                registerAndVote();
                break;
            case 2:
                adminMenu();
                break;
            case 3:
                printf("Exiting program...\n");
                return 0;
            default:
                printf("Invalid choice! Try again.\n");
        }
    }
}

// --- Helper functions ---

void clearInputBuffer() {
    while (getchar() != '\n');
}

int checkDuplicateID(const char *studentID) {
    FILE *fp = fopen(FILE_NAME, "r");
    if (fp == NULL) return 0; // file doesn't exist yet

    VoteRecord record;
    while (fscanf(fp, "%[^|]|%[^\n]\n", record.studentID, record.candidate) == 2) {
        if (strcmp(record.studentID, studentID) == 0) {
            fclose(fp);
            return 1;
        }
    }
    fclose(fp);
    return 0;
}

// --- Core Functions ---

void registerAndVote() {
    VoteRecord record;
    printf("\nEnter Student ID: ");
    scanf("%s", record.studentID);
    clearInputBuffer();

    if (checkDuplicateID(record.studentID)) {
        printf("Error: You have already voted!\n");
        return;
    }

    printf("\nCandidates:\n");
    for (int i = 0; i < numCandidates; i++) {
        printf("%d. %s\n", i + 1, candidates[i]);
    }

    int choice;
    printf("Enter Candidate Number: ");
    scanf("%d", &choice);
    clearInputBuffer();

    if (choice < 1 || choice > numCandidates) {
        printf("Invalid candidate number.\n");
        return;
    }

    strcpy(record.candidate, candidates[choice - 1]);

    FILE *fp = fopen(FILE_NAME, "a");
    if (fp == NULL) {
        printf("Error opening file.\n");
        return;
    }

    fprintf(fp, "%s|%s\n", record.studentID, record.candidate);
    fclose(fp);

    printf("Vote successfully recorded for %s!\n", record.candidate);
}

// --- Admin Menu ---

void adminMenu() {
    int choice;
    while (1) {
        printf("\n==== Admin Menu ====\n");
        printf("1. View All Votes\n");
        printf("2. Update Vote\n");
        printf("3. Delete Voter Record\n");
        printf("4. Display Election Results\n");
        printf("5. Exit\n");
        printf("Enter choice: ");
        scanf("%d", &choice);
        clearInputBuffer();

        switch (choice) {
            case 1:
                viewAllVotes();
                break;
            case 2:
                updateVote();
                break;
            case 3:
                deleteVote();
                break;
            case 4:
                displayResults();
                break;
            case 5:
                return;
            default:
                printf("Invalid choice!\n");
        }
    }
}

// --- CRUD Functions ---

void viewAllVotes() {
    FILE *fp = fopen(FILE_NAME, "r");
    if (fp == NULL) {
        printf("No votes recorded yet.\n");
        return;
    }

    VoteRecord record;
    printf("\n%-15s %-20s\n", "Student ID", "Candidate");
    printf("--------------------------------------\n");
    while (fscanf(fp, "%[^|]|%[^\n]\n", record.studentID, record.candidate) == 2) {
        printf("%-15s %-20s\n", record.studentID, record.candidate);
    }

    fclose(fp);
}

void updateVote() {
    FILE *fp = fopen(FILE_NAME, "r");
    if (fp == NULL) {
        printf("No votes recorded yet.\n");
        return;
    }

    VoteRecord records[MAX];
    int count = 0;
    while (fscanf(fp, "%[^|]|%[^\n]\n", records[count].studentID, records[count].candidate) == 2) {
        count++;
    }
    fclose(fp);

    char id[20];
    printf("Enter Student ID to update: ");
    scanf("%s", id);
    clearInputBuffer();

    int found = 0;
    for (int i = 0; i < count; i++) {
        if (strcmp(records[i].studentID, id) == 0) {
            printf("\nCandidates:\n");
            for (int j = 0; j < numCandidates; j++) {
                printf("%d. %s\n", j + 1, candidates[j]);
            }

            int choice;
            printf("Enter new Candidate Number: ");
            scanf("%d", &choice);
            clearInputBuffer();

            if (choice < 1 || choice > numCandidates) {
                printf("Invalid candidate number.\n");
                return;
            }

            strcpy(records[i].candidate, candidates[choice - 1]);
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
        fprintf(fp, "%s|%s\n", records[i].studentID, records[i].candidate);
    }
    fclose(fp);

    printf("Vote updated successfully!\n");
}

void deleteVote() {
    FILE *fp = fopen(FILE_NAME, "r");
    if (fp == NULL) {
        printf("No votes recorded yet.\n");
        return;
    }

    VoteRecord records[MAX];
    int count = 0;
    while (fscanf(fp, "%[^|]|%[^\n]\n", records[count].studentID, records[count].candidate) == 2) {
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
        if (strcmp(records[i].studentID, id) != 0) {
            fprintf(temp, "%s|%s\n", records[i].studentID, records[i].candidate);
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

void displayResults() {
    FILE *fp = fopen(FILE_NAME, "r");
    if (fp == NULL) {
        printf("No votes recorded yet.\n");
        return;
    }

    int votes[numCandidates];
    for (int i = 0; i < numCandidates; i++) votes[i] = 0;

    VoteRecord record;
    while (fscanf(fp, "%[^|]|%[^\n]\n", record.studentID, record.candidate) == 2) {
        for (int i = 0; i < numCandidates; i++) {
            if (strcmp(record.candidate, candidates[i]) == 0)
                votes[i]++;
        }
    }
    fclose(fp);

    printf("\nElection Results:\n");
    for (int i = 0; i < numCandidates; i++) {
        printf("%s : %d votes\n", candidates[i], votes[i]);
    }

    // Find winner
    int maxVotes = 0;
    for (int i = 0; i < numCandidates; i++) {
        if (votes[i] > maxVotes)
            maxVotes = votes[i];
    }

    printf("-----------------------------\nWinner(s): ");
    for (int i = 0; i < numCandidates; i++) {
        if (votes[i] == maxVotes)
            printf("%s ", candidates[i]);
    }
    printf("\n");
}
