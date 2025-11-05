#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

int getBaseValue(char *baseName);
int isValidNumber(char *num, int base);
long long convertToDecimal(char *num, int base);
void convertFromDecimal(long long decimal);
void baseConversion();
void arithmeticOperation();

int main() {
    int choice;

    while (1) {
        printf("===== Multi-base Calculator =====\n");
        printf("1. Base Conversion\n");
        printf("2. Arithmetic Operation\n");
        printf("3. Exit\n");
        printf("Choose an option: ");
        scanf("%d", &choice);
        getchar();

        switch (choice) {
            case 1:
                baseConversion();
                break;
            case 2:
                arithmeticOperation();
                break;
            case 3:
                printf("Exiting program...\n");
                return 0;
            default:
                printf("Invalid choice. Try again.\n\n");
        }
    }

    return 0;
}

int getBaseValue(char *baseName) {
    for (int i = 0; baseName[i]; i++)
        baseName[i] = tolower(baseName[i]);

    if (strcmp(baseName, "binary") == 0) return 2;
    if (strcmp(baseName, "octal") == 0) return 8;
    if (strcmp(baseName, "decimal") == 0) return 10;
    if (strcmp(baseName, "hexadecimal") == 0) return 16;

    return -1;
}

int isValidNumber(char *num, int base) {
    int i = 0;
    if (num[0] == '-' || num[0] == '+') i = 1;

    for (; num[i]; i++) {
        char c = toupper(num[i]);
        if (base == 2 && (c != '0' && c != '1')) return 0;
        if (base == 8 && (c < '0' || c > '7')) return 0;
        if (base == 10 && !isdigit(c)) return 0;
        if (base == 16 && !(isdigit(c) || (c >= 'A' && c <= 'F'))) return 0;
    }
    return 1;
}

long long convertToDecimal(char *num, int base) {
    return strtoll(num, NULL, base);
}

void convertFromDecimal(long long decimal) {
    printf("Decimal: %lld\n", decimal);
    printf("Octal: %llo\n", decimal);

    char binary[65];
    unsigned long long temp = (decimal < 0) ? -decimal : decimal;
    int i = 0;
    if (temp == 0) binary[i++] = '0';
    while (temp > 0) {
        binary[i++] = (temp % 2) + '0';
        temp /= 2;
    }
    if (decimal < 0) printf("Binary: -");
    for (int j = i - 1; j >= 0; j--) printf("%c", binary[j]);
    printf("\n");

    if (decimal < 0)
        printf("Hexadecimal: -%llX\n", -decimal);
    else
        printf("Hexadecimal: %llX\n", decimal);
}

void baseConversion() {
    char num[65], baseName[20];
    printf("Enter number: ");
    scanf("%s", num);
    printf("Enter base (Binary/Octal/Decimal/Hexadecimal): ");
    scanf("%s", baseName);

    int base = getBaseValue(baseName);
    if (base == -1) {
        printf("Error: Invalid base.\n\n");
        return;
    }

    if (!isValidNumber(num, base)) {
        printf("Error: Invalid %s number.\n\n", baseName);
        return;
    }

    long long decimal = convertToDecimal(num, base);
    printf("Output:\n");
    convertFromDecimal(decimal);
    printf("\n");
}

void arithmeticOperation() {
    char num1[65], num2[65];
    char base1[20], base2[20];
    char op;
    printf("Enter first number: ");
    scanf("%s", num1);
    printf("Enter base (Binary/Octal/Decimal/Hexadecimal): ");
    scanf("%s", base1);
    printf("Enter second number: ");
    scanf("%s", num2);
    printf("Enter base (Binary/Octal/Decimal/Hexadecimal): ");
    scanf("%s", base2);
    printf("Operation (+/-): ");
    scanf(" %c", &op);

    int b1 = getBaseValue(base1);
    int b2 = getBaseValue(base2);

    if (b1 == -1 || b2 == -1) {
        printf("Error: Invalid base.\n\n");
        return;
    }

    if (!isValidNumber(num1, b1)) {
        printf("Error: Invalid %s number.\n\n", base1);
        return;
    }
    if (!isValidNumber(num2, b2)) {
        printf("Error: Invalid %s number.\n\n", base2);
        return;
    }

    long long dec1 = convertToDecimal(num1, b1);
    long long dec2 = convertToDecimal(num2, b2);
    long long result;

    if (op == '+')
        result = dec1 + dec2;
    else if (op == '-')
        result = dec1 - dec2;
    else {
        printf("Error: Invalid operation.\n\n");
        return;
    }

    printf("Output:\n");
    convertFromDecimal(result);
    printf("\n");
}
