
import java.util.*;
import java.io.*;

public class AIChatbot {

    static Scanner input = new Scanner(System.in);

    static LinkedHashMap<String, String> knowledgeBase =
            new LinkedHashMap<>();

    static LinkedHashMap<String, String> customFAQs =
            new LinkedHashMap<>();

    static ArrayList<String> chatHistory = new ArrayList<>();

    static final String FAQ_FILE = "custom_faqs.properties";

    static void loadFAQs() {
        knowledgeBase.put("hello", "Hello! Welcome to Student Helpdesk. How can I help you?");
        knowledgeBase.put("how are you", "I am doing great! How can I assist you today?");
        knowledgeBase.put("what is your name", "I am AI Student Helpdesk Chatbot.");
        knowledgeBase.put("what is java", "Java is a high-level, object-oriented programming language.");
        knowledgeBase.put("what is oop", "OOP stands for Object-Oriented Programming. It organizes code using classes and objects.");
        knowledgeBase.put("what is a class", "A class is a blueprint used to create objects in Java.");
        knowledgeBase.put("what is an object", "An object is an instance of a class.");
        knowledgeBase.put("what is inheritance", "Inheritance allows one class to acquire properties and methods of another class.");
        knowledgeBase.put("what is polymorphism", "Polymorphism allows one method or object to take different forms.");
        knowledgeBase.put("what is encapsulation", "Encapsulation combines data and methods in one class and controls access to data.");
        knowledgeBase.put("what is abstraction", "Abstraction hides implementation details and shows essential features.");
        knowledgeBase.put("what is arraylist", "ArrayList is a resizable collection in Java that stores elements.");
        knowledgeBase.put("what is a constructor", "A constructor is a special class member used to initialize objects.");
        knowledgeBase.put("what is a method", "A method is a block of code that performs a particular task.");
        knowledgeBase.put("what is github", "GitHub is a platform used to store, manage and share code.");
        knowledgeBase.put("what is programming", "Programming is the process of writing instructions for a computer.");
        knowledgeBase.put("thank you", "You're welcome! Happy learning.");
        knowledgeBase.put("goodbye", "Goodbye! Keep learning and keep coding.");

        loadCustomFAQs();
    }

    static void loadCustomFAQs() {
        Properties properties = new Properties();

        File file = new File(FAQ_FILE);

        if (!file.exists()) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);

            for (String key : properties.stringPropertyNames()) {
                customFAQs.put(key, properties.getProperty(key));
            }

            knowledgeBase.putAll(customFAQs);

        } catch (IOException e) {
            System.out.println("Could not load saved FAQs.");
        }
    }

    static void saveCustomFAQs() {
        Properties properties = new Properties();

        for (Map.Entry<String, String> entry : customFAQs.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }

        try (FileOutputStream fos = new FileOutputStream(FAQ_FILE)) {
            properties.store(fos, "Custom Chatbot FAQs");

        } catch (IOException e) {
            System.out.println("Could not save FAQs.");
        }
    }

    static Set<String> getKeywords(String text) {
        Set<String> keywords = new HashSet<>();

        String[] words = text.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .split("\\s+");

        Set<String> stopWords = new HashSet<>(Arrays.asList(
                "what", "is", "the", "a", "an", "how",
                "are", "you", "do", "does", "can", "i",
                "me", "please", "tell", "about", "in",
                "to", "of", "and", "my", "your", "explain"
        ));

        for (String word : words) {
            if (!word.isEmpty() && !stopWords.contains(word)) {
                keywords.add(word);
            }
        }

        return keywords;
    }

    static String findAnswer(String question) {
        String normalized = question.toLowerCase().trim();

        if (normalized.matches("^(hi|hey|hello|assalamualaikum)[!. ]*$")) {
            return knowledgeBase.get("hello");
        }

        if (normalized.matches("^(bye|goodbye|exit)[!. ]*$")) {
            return knowledgeBase.get("goodbye");
        }

        if (normalized.matches("^(thanks|thank you)[!. ]*$")) {
            return knowledgeBase.get("thank you");
        }

        Set<String> userKeywords = getKeywords(question);

        if (userKeywords.isEmpty()) {
            return "Please ask a programming-related question.";
        }

        String bestAnswer = null;
        double bestScore = 0;

        for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {

            Set<String> faqKeywords = getKeywords(entry.getKey());

            int matches = 0;

            for (String word : userKeywords) {
                if (faqKeywords.contains(word)) {
                    matches++;
                }
            }

            double score = (double) matches /
                    Math.max(1, faqKeywords.size());

            if (score > bestScore) {
                bestScore = score;
                bestAnswer = entry.getValue();
            }
        }

        if (bestScore >= 0.5 && bestAnswer != null) {
            return bestAnswer;
        }

        return "Sorry, I don't know that answer yet. "
                + "You can teach me using the /teach command.";
    }

    static void teachChatbot() {
        System.out.println("\n--- Teach Chatbot ---");

        System.out.print("Enter a question or keyword: ");
        String question = input.nextLine().trim();

        if (question.isEmpty()) {
            System.out.println("Question cannot be empty.");
            return;
        }

        System.out.print("Enter its answer: ");
        String answer = input.nextLine().trim();

        if (answer.isEmpty()) {
            System.out.println("Answer cannot be empty.");
            return;
        }

        customFAQs.put(question, answer);
        knowledgeBase.put(question, answer);

        saveCustomFAQs();

        System.out.println("New FAQ saved successfully!");
    }

    static void showFAQs() {
        System.out.println("\n--- Available Questions ---");

        int number = 1;

        for (String question : knowledgeBase.keySet()) {
            System.out.println(number + ". " + question);
            number++;
        }
    }

    static void showHistory() {
        System.out.println("\n--- Chat History ---");

        if (chatHistory.isEmpty()) {
            System.out.println("No conversation yet.");
            return;
        }

        for (String message : chatHistory) {
            System.out.println(message);
        }
    }

    static void showHelp() {
        System.out.println("\n--- Available Commands ---");
        System.out.println("/teach  - Add a new question and answer");
        System.out.println("/faqs   - View available questions");
        System.out.println("/history - View current chat history");
        System.out.println("/clear  - Clear current chat history");
        System.out.println("/help   - Show commands");
        System.out.println("/exit   - Close chatbot");
    }

    public static void main(String[] args) {

        loadFAQs();

        System.out.println("==================================");
        System.out.println("   AI STUDENT HELPDESK CHATBOT");
        System.out.println("==================================");

        System.out.println("Hello! Ask me anything about Java.");
        System.out.println("Type /help to see available commands.");
        System.out.println("Type /exit to close the chatbot.");

        while (true) {

            System.out.print("\nYou: ");
            String question = input.nextLine().trim();

            if (question.equalsIgnoreCase("/exit")) {
                System.out.println("Bot: Goodbye! Happy learning.");
                break;
            }

            if (question.equalsIgnoreCase("/teach")) {
                teachChatbot();
                continue;
            }

            if (question.equalsIgnoreCase("/faqs")) {
                showFAQs();
                continue;
            }

            if (question.equalsIgnoreCase("/history")) {
                showHistory();
                continue;
            }

            if (question.equalsIgnoreCase("/help")) {
                showHelp();
                continue;
            }

            if (question.equalsIgnoreCase("/clear")) {
                chatHistory.clear();
                System.out.println("Chat history cleared.");
                continue;
            }

            if (question.isEmpty()) {
                System.out.println("Bot: Please enter a question.");
                continue;
            }

            String answer = findAnswer(question);

            System.out.println("Bot: " + answer);

            chatHistory.add("You: " + question);
            chatHistory.add("Bot: " + answer);
        }

        saveCustomFAQs();
        input.close();
    }
}
