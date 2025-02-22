package com.scheduler.ui;

import java.time.LocalDate;
import java.time.YearMonth;

import com.scheduler.db.dao.TaskDAO;
import com.scheduler.model.Task;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateTaskController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker dueDatePicker;
    @FXML private TextField startTimeField;
    @FXML private CheckBox priorityCheckBox;
    @FXML private CheckBox completedCheckBox;
    @FXML private ComboBox<String> recurrenceComboBox;
    @FXML private TextField categoryField;
    
    private Task taskToUpdate; // The task being updated

    // This method is called by the main controller after loading the update pop-up.
    public void setTask(Task task) {
        this.taskToUpdate = task;
        System.out.println("Updating task with id: " + task.getId());
        populateFields();
    }
    
    private void populateFields() {
        // Pre-populate the fields with the task's current values.
        titleField.setText(taskToUpdate.getTitle());
        descriptionArea.setText(taskToUpdate.getDescription());
        
        // Convert the stored due date string to LocalDate if possible.
        if (!taskToUpdate.getDueDate().isEmpty()) {
            dueDatePicker.setValue(LocalDate.parse(taskToUpdate.getDueDate()));
        }
        
        startTimeField.setText(taskToUpdate.getStartTime());
        priorityCheckBox.setSelected(Boolean.parseBoolean(taskToUpdate.getPriority()));
        completedCheckBox.setSelected(Boolean.parseBoolean(taskToUpdate.getStatus()));
        recurrenceComboBox.setValue(taskToUpdate.getRecurrence());
        categoryField.setText(taskToUpdate.getCategory());
    }
    
    @FXML
    private void handleUpdate() {
        // Update the task object with the new values from the form.
        taskToUpdate.setTitle(titleField.getText());
        taskToUpdate.setDescription(descriptionArea.getText());
        LocalDate dueDate = dueDatePicker.getValue();
        taskToUpdate.setDueDate(dueDate != null ? dueDate.toString() : "");
        taskToUpdate.setStartTime(startTimeField.getText());
        taskToUpdate.setPriority(priorityCheckBox.isSelected() ? "True" : "False");
        taskToUpdate.setStatus(completedCheckBox.isSelected() ? "True" : "False");
        taskToUpdate.setRecurrence(recurrenceComboBox.getValue());
        taskToUpdate.setCategory(categoryField.getText());

        if (!taskToUpdate.getRecurrence().equals("None") && taskToUpdate.getStatus().equals("True")) {
            // Mark the current task as uncompleted for the new cycle.
            taskToUpdate.setStatus("False");
        
            if (taskToUpdate.getRecurrence().equals("Daily")) {
                taskToUpdate.setDueDate(getNextDailyOccurrence(dueDate).toString());
            }
            else if (taskToUpdate.getRecurrence().equals("Weekly")) {
                taskToUpdate.setDueDate(getNextWeeklyOccurrence(dueDate).toString());
            }
            else if (taskToUpdate.getRecurrence().equals("Fortnightly")) {
                taskToUpdate.setDueDate(getNextFortnightlyOccurrence(dueDate).toString());
            }
            else {
                taskToUpdate.setDueDate(getNextMonthlyOccurrence(dueDate).toString());
            }
        }        
        
        // Call DAO to update the task in the database.
        TaskDAO taskDAO = new TaskDAO();
        taskDAO.updateTask(taskToUpdate);
        
        System.out.println("Task updated: " + taskToUpdate.getTitle());
        
        // Close the pop-up window.
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    public LocalDate getNextMonthlyOccurrence(LocalDate currentDate) {
        YearMonth nextMonth = YearMonth.from(currentDate).plusMonths(1);
        int day = currentDate.getDayOfMonth();
        int lastDayOfNext = nextMonth.lengthOfMonth();
        int newDay = Math.min(day, lastDayOfNext);
        return LocalDate.of(nextMonth.getYear(), nextMonth.getMonth(), newDay);
    }

    public LocalDate getNextWeeklyOccurrence(LocalDate currentDate) {
        return currentDate.plusDays(7);
    }

    public LocalDate getNextFortnightlyOccurrence(LocalDate currentDate) {
        return currentDate.plusDays(14);
    }

    public LocalDate getNextDailyOccurrence(LocalDate currentDate) {
        return currentDate.plusDays(1);
    }
}

