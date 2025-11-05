#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>

#define MAX_BITS 16

int isValidBinary(const char *bin);
void binaryAddition(const char *a, const char *b);
void binarySubtraction(const char *a, const char *b);
void binaryMultiplication(const char *a, const char *b);
int binaryToDecimal(const char *bin);
void decimalToBinary(int num, char *bin);
void printAligned(const char *a, const char *b, const char *res, char op);

int main() {
    int choice;
    char bin1[MAX_BITS + 1], bin2[MAX_BITS + 1];

    do {
        printf("\n===== Binary Arithmetic Simulator =====\n");
        printf("1. Addition\n");
        printf("2. Subtraction\n");
        printf("3. Multiplication\n");
        printf("4. Exit\n");
        printf("Choose an option: ");
        scanf("%d", &choice);
        getchar();

        if (choice == 4) {
            printf("Exiting program...\n");
            break;
        }

        printf("Enter first binary number: ");
        scanf("%s", bin1);
        printf("Enter second binary number: ");
        scanf("%s", bin2);

        if (!isValidBinary(bin1) || !isValidBinary(bin2)) {
            printf("Error: Invalid binary input. Use only 0s and 1s (max %d bits).\n", MAX_BITS);
            continue;
        }

        switch (choice) {
            case 1:
                binaryAddition(bin1, bin2);
                break;
            case 2:
                binarySubtraction(bin1, bin2);
                break;
            case 3:
                binaryMultiplication(bin1, bin2);
                break;
            default:
                printf("Invalid choice.\n");
        }

    } while (1);

    return 0;
}

int isValidBinary(const char *bin) {
    if (strlen(bin) > MAX_BITS) return 0;
    for (int i = 0; bin[i]; i++)
        if (bin[i] != '0' && bin[i] != '1')
            return 0;
    return 1;
}

void printAligned(const char *a, const char *b, const char *res, char op) {
    int lenA = strlen(a);
    int lenB = strlen(b);
    int lenR = strlen(res);
    int width = lenR > lenA + 2 ? lenR : lenA + 2;

    printf("\nStep-by-step:\n");
    printf("%*s\n", width, a);
    printf("%c %*s\n", op, width - 2, b);
    for (int i = 0; i < width; i++) printf("-");
    printf("\n%*s\n", width, res);
}

int binaryToDecimal(const char *bin) {
    int dec = 0;
    for (int i = 0; bin[i]; i++) {
        dec = dec * 2 + (bin[i] - '0');
    }
    return dec;
}

void decimalToBinary(int num, char *bin) {
    if (num == 0) {
        strcpy(bin, "0");
        return;
    }

    char temp[64];
    int i = 0;
    unsigned int n = (num < 0) ? -num : num;
    while (n > 0) {
        temp[i++] = (n % 2) + '0';
        n /= 2;
    }
    if (num < 0) temp[i++] = '-';
    temp[i] = '\0';

    int len = strlen(temp);
    for (int j = 0; j < len; j++)
        bin[j] = temp[len - j - 1];
    bin[len] = '\0';
}

void binaryAddition(const char *a, const char *b) {
    char result[MAX_BITS + 2] = {0};
    int i = strlen(a) - 1, j = strlen(b) - 1, k = MAX_BITS, carry = 0;

    result[k + 1] = '\0';

    while (i >= 0 || j >= 0 || carry) {
        int sum = carry;
        if (i >= 0) sum += a[i--] - '0';
        if (j >= 0) sum += b[j--] - '0';
        result[k--] = (sum % 2) + '0';
        carry = sum / 2;
    }

    char *resPtr = &result[k + 1];
    printAligned(a, b, resPtr, '+');

    int dec = binaryToDecimal(resPtr);
    printf("\nOutput:\n");
    printf("Binary: %s\n", resPtr);
    printf("Decimal: %d\n", dec);
    printf("Hexadecimal: %X\n", dec);
}

void binarySubtraction(const char *a, const char *b) {
    int lenA = strlen(a);
    int lenB = strlen(b);
    int i = lenA - 1, j = lenB - 1, k;
    char result[MAX_BITS + 2];
    int borrow = 0;

    char A[MAX_BITS + 1], B[MAX_BITS + 1];
    strcpy(A, a);
    strcpy(B, b);

    while (lenB < lenA) {
        memmove(B + 1, B, lenB + 1);
        B[0] = '0';
        lenB++;
    }
    while (lenA < lenB) {
        memmove(A + 1, A, lenA + 1);
        A[0] = '0';
        lenA++;
    }

    k = lenA;
    result[k] = '\0';

    for (i = lenA - 1; i >= 0; i--) {
        int bitA = A[i] - '0' - borrow;
        int bitB = B[i] - '0';
        if (bitA < bitB) {
            bitA += 2;
            borrow = 1;
        } else {
            borrow = 0;
        }
        result[i] = (bitA - bitB) + '0';
    }

    char *resPtr = result;
    while (*resPtr == '0' && *(resPtr + 1)) resPtr++;

    printAligned(a, b, resPtr, '-');

    int dec = binaryToDecimal(resPtr);
    printf("\nOutput:\n");
    printf("Binary: %s\n", resPtr);
    printf("Decimal: %d\n", dec);
    printf("Hexadecimal: %X\n", dec);
}

void binaryMultiplication(const char *a, const char *b) {
    int lenA = strlen(a), lenB = strlen(b);
    int decA = binaryToDecimal(a);
    int decB = binaryToDecimal(b);
    int product = 0;

    printf("\nStep-by-step:\n");
    printf("%6s\n", a);
    printf("x %4s\n", b);
    printf("------\n");

    for (int i = lenB - 1; i >= 0; i--) {
        if (b[i] == '1') {
            int shift = lenB - 1 - i;
            product += decA << shift;
            char shifted[64];
            decimalToBinary(decA << shift, shifted);
            printf("%*s\n", 6, shifted);
        }
    }

    printf("------\n");

    char resBin[64];
    decimalToBinary(product, resBin);
    printf("%6s\n", resBin);

    printf("\nOutput:\n");
    printf("Binary: %s\n", resBin);
    printf("Decimal: %d\n", product);
    printf("Hexadecimal: %X\n", product);
}
