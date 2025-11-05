#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

int isBinary(const char *s) {
    for (int i = 0; s[i]; i++)
        if (s[i] != '0' && s[i] != '1')
            return 0;
    return 1;
}

int isHex(const char *s) {
    for (int i = 0; s[i]; i++)
        if (!isxdigit(s[i]))
            return 0;
    return 1;
}

unsigned int decimalToInt(const char *ip) {
    unsigned int bytes[4];
    if (sscanf(ip, "%u.%u.%u.%u", &bytes[0], &bytes[1], &bytes[2], &bytes[3]) != 4)
        return 0xFFFFFFFF;
    for (int i = 0; i < 4; i++)
        if (bytes[i] > 255)
            return 0xFFFFFFFF;
    return (bytes[0] << 24) | (bytes[1] << 16) | (bytes[2] << 8) | bytes[3];
}

unsigned int binaryToInt(const char *bin) {
    unsigned int value = 0;
    for (int i = 0; i < 32; i++) {
        value = (value << 1) | (bin[i] - '0');
    }
    return value;
}

unsigned int hexToInt(const char *hex) {
    unsigned int value = 0;
    for (int i = 0; i < 8; i++) {
        char c = toupper(hex[i]);
        int digit = (c >= 'A') ? (c - 'A' + 10) : (c - '0');
        value = (value << 4) | digit;
    }
    return value;
}

void printDecimal(unsigned int ip) {
    printf("Decimal: %u.%u.%u.%u\n",
           (ip >> 24) & 0xFF,
           (ip >> 16) & 0xFF,
           (ip >> 8) & 0xFF,
           ip & 0xFF);
}

void printBinary(unsigned int ip) {
    printf("Binary : ");
    for (int i = 31; i >= 0; i--) {
        printf("%d", (ip >> i) & 1);
        if (i % 8 == 0 && i != 0) printf(".");
    }
    printf("\n");
}

void printHex(unsigned int ip) {
    printf("Hex    : %02X%02X%02X%02X\n",
           (ip >> 24) & 0xFF,
           (ip >> 16) & 0xFF,
           (ip >> 8) & 0xFF,
           ip & 0xFF);
}

int main() {
    char input[100];
    printf("Enter IP Address (Decimal/Binary/Hex): ");
    scanf("%s", input);

    unsigned int ipValue = 0;
    int valid = 1;

    if (strchr(input, '.')) {

        ipValue = decimalToInt(input);
        if (ipValue == 0xFFFFFFFF) valid = 0;
    } else if (isBinary(input) && strlen(input) == 32) {
        ipValue = binaryToInt(input);
    } else if (isHex(input) && strlen(input) == 8) {
        ipValue = hexToInt(input);
    } else {
        valid = 0;
    }

    if (!valid) {
        printf("Invalid IP address format!\n");
        return 1;
    }

    printf("\nOutput:\n");
    printDecimal(ipValue);
    printBinary(ipValue);
    printHex(ipValue);

    return 0;
}
