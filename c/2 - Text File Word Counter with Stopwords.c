#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_WORD_LEN 100
#define MAX_STOPWORDS 1000
#define MAX_WORDS 10000
#define MAX_FILE_SIZE 5242880

typedef struct {
    char word[MAX_WORD_LEN];
    int count;
} WordCount;

void toLowerCase(char *str);
void removePunctuation(char *str);
int isStopword(char *word, char stopwords[][MAX_WORD_LEN], int stopwordCount);
int compare(const void *a, const void *b);

int main() {
    FILE *fpInput, *fpStop, *fpOutput;
    char filename[100] = "input.txt";
    char stopfile[100] = "stopwords.txt";
    char outputfile[100] = "output.txt";

    fpInput = fopen(filename, "r");
    if (!fpInput) {
        printf("Error: Could not open input file '%s'.\n", filename);
        return 1;
    }
    fseek(fpInput, 0, SEEK_END);
    long fileSize = ftell(fpInput);
    fseek(fpInput, 0, SEEK_SET);
    if (fileSize == 0) {
        printf("Error: Input file is empty.\n");
        fclose(fpInput);
        return 1;
    }
    if (fileSize > MAX_FILE_SIZE) {
        printf("Error: File exceeds 5 MB size limit.\n");
        fclose(fpInput);
        return 1;
    }

    fpStop = fopen(stopfile, "r");
    if (!fpStop) {
        printf("Error: Could not open stopwords file '%s'.\n", stopfile);
        fclose(fpInput);
        return 1;
    }

    char stopwords[MAX_STOPWORDS][MAX_WORD_LEN];
    int stopwordCount = 0;
    while (fgets(stopwords[stopwordCount], MAX_WORD_LEN, fpStop)) {
        stopwords[stopwordCount][strcspn(stopwords[stopwordCount], "\n")] = '\0';
        toLowerCase(stopwords[stopwordCount]);
        stopwordCount++;
    }
    fclose(fpStop);

    WordCount wordCounts[MAX_WORDS];
    int wordCount = 0;
    char word[MAX_WORD_LEN];

    while (fscanf(fpInput, "%99s", word) == 1) {
        removePunctuation(word);
        toLowerCase(word);

        if (strlen(word) == 0) continue;
        if (isStopword(word, stopwords, stopwordCount)) continue;

        int found = 0;
        for (int i = 0; i < wordCount; i++) {
            if (strcmp(wordCounts[i].word, word) == 0) {
                wordCounts[i].count++;
                found = 1;
                break;
            }
        }
        if (!found) {
            strcpy(wordCounts[wordCount].word, word);
            wordCounts[wordCount].count = 1;
            wordCount++;
        }
    }
    fclose(fpInput);

    if (wordCount == 0) {
        printf("No words found after filtering stopwords.\n");
        return 0;
    }

    qsort(wordCounts, wordCount, sizeof(WordCount), compare);

    printf("Word Frequency Count (excluding stopwords):\n");
    for (int i = 0; i < wordCount; i++) {
        printf("%s -> %d\n", wordCounts[i].word, wordCounts[i].count);
    }

    fpOutput = fopen(outputfile, "w");
    if (!fpOutput) {
        printf("Error: Could not create output file '%s'.\n", outputfile);
        return 1;
    }

    for (int i = 0; i < wordCount; i++) {
        fprintf(fpOutput, "%s: %d\n", wordCounts[i].word, wordCounts[i].count);
    }
    fclose(fpOutput);

    printf("\nResults exported successfully to '%s'.\n", outputfile);
    return 0;
}

void toLowerCase(char *str) {
    for (int i = 0; str[i]; i++)
        str[i] = tolower((unsigned char)str[i]);
}

void removePunctuation(char *str) {
    int i, j = 0;
    for (i = 0; str[i]; i++) {
        if (isalnum((unsigned char)str[i]))
            str[j++] = str[i];
    }
    str[j] = '\0';
}

int isStopword(char *word, char stopwords[][MAX_WORD_LEN], int stopwordCount) {
    for (int i = 0; i < stopwordCount; i++) {
        if (strcmp(word, stopwords[i]) == 0)
            return 1;
    }
    return 0;
}

int compare(const void *a, const void *b) {
    WordCount *w1 = (WordCount *)a;
    WordCount *w2 = (WordCount *)b;
    return w2->count - w1->count;
}
