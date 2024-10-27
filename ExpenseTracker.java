import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Represents an individual expense with amount, category, and description
class Expense {
    private double amount;
    private String category;
    private String description;

    public Expense(double amount, String category, String description) {
        this.amount = amount;
        this.category = category;
        this.description = description;
    }

    // Getters for each field
    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    // Override toString to display expense information
    @Override
    public String toString() {
        return "Expense{" +
                "amount=" + amount +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

// Manages a collection of expenses, including adding, calculating totals, and saving/loading from files
class ExpenseTracker {
    private List<Expense> expenses;

    public ExpenseTracker() {
        expenses = new ArrayList<>();
    }

    // Add a new expense to the list
    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    // Retrieve all expenses
    public List<Expense> getExpenses() {
        return expenses;
    }

    // Calculate total expenses across all categories
    public double getTotalExpenses() {
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        return total;
    }

    // Calculate total expenses for a specific category
    public double getTotalByCategory(String category) {
        double total = 0;
        for (Expense expense : expenses) {
            if (expense.getCategory().equalsIgnoreCase(category)) {
                total += expense.getAmount();
            }
            else{
                System.out.println("category not found");
            }
        }
        return total;
    }

    // Print all expenses to the console
    public void printExpenses() {
        for (Expense expense : expenses) {
            System.out.println(expense);
        }
    }

    // Save all expenses to a file in CSV format
    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Expense expense : expenses) {
                writer.write(expense.getAmount() + "," + expense.getCategory() + "," + expense.getDescription());
                writer.newLine();
            }
            System.out.println("Expenses saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

// Main application class for interacting with the user
public class ExpenseTrackerApp {
    public static void main(String[] args) {
        ExpenseTracker tracker = new ExpenseTracker();
        Scanner sc = new Scanner(System.in);
        int choice;

        // Display menu options and process user input
        do {
            System.out.println("\nExpense Tracker Menu:");
            System.out.println("1. Add Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. View Total Expenses");
            System.out.println("4. View Total by Category");
            System.out.println("5. Save Expenses to File");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine(); 

            switch (choice) {
                case 1:
                    // Get details from user and add new expense
                    double amount =getAmountInput(sc);
                    System.out.print("Enter category: ");
                    String category = sc.nextLine();
                    System.out.print("Enter description: ");
                    String description = sc.nextLine();
                    tracker.addExpense(new Expense(amount, category, description));
                    break;
                case 2:
                    // Display all expenses
                    System.out.println("All Expenses:");
                    tracker.printExpenses();
                    break;
                case 3:
                    // Display total expenses
                    System.out.println("Total Expenses: " + tracker.getTotalExpenses());
                    break;
                case 4:
                    // Display total expenses for a specific category
                    String cat=getValidCategory(tracker, sc);
                    System.out.println("Total for " + cat + ": " + tracker.getTotalByCategory(cat));
                    break;
                case 5:
                    // Save expenses to a specified file
                    System.out.print("Enter filename to save: ");
                    String saveFilename = sc.nextLine();
                    tracker.saveToFile(saveFilename);
                    break;
           
                case 6:
                    // Exit the application
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 6);

        
        sc.close();
    }
     // Method to handle input for the amount with exception handling
     private static double getAmountInput(Scanner scanner) {
        double amount = 0.0;
        while (true) {
            System.out.print("Enter amount: ");
            try {
                amount = Double.parseDouble(scanner.nextLine());
                break;  // Exit loop if input is valid
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value for the amount.");
            }
        }
        return amount;
    }
     // Method to get and validate category input that exists in the expense list
    private static String getValidCategory(ExpenseTracker tracker, Scanner scanner) {
        while (true) {
            System.out.print("Enter category: ");
            final String category = scanner.nextLine();

            // Check if the entered category exists in the expenses list
            boolean categoryExists = tracker.getExpenses().stream()
                .anyMatch(expense -> expense.getCategory().equalsIgnoreCase(category));

            if (categoryExists) {
                return category;  // Valid category found
            } else {
                System.out.println("Invalid category. Please enter a category that exists in the expense list.");
            }
        }
    }
      
}
