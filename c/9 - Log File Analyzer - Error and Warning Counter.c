#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>

#define LOG_FILE "system.log"
#define REPORT_FILE "report.txt"
#define LINE_SIZE 512

// Function to convert a string to uppercase (for case-insensitive comparison)
void to_uppercase(char *str) {
    for (int i = 0; str[i]; i++)
        str[i] = toupper(str[i]);
}

int main() {
    FILE *fp = fopen(LOG_FILE, "r");
    if (fp == NULL) {
        printf("Error: %s not found.\n", LOG_FILE);
        return 1;
    }

    FILE *report = fopen(REPORT_FILE, "w");
    if (report == NULL) {
        printf("Error: Could not create %s\n", REPORT_FILE);
        fclose(fp);
        return 1;
    }

    char line[LINE_SIZE];
    char last_error[LINE_SIZE] = "None";
    int error_count = 0, warning_count = 0, info_count = 0;

    // Read file line by line
    while (fgets(line, sizeof(line), fp)) {
        char upper_line[LINE_SIZE];
        strcpy(upper_line, line);
        to_uppercase(upper_line);

        if (strstr(upper_line, "ERROR")) {
            error_count++;
            strcpy(last_error, line);
        } else if (strstr(upper_line, "WARNING")) {
            warning_count++;
        } else if (strstr(upper_line, "INFO")) {
            info_count++;
        }
    }

    fclose(fp);

    // Display on console
    printf("Log Summary Report\n");
    printf("-------------------\n");
    printf("Total ERRORs   : %d\n", error_count);
    printf("Total WARNINGs : %d\n", warning_count);
    printf("Total INFOs    : %d\n", info_count);
    printf("Last ERROR     : %s", last_error);

    // Write to report file
    fprintf(report, "Log Summary Report\n");
    fprintf(report, "-------------------\n");
    fprintf(report, "Total ERRORs   : %d\n", error_count);
    fprintf(report, "Total WARNINGs : %d\n", warning_count);
    fprintf(report, "Total INFOs    : %d\n", info_count);
    fprintf(report, "Last ERROR     : %s", last_error);

    fclose(report);

    printf("\nReport saved to %s\n", REPORT_FILE);
    return 0;
}
