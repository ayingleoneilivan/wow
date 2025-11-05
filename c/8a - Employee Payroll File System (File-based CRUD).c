#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define FILE_NAME "employees.txt"
#define MAX 200

// Structure to hold employee payroll info
typedef struct {
    int empID;
    char name[50];
    char position[50];
    float basicSalary;
    float allowances;
    float deductions;
    float tax;
    float netSalary;
} Employee;

// Function prototypes
void addEmployee();
void viewEmployees();
void updateEmployee();
void deleteEmployee();
void searchAndPayslip();
int employeeExists(int id);
void saveEmployees(Employee employees[], int count);
float calculateTax(float gross);
float calculateNetSalary(float basic, float allowances, float deductions);

// Main program
int main() {
    int choice;

    do {
        printf("\n==== EMPLOYEE PAYROLL MANAGEMENT SYSTEM ====\n");
        printf("1. Add Employee Record\n");
        printf("2. View All Records\n");
        printf("3. Update Employee Record\n");
        printf("4. Delete Employee Record\n");
        printf("5. Search & Generate Payslip\n");
        printf("6. Exit\n");
        printf("Enter choice: ");
        scanf("%d", &choice);
        getchar(); // consume newline

        switch (choice) {
            case 1: addEmployee(); break;
            case 2: viewEmployees(); break;
            case 3: updateEmployee(); break;
            case 4: deleteEmployee(); break;
            case 5: searchAndPayslip(); break;
            case 6: printf("Exiting program...\n"); break;
            default: printf("Invalid choice! Try again.\n");
        }
    } while (choice != 6);

    return 0;
}

// Check if EmployeeID already exists
int employeeExists(int id) {
    FILE *file = fopen(FILE_NAME, "r");
    if (!file) return 0;

    Employee e;
    while (fscanf(file, "%d|%[^|]|%[^|]|%f|%f|%f|%f|%f\n",
                  &e.empID, e.name, e.position, &e.basicSalary,
                  &e.allowances, &e.deductions, &e.tax, &e.netSalary) == 8) {
        if (e.empID == id) {
            fclose(file);
            return 1;
        }
    }
    fclose(file);
    return 0;
}

// Calculate tax (10% of gross salary)
float calculateTax(float gross) {
    return gross * 0.10;
}

// Calculate net salary
float calculateNetSalary(float basic, float allowances, float deductions) {
    float gross = basic + allowances - deductions;
    float tax = calculateTax(gross);
    return gross - tax;
}

// Add new employee record
void addEmployee() {
    FILE *file = fopen(FILE_NAME, "a+");
    if (!file) {
        printf("Error opening file!\n");
        return;
    }

    Employee e;

    printf("Enter Employee ID: ");
    scanf("%d", &e.empID);
    getchar();

    if (employeeExists(e.empID)) {
        printf("Error: Employee ID already exists!\n");
        fclose(file);
        return;
    }

    printf("Enter Full Name: ");
    fgets(e.name, sizeof(e.name), stdin);
    e.name[strcspn(e.name, "\n")] = 0;

    printf("Enter Position: ");
    fgets(e.position, sizeof(e.position), stdin);
    e.position[strcspn(e.position, "\n")] = 0;

    printf("Enter Basic Salary: ");
    scanf("%f", &e.basicSalary);
    printf("Enter Allowances: ");
    scanf("%f", &e.allowances);
    printf("Enter Deductions: ");
    scanf("%f", &e.deductions);
    getchar();

    if (e.basicSalary < 0 || e.allowances < 0 || e.deductions < 0) {
        printf("Error: Salary values cannot be negative.\n");
        fclose(file);
        return;
    }

    float gross = e.basicSalary + e.allowances - e.deductions;
    e.tax = calculateTax(gross);
    e.netSalary = gross - e.tax;

    fprintf(file, "%d|%s|%s|%.2f|%.2f|%.2f|%.2f|%.2f\n",
            e.empID, e.name, e.position, e.basicSalary,
            e.allowances, e.deductions, e.tax, e.netSalary);

    fclose(file);
    printf("Record added successfully!\n");
}

// View all employee records
void viewEmployees() {
    FILE *file = fopen(FILE_NAME, "r");
    if (!file) {
        printf("No records found.\n");
        return;
    }

    Employee e;
    int count = 0;

    printf("\n--------------------------------------------------------\n");
    printf("%-5s %-20s %-20s %-10s\n", "ID", "Name", "Position", "Net Salary");
    printf("--------------------------------------------------------\n");

    while (fscanf(file, "%d|%[^|]|%[^|]|%f|%f|%f|%f|%f\n",
                  &e.empID, e.name, e.position, &e.basicSalary,
                  &e.allowances, &e.deductions, &e.tax, &e.netSalary) == 8) {
        printf("%-5d %-20s %-20s %-10.2f\n",
               e.empID, e.name, e.position, e.netSalary);
        count++;
    }

    if (count == 0)
        printf("No records found.\n");

    printf("--------------------------------------------------------\n");
    fclose(file);
}

// Update employee record
void updateEmployee() {
    FILE *file = fopen(FILE_NAME, "r");
    if (!file) {
        printf("No records found.\n");
        return;
    }

    Employee employees[MAX];
    int count = 0;

    while (fscanf(file, "%d|%[^|]|%[^|]|%f|%f|%f|%f|%f\n",
                  &employees[count].empID, employees[count].name,
                  employees[count].position, &employees[count].basicSalary,
                  &employees[count].allowances, &employees[count].deductions,
                  &employees[count].tax, &employees[count].netSalary) == 8) {
        count++;
    }
    fclose(file);

    int id;
    printf("Enter Employee ID to update: ");
    scanf("%d", &id);
    getchar();

    int found = 0;
    for (int i = 0; i < count; i++) {
        if (employees[i].empID == id) {
            found = 1;
            printf("Enter new Basic Salary (%.2f): ", employees[i].basicSalary);
            scanf("%f", &employees[i].basicSalary);
            printf("Enter new Allowances (%.2f): ", employees[i].allowances);
            scanf("%f", &employees[i].allowances);
            printf("Enter new Deductions (%.2f): ", employees[i].deductions);
            scanf("%f", &employees[i].deductions);
            getchar();

            float gross = employees[i].basicSalary + employees[i].allowances - employees[i].deductions;
            employees[i].tax = calculateTax(gross);
            employees[i].netSalary = gross - employees[i].tax;

            printf("Record updated successfully!\n");
            break;
        }
    }

    if (!found)
        printf("Employee ID not found.\n");
    else
        saveEmployees(employees, count);
}

// Delete employee record
void deleteEmployee() {
    FILE *file = fopen(FILE_NAME, "r");
    if (!file) {
        printf("No records found.\n");
        return;
    }

    Employee employees[MAX];
    int count = 0;

    while (fscanf(file, "%d|%[^|]|%[^|]|%f|%f|%f|%f|%f\n",
                  &employees[count].empID, employees[count].name,
                  employees[count].position, &employees[count].basicSalary,
                  &employees[count].allowances, &employees[count].deductions,
                  &employees[count].tax, &employees[count].netSalary) == 8) {
        count++;
    }
    fclose(file);

    int id;
    printf("Enter Employee ID to delete: ");
    scanf("%d", &id);
    getchar();

    int found = 0, j = 0;
    Employee updated[MAX];

    for (int i = 0; i < count; i++) {
        if (employees[i].empID == id) {
            found = 1;
            continue;
        }
        updated[j++] = employees[i];
    }

    if (!found)
        printf("Employee ID not found.\n");
    else {
        saveEmployees(updated, j);
        printf("Record deleted successfully!\n");
    }
}

// Save employee array to file
void saveEmployees(Employee employees[], int count) {
    FILE *file = fopen(FILE_NAME, "w");
    if (!file) {
        printf("Error writing to file!\n");
        return;
    }

    for (int i = 0; i < count; i++) {
        fprintf(file, "%d|%s|%s|%.2f|%.2f|%.2f|%.2f|%.2f\n",
                employees[i].empID, employees[i].name, employees[i].position,
                employees[i].basicSalary, employees[i].allowances,
                employees[i].deductions, employees[i].tax, employees[i].netSalary);
    }

    fclose(file);
}

// Search employee & display payslip
void searchAndPayslip() {
    FILE *file = fopen(FILE_NAME, "r");
    if (!file) {
        printf("No records found.\n");
        return;
    }

    int id;
    printf("Enter Employee ID: ");
    scanf("%d", &id);
    getchar();

    Employee e;
    int found = 0;

    while (fscanf(file, "%d|%[^|]|%[^|]|%f|%f|%f|%f|%f\n",
                  &e.empID, e.name, e.position, &e.basicSalary,
                  &e.allowances, &e.deductions, &e.tax, &e.netSalary) == 8) {
        if (e.empID == id) {
            found = 1;
            printf("\nPayslip for %s\n", e.name);
            printf("--------------------------------\n");
            printf("Position      : %s\n", e.position);
            printf("Basic Salary  : %.2f\n", e.basicSalary);
            printf("Allowances    : %.2f\n", e.allowances);
            printf("Deductions    : %.2f\n", e.deductions);
            float gross = e.basicSalary + e.allowances - e.deductions;
            printf("Gross Salary  : %.2f\n", gross);
            printf("Tax (10%%)     : %.2f\n", e.tax);
            printf("Net Salary    : %.2f\n", e.netSalary);
            printf("--------------------------------\n");
            break;
        }
    }

    if (!found)
        printf("Employee ID not found.\n");

    fclose(file);
}
