import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

/*
 QuizApp.java
 File-based Quiz Application (Multiple Choice)
*/

class Question {
    private String id;
    private String text;
    private String[] choices; // A, B, C, D
    private char correct; // 'A'..'D'

    public Question(String id, String text, String a, String b, String c, String d, char correct) {
        this.id = id;
        this.text = text;
        this.choices = new String[]{a, b, c, d};
        this.correct = Character.toUpperCase(correct);
    }

    public String getId() { return id; }
    public String getText() { return text; }
    public String getChoice(int index) { return choices[index]; }
    public char getCorrect() { return correct; }

    public boolean isCorrect(char answer) {
        return Character.toUpperCase(answer) == correct;
    }

    public String toFileString() {
        return String.join("|", id, text, choices[0], choices[1], choices[2], choices[3], String.valueOf(correct));
    }

    public static Question fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 7) return null;
        return new Question(parts[0].trim(), parts[1].trim(),
                parts[2].trim(), parts[3].trim(), parts[4].trim(), parts[5].trim(),
                parts[6].trim().isEmpty() ? ' ' : parts[6].trim().charAt(0));
    }

    public void displayFull() {
        System.out.println("ID: " + id);
        System.out.println("Question: " + text);
        System.out.println("A. " + choices[0]);
        System.out.println("B. " + choices[1]);
        System.out.println("C. " + choices[2]);
        System.out.println("D. " + choices[3]);
        System.out.println("Correct: " + correct);
    }
}

class Result {
    private String studentId;
    private String studentName;
    private int score;
    private int total;
    private LocalDate date;

    public Result(String studentId, String studentName, int score, int total, LocalDate date) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.score = score;
        this.total = total;
        this.date = date;
    }

    // FIXED: Write as "score/total" instead of two separate fields
    public String toFileString() {
        return String.join("|", studentId, studentName, score + "/" + total, date.toString());
    }

    // FIXED: Read back "score/total"
    public static Result fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 4) return null;
        String[] scoreParts = parts[2].split("/");
        int score = Integer.parseInt(scoreParts[0]);
        int total = Integer.parseInt(scoreParts[1]);
        return new Result(parts[0].trim(), parts[1].trim(), score, total, LocalDate.parse(parts[3].trim()));
    }

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public LocalDate getDate() { return date; }
}


class MultipleChoiceQuiz {
    private List<Question> questions;
    private static final String QUESTIONS_FILE = "quiz_questions.txt";
    private static final String RESULTS_FILE = "quiz_results.txt";

    public MultipleChoiceQuiz() {
        questions = new ArrayList<>();
        loadQuestions();
    }

    private void loadQuestions() {
        Path p = Paths.get(QUESTIONS_FILE);
        questions.clear();
        if (Files.notExists(p)) {
            System.out.println("(No existing quiz questions found. Please add questions in Instructor mode.)");
            return;
        }
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                Question q = Question.fromFileString(line);
                if (q != null) questions.add(q);
            }
        } catch (IOException e) {
            System.out.println("Error reading questions file: " + e.getMessage());
        }
    }

    private void saveQuestionsToFile() {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(QUESTIONS_FILE))) {
            for (Question q : questions) {
                bw.write(q.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving questions: " + e.getMessage());
        }
    }

    // Instructor menu
    public void instructorMenu(Scanner sc) {
        while (true) {
            System.out.println("\n--- Instructor Menu ---");
            System.out.println("1. View Questions");
            System.out.println("2. Add Question");
            System.out.println("3. Edit Question");
            System.out.println("4. Delete Question");
            System.out.println("5. View Reports");
            System.out.println("6. Back");
            System.out.print("Choice: ");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1" -> viewQuestions();
                case "2" -> addQuestion(sc);
                case "3" -> editQuestion(sc);
                case "4" -> deleteQuestion(sc);
                case "5" -> viewReports();
                case "6" -> { loadQuestions(); return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void viewQuestions() {
        if (questions.isEmpty()) {
            System.out.println("No questions available.");
            return;
        }
        for (Question q : questions) {
            q.displayFull();
            System.out.println("---------------------------------");
        }
    }

    private void addQuestion(Scanner sc) {
        System.out.print("Enter Question ID (e.g., Q1): ");
        String id = sc.nextLine().trim();
        for (Question q : questions)
            if (q.getId().equalsIgnoreCase(id)) {
                System.out.println("ID already exists.");
                return;
            }
        System.out.print("Question text: ");
        String text = sc.nextLine().trim();
        System.out.print("Choice A: ");
        String a = sc.nextLine().trim();
        System.out.print("Choice B: ");
        String b = sc.nextLine().trim();
        System.out.print("Choice C: ");
        String c = sc.nextLine().trim();
        System.out.print("Choice D: ");
        String d = sc.nextLine().trim();
        char correct;
        while (true) {
            System.out.print("Correct answer (A/B/C/D): ");
            String ans = sc.nextLine().trim().toUpperCase();
            if (ans.matches("[ABCD]")) {
                correct = ans.charAt(0);
                break;
            }
            System.out.println("Invalid choice.");
        }
        questions.add(new Question(id, text, a, b, c, d, correct));
        saveQuestionsToFile();
        System.out.println("Question added.");
    }

    private void editQuestion(Scanner sc) {
        System.out.print("Enter Question ID to edit: ");
        String id = sc.nextLine().trim();
        Question target = null;
        for (Question q : questions)
            if (q.getId().equalsIgnoreCase(id)) {
                target = q;
                break;
            }
        if (target == null) {
            System.out.println("Question not found.");
            return;
        }

        System.out.println("Leave blank to keep existing value.");
        System.out.print("New text (current: " + target.getText() + "): ");
        String text = sc.nextLine().trim();
        System.out.print("A (current: " + target.getChoice(0) + "): ");
        String a = sc.nextLine().trim();
        System.out.print("B (current: " + target.getChoice(1) + "): ");
        String b = sc.nextLine().trim();
        System.out.print("C (current: " + target.getChoice(2) + "): ");
        String c = sc.nextLine().trim();
        System.out.print("D (current: " + target.getChoice(3) + "): ");
        String d = sc.nextLine().trim();

        String newText = text.isEmpty() ? target.getText() : text;
        String na = a.isEmpty() ? target.getChoice(0) : a;
        String nb = b.isEmpty() ? target.getChoice(1) : b;
        String nc = c.isEmpty() ? target.getChoice(2) : c;
        String nd = d.isEmpty() ? target.getChoice(3) : d;

        char correct;
        while (true) {
            System.out.print("Correct answer (A/B/C/D) or blank to keep (" + target.getCorrect() + "): ");
            String ans = sc.nextLine().trim().toUpperCase();
            if (ans.isEmpty()) {
                correct = target.getCorrect();
                break;
            }
            if (ans.matches("[ABCD]")) {
                correct = ans.charAt(0);
                break;
            }
            System.out.println("Invalid input.");
        }

        Question newQ = new Question(target.getId(), newText, na, nb, nc, nd, correct);
        for (int i = 0; i < questions.size(); i++)
            if (questions.get(i).getId().equalsIgnoreCase(id)) {
                questions.set(i, newQ);
                break;
            }
        saveQuestionsToFile();
        System.out.println("Question updated.");
    }

    private void deleteQuestion(Scanner sc) {
        System.out.print("Enter Question ID to delete: ");
        String id = sc.nextLine().trim();
        boolean removed = questions.removeIf(q -> q.getId().equalsIgnoreCase(id));
        if (removed) {
            saveQuestionsToFile();
            System.out.println("Question deleted.");
        } else {
            System.out.println("Question not found.");
        }
    }

    public void studentMenu(Scanner sc) {
        System.out.println("\n--- Student Mode ---");
        System.out.print("Student ID: ");
        String sid = sc.nextLine().trim();
        System.out.print("Student Name: ");
        String sname = sc.nextLine().trim();

        if (questions.isEmpty()) {
            System.out.println("No questions available. Please ask your instructor to add some first.");
            return;
        }

        boolean shuffle = false;
        System.out.print("Shuffle questions? (Y/N): ");
        String sh = sc.nextLine().trim().toUpperCase();
        if (sh.equals("Y")) shuffle = true;

        List<Question> quizQs = new ArrayList<>(questions);
        if (shuffle) Collections.shuffle(quizQs);

        int total = quizQs.size();
        int score = 0;

        for (int i = 0; i < quizQs.size(); i++) {
            Question q = quizQs.get(i);
            System.out.println();
            System.out.printf("Q%d) %s%n", i + 1, q.getText());
            System.out.println("A. " + q.getChoice(0));
            System.out.println("B. " + q.getChoice(1));
            System.out.println("C. " + q.getChoice(2));
            System.out.println("D. " + q.getChoice(3));

            char ans;
            while (true) {
                System.out.print("Your answer (A/B/C/D): ");
                String a = sc.nextLine().trim().toUpperCase();
                if (a.isEmpty()) {
                    System.out.println("Answer cannot be blank.");
                    continue;
                }
                if (a.matches("[ABCD]")) {
                    ans = a.charAt(0);
                    break;
                }
                System.out.println("Invalid choice.");
            }

            if (q.isCorrect(ans)) score++;
        }

        LocalDate today = LocalDate.now();
        Result res = new Result(sid, sname, score, total, today);
        appendResultToFile(res);
        System.out.println("\nQuiz completed.");
        System.out.printf("Score: %d/%d%n", score, total);
    }

    private void appendResultToFile(Result r) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get("quiz_results.txt"), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            bw.write(r.toFileString());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error saving result: " + e.getMessage());
        }
    }

    private void viewReports() {
        List<Result> results = loadResults();
        if (results.isEmpty()) {
            System.out.println("No results available.");
            return;
        }

        System.out.println("\nAll Student Results:");
        for (Result r : results)
            System.out.printf("%s | %s | %d/%d | %s%n", r.getStudentId(), r.getStudentName(), r.getScore(), r.getTotal(), r.getDate());

        double avg = results.stream().mapToDouble(r -> 100.0 * r.getScore() / r.getTotal()).average().orElse(0.0);
        Result max = Collections.max(results, Comparator.comparingDouble(r -> 100.0 * r.getScore() / r.getTotal()));
        Result min = Collections.min(results, Comparator.comparingDouble(r -> 100.0 * r.getScore() / r.getTotal()));

        System.out.println();
        System.out.printf("Average score: %.2f%%%n", avg);
        System.out.printf("Highest: %s (%s) -> %d/%d%n", max.getStudentName(), max.getStudentId(), max.getScore(), max.getTotal());
        System.out.printf("Lowest: %s (%s) -> %d/%d%n", min.getStudentName(), min.getStudentId(), min.getScore(), min.getTotal());
    }

    private List<Result> loadResults() {
        List<Result> res = new ArrayList<>();
        Path p = Paths.get("quiz_results.txt");
        if (Files.notExists(p)) return res;
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                Result r = Result.fromFileString(line);
                if (r != null) res.add(r);
            }
        } catch (IOException e) {
            System.out.println("Error reading results: " + e.getMessage());
        }
        return res;
    }
}

public class QuizApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        MultipleChoiceQuiz quiz = new MultipleChoiceQuiz();

        while (true) {
            System.out.println("\n=== Quiz Application ===");
            System.out.println("1. Instructor");
            System.out.println("2. Student");
            System.out.println("3. Exit");
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1" -> quiz.instructorMenu(sc);
                case "2" -> quiz.studentMenu(sc);
                case "3" -> { System.out.println("Exiting."); return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}
