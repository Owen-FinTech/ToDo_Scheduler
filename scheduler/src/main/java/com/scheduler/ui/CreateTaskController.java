package com.scheduler.ui;

import java.time.LocalDate;

import com.scheduler.model.Task;
import com.scheduler.App;
import com.scheduler.db.dao.TaskDAO;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateTaskController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker dueDatePicker;
    @FXML private TextField startTimeField;
    @FXML private CheckBox priorityCheckBox;
    @FXML private CheckBox completedCheckBox;
    @FXML private ComboBox<String> recurrenceComboBox;
    @FXML private TextField categoryField;
    
    @FXML
    private void initialize() {
        recurrenceComboBox.getSelectionModel().selectFirst(); // selects "None" (or whichever is first)
    }
    
    @FXML
    public void handleCreate() {
        // Create a new Task object and set its fields.
        Task newTask = new Task();
        newTask.setUserId(App.currentUser.getId());
        newTask.setTitle(titleField.getText());
        newTask.setDescription(descriptionArea.getText());
        
        LocalDate dueDate = dueDatePicker.getValue();
        newTask.setDueDate(dueDate != null ? dueDate.toString() : "");
        
        newTask.setStartTime(startTimeField.getText()); 
        newTask.setPriority(priorityCheckBox.isSelected() ? "True" : "False");
        newTask.setStatus(completedCheckBox.isSelected() ? "True" : "False");

        newTask.setRecurrence(recurrenceComboBox.getValue());
        newTask.setCategory(categoryField.getText());
        
        TaskDAO.addTask(newTask);
        
        // Close the pop-up window.
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleCancel() {
        // Simply close the window without saving.
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}
