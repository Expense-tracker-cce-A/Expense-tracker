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
        primaryStage.setTitle("Expense Tracker");

        VBox layout = new VBox(10);
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        TextField amountField = new TextField();
        amountField.setPromptText("Amount");
        ComboBox<ExpenseCategory> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll(ExpenseCategory.values());
        Button addButton = new Button("Add Expense");
        Button saveButton = new Button("Save to File");
        Button loadButton = new Button("Load from File");
        TextArea expenseListArea = new TextArea();
        expenseListArea.setEditable(false);

        addButton.setOnAction(e -> {
            try {
                String description = descriptionField.getText();
                double amount = Double.parseDouble(amountField.getText());
                ExpenseCategory category = categoryComboBox.getValue();
                Expense expense = new Expense(description, amount, category, LocalDate.now());
                tracker.addExpense(expense);
                updateExpenseList(expenseListArea);
                clearInputFields(descriptionField, amountField, categoryComboBox);
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter a valid amount.");
            }
        });

        saveButton.setOnAction(e -> saveToFile("expenses.csv"));
        loadButton.setOnAction(e -> loadFromFile("expenses.csv", expenseListArea));

        layout.getChildren().addAll(descriptionField, amountField, categoryComboBox, addButton, saveButton, loadButton, expenseListArea);
        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateExpenseList(TextArea expenseListArea) {
        expenseListArea.setText(tracker.getExpenses().stream()
                .map(Expense::toString)
                .collect(Collectors.joining("\n")));
    }

    private void clearInputFields(TextField descriptionField, TextField amountField, ComboBox<ExpenseCategory> categoryComboBox) {
        descriptionField.clear();
        amountField.clear();
        categoryComboBox.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }

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
    public static class Expense {
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

    public enum ExpenseCategory {
        FOOD, TRANSPORT, ENTERTAINMENT, UTILITIES, OTHER
    }

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

        public List<Expense> getExpensesByCategory(ExpenseCategory category) {
            return expenses.stream()
                    .filter(expense -> expense.getCategory() == category)
                    .collect(Collectors.toList());
        }

        public void clearExpenses() {
            expenses.clear();
        }
    }
}
