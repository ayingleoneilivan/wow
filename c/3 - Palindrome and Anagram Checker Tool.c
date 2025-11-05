#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>

#define MAX_LEN 200

void cleanString(char *src, char *dest);
int isPalindrome(char *str);
int isAnagram(char *str1, char *str2);
void sortString(char *str);

int main() {
    int choice;
    char input1[MAX_LEN], input2[MAX_LEN];
    char clean1[MAX_LEN], clean2[MAX_LEN];

    do {
        printf("\n===== Palindrome & Anagram Tool =====\n");
        printf("1. Palindrome Checker\n");
        printf("2. Anagram Checker\n");
        printf("3. Exit\n");
        printf("Choose an option: ");
        scanf("%d", &choice);
        getchar();

        switch (choice) {
            case 1:
                printf("Enter word/phrase: ");
                fgets(input1, MAX_LEN, stdin);
                input1[strcspn(input1, "\n")] = '\0';

                cleanString(input1, clean1);

                if (strlen(clean1) == 0) {
                    printf("Invalid input.\n");
                    break;
                }

                if (isPalindrome(clean1))
                    printf("Output: Palindrome\n");
                else
                    printf("Output: Not Palindrome\n");
                break;

            case 2:
                printf("Enter first word/phrase: ");
                fgets(input1, MAX_LEN, stdin);
                input1[strcspn(input1, "\n")] = '\0';

                printf("Enter second word/phrase: ");
                fgets(input2, MAX_LEN, stdin);
                input2[strcspn(input2, "\n")] = '\0';

                cleanString(input1, clean1);
                cleanString(input2, clean2);

                if (strlen(clean1) == 0 || strlen(clean2) == 0) {
                    printf("Invalid input.\n");
                    break;
                }

                if (isAnagram(clean1, clean2))
                    printf("Output: Anagrams\n");
                else
                    printf("Output: Not Anagrams\n");
                break;

            case 3:
                printf("Exiting program...\n");
                break;

            default:
                printf("Invalid choice. Please try again.\n");
        }

    } while (choice != 3);

    return 0;
}

void cleanString(char *src, char *dest) {
    int j = 0;
    for (int i = 0; src[i]; i++) {
        if (isalnum((unsigned char)src[i])) {
            dest[j++] = tolower((unsigned char)src[i]);
        }
    }
    dest[j] = '\0';
}

int isPalindrome(char *str) {
    int left = 0;
    int right = strlen(str) - 1;
    while (left < right) {
        if (str[left] != str[right])
            return 0;
        left++;
        right--;
    }
    return 1;
}

int isAnagram(char *str1, char *str2) {
    if (strlen(str1) != strlen(str2))
        return 0;

    char temp1[MAX_LEN], temp2[MAX_LEN];
    strcpy(temp1, str1);
    strcpy(temp2, str2);

    sortString(temp1);
    sortString(temp2);

    return strcmp(temp1, temp2) == 0;
}

void sortString(char *str) {
    int n = strlen(str);
    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - i - 1; j++) {
            if (str[j] > str[j + 1]) {
                char temp = str[j];
                str[j] = str[j + 1];
                str[j + 1] = temp;
            }
        }
    }
}
