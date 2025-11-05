#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>

void compressFile() {
    FILE *in = fopen("input.txt", "r");
    FILE *out = fopen("compressed.txt", "w");

    if (!in) {
        printf("Error: Cannot open input.txt\n");
        return;
    }
    if (!out) {
        printf("Error: Cannot create compressed.txt\n");
        fclose(in);
        return;
    }

    int ch, prev;
    int count = 1;

    prev = fgetc(in);
    if (prev == EOF) {
        printf("input.txt is empty.\n");
        fclose(in);
        fclose(out);
        return;
    }

    while ((ch = fgetc(in)) != EOF) {
        if (ch == prev) {
            count++;
        } else {
            fprintf(out, "%c%d", prev, count);
            prev = ch;
            count = 1;
        }
    }

    fprintf(out, "%c%d", prev, count);

    fclose(in);
    fclose(out);

    printf("File compressed successfully compressed.txt\n");
}

int isValidRLEFormat(FILE *file) {
    int ch;
    int expectingDigit = 0;

    while ((ch = fgetc(file)) != EOF) {
        if (expectingDigit) {
            if (!isdigit(ch)) return 0;
        } else {
            if (!isprint(ch) || isdigit(ch)) return 0;
        }
        expectingDigit = !expectingDigit;
    }

    return !expectingDigit;
}

void decompressFile() {
    FILE *in = fopen("compressed.txt", "r");
    FILE *out = fopen("decompressed.txt", "w");

    if (!in) {
        printf("Error: Cannot open compressed.txt\n");
        return;
    }
    if (!out) {
        printf("Error: Cannot create decompressed.txt\n");
        fclose(in);
        return;
    }

    fseek(in, 0, SEEK_SET);
    if (!isValidRLEFormat(in)) {
        printf("Error: compressed.txt is not in valid RLE format!\n");
        fclose(in);
        fclose(out);
        return;
    }

    rewind(in);

    int ch;
    while ((ch = fgetc(in)) != EOF) {
        int count;
        if (fscanf(in, "%d", &count) != 1) {
            printf("Error: Invalid RLE structure!\n");
            fclose(in);
            fclose(out);
            return;
        }
        for (int i = 0; i < count; i++) {
            fputc(ch, out);
        }
    }

    fclose(in);
    fclose(out);

    printf("File decompressed successfully decompressed.txt\n");
}

int main() {
    int choice;

    while (1) {
        printf("\n=== File Compression Simulator (RLE) ===\n");
        printf("1. Compress File\n");
        printf("2. Decompress File\n");
        printf("3. Exit\n");
        printf("Enter your choice: ");
        scanf("%d", &choice);
        getchar();

        switch (choice) {
            case 1:
                compressFile();
                break;
            case 2:
                decompressFile();
                break;
            case 3:
                printf("Exiting program...\n");
                return 0;
            default:
                printf("Invalid choice! Please enter 1–3.\n");
        }
    }
}
