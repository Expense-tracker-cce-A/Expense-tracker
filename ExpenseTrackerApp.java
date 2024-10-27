import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseTrackerApp extends Application {

    // The main tracker that holds expenses
    private ExpenseTracker tracker = new ExpenseTracker();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Expense Tracker"); // Set the title of the window
        // Set up the main layout and form components
        VBox layout = new VBox(10); // Vertical layout with 10px spacing
        // Labels and input fields for expense description and amount
        Label descriptionLabel = new Label("Description:");
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Enter Description"); // Placeholder text
        Label amountLabel = new Label("Amount:");
        TextField amountField = new TextField();
        amountField.setPromptText("Enter Amount"); // Placeholder text
        // Dropdown menu to select expense category
        ComboBox<ExpenseCategory> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll(ExpenseCategory.values());
         // Buttons for adding expense, saving, loading, and viewing totals
        Button addButton = new Button("Add Expense");
        Button saveButton = new Button("Save to File");
        Button loadButton = new Button("Load from File");
        Button totalButton = new Button("Show Total Expenses");
        Button categoryTotalButton = new Button("Show Category Total");
         // Label to display the total expenses
        Label totalLabel = new Label("Total Expenses: 0.00");
         // Text area to display the list of all expenses
        TextArea expenseListArea = new TextArea();
        expenseListArea.setEditable(false);  // Make the text area read-only
 // Event handler for adding a new expense
        addButton.setOnAction(e -> {
            try {
                String description = descriptionField.getText();  // Get description text
                double amount = Double.parseDouble(amountField.getText()); // Parse amount as a double
                ExpenseCategory category = categoryComboBox.getValue(); // Get selected category
                 // Create a new expense with current date and add it to tracker
                Expense expense = new Expense(description, amount, category, LocalDate.now());
                tracker.addExpense(expense);
                 // Update the expense list display and clear input fields
                updateExpenseList(expenseListArea);
                clearInputFields(descriptionField, amountField, categoryComboBox);
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter a valid amount.");
            }
        });
        // Event handler to display total expenses in the totalLabel
                totalButton.setOnAction(e -> updateTotalLabel(totalLabel));
        // Event handler to display total expenses for a selected category
                categoryTotalButton.setOnAction(e -> {
                ExpenseCategory category = categoryComboBox.getValue();
                if (category != null) {
                    double categoryTotal = tracker.getTotalByCategory(category);
        showAlert("Category Total", "Total for " + category + ": " + String.format("%.2f", categoryTotal));
    } else {
        showAlert("No Category Selected", "Please select a category to view the total.");
    }
});
                // Event handlers for saving and loading expenses from a file
        saveButton.setOnAction(e -> saveToFile("expenses.csv"));
        loadButton.setOnAction(e -> loadFromFile("expenses.csv", expenseListArea));
        
// Add all UI elements to the layout
        layout.getChildren().addAll(descriptionLabel,descriptionField,amountLabel, amountField, categoryComboBox, addButton, saveButton, loadButton,totalButton,categoryTotalButton,totalLabel, expenseListArea);
        Scene scene = new Scene(layout, 600, 600); // Create and set up the scene
        primaryStage.setScene(scene);
        primaryStage.show();
    }
 // Updates the expense list area with current expenses
    private void updateExpenseList(TextArea expenseListArea) {
        expenseListArea.setText(tracker.getExpenses().stream()
                .map(Expense::toString)
                .collect(Collectors.joining("\n")));
    }
        // Updates the total expenses label
    private void updateTotalLabel(Label totalLabel) {
    double totalExpenses = tracker.getTotalExpenses();
    totalLabel.setText("Total Expenses: " + String.format("%.2f", totalExpenses));
}
     // Clears input fields after adding an expense
    private void clearInputFields(TextField descriptionField, TextField amountField, ComboBox<ExpenseCategory> categoryComboBox) {
        descriptionField.clear();
        amountField.clear();
        categoryComboBox.setValue(null);
    }
  // Shows an alert dialog with a title and message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }
// Saves expenses to a CSV file
    private void saveToFile(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (Expense expense : tracker.getExpenses()) {
                writer.write(String.format("%s,%s,%.2f,%s\n",
                        expense.getDescription(), expense.getCategory(), expense.getAmount(), expense.getDate()));
            }
        } catch (IOException e) {
            showAlert("Error", "Error saving to file: " + e.getMessage());
        }
    }
 // Loads expenses from a CSV file
    private void loadFromFile(String filename, TextArea expenseListArea) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            tracker.clearExpenses(); // Clear existing expenses before loading
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Expense expense = new Expense(parts[0], Double.parseDouble(parts[2]), ExpenseCategory.valueOf(parts[1]), LocalDate.parse(parts[3]));
                tracker.addExpense(expense);
            }
            updateExpenseList(expenseListArea);
        } catch (IOException e) {
            showAlert("Error", "Error loading from file: " + e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "Error processing the file: " + e.getMessage());
        }
    }

    // Inner classes for Expense, ExpenseCategory, and ExpenseTracker
    public static class Expense {   // Represents an individual expense with description, amount, category, and date
        private String description;
        private double amount;
        private ExpenseCategory category;
        private LocalDate date;

        public Expense(String description, double amount, ExpenseCategory category, LocalDate date) {
            this.description = description;
            this.amount = amount;
            this.category = category;
            this.date = date;
        }

        public String getDescription() {
            return description;
        }

        public double getAmount() {
            return amount;
        }

        public ExpenseCategory getCategory() {
            return category;
        }

        public LocalDate getDate() {
            return date;
        }

        @Override
        public String toString() {
            return String.format("%s: %.2f [%s] on %s", description, amount, category, date);
        }
    }
// Enum representing different expense categories
    public enum ExpenseCategory {
        FOOD, TRANSPORT, ENTERTAINMENT, UTILITIES, OTHER
    }
// Class to manage the list of expenses
    public static class ExpenseTracker {
        private List<Expense> expenses;

        public ExpenseTracker() {
            expenses = new ArrayList<>();
        }

        public void addExpense(Expense expense) {
            expenses.add(expense);
        }

        public List<Expense> getExpenses() {
            return expenses;
        }

        public double getTotalExpenses() {
            return expenses.stream().mapToDouble(Expense::getAmount).sum();
        }
 // Calculates total amount spent in a specific category
        public double getTotalByCategory(ExpenseCategory category) {
            return expenses.stream()
                    .filter(expense -> expense.getCategory() == category)
                    .mapToDouble(Expense::getAmount) 
                    .sum();
        }

        public void clearExpenses() {
            expenses.clear();
        }
    }
}
