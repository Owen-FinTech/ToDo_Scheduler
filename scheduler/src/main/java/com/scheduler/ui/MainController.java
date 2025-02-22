package com.scheduler.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

import com.scheduler.App;
import com.scheduler.db.dao.TaskDAO;
import com.scheduler.model.Task;

public class MainController {

    // Filter buttons 
    @FXML private RadioButton allButton;
    @FXML private RadioButton overdueButton;
    @FXML private RadioButton completedButton;
    @FXML private RadioButton priorityButton;
    
    // Calendar grid (to be populated with day nodes)
    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;
    
    // TableView and its columns for tasks
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> nameColumn;
    @FXML private TableColumn<Task, String> dateColumn;
    @FXML private TableColumn<Task, String> timeColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, String> completedColumn;
    @FXML private TableColumn<Task, String> recurrenceColumn;
    @FXML private TableColumn<Task, String> categoryColumn;

    private ToggleGroup filterGroup = new ToggleGroup();
    private Integer selectedDay = null;
    private YearMonth selectedYearMonth = null;
    private YearMonth currentYearMonth = YearMonth.now();
    
    // Initialization method; this is called automatically after the FXML is loaded
    @FXML
    private void initialize() {
        // Set up the TableView columns by wrapping Task getters in JavaFX properties

        allButton.setToggleGroup(filterGroup);
        overdueButton.setToggleGroup(filterGroup);
        completedButton.setToggleGroup(filterGroup);
        priorityButton.setToggleGroup(filterGroup);
        
        // Start with no filter selected.
        filterGroup.selectToggle(allButton);

        selectedDay = null;
        clearCalendarSelection();
        refreshTaskList();
        
        filterGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Clear any calendar day selection so that the date filter doesn't override the radio filter.
                selectedDay = null;
                clearCalendarSelection();
                refreshTaskList();
            }
        });
        
        // Set up the calendar and task table as before
        setupCalendarGrid();
        updateCalendar();
        populateCalendar(YearMonth.now());

        // For the "Name" column, display the task's title.
        nameColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getTitle()));
        
        // For the "Date" column, display the task's due date.
        dateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getDueDate()));
        
        // For the "Time" column, display the task's start time.
        timeColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getStartTime()));
        
        // For the "Priority" column, display the task's priority.
        priorityColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getPriority()));
        
        // For the "Completed" column, assume that if the status equals "Completed" (case-insensitive), it's true.
        completedColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getStatus()));

        recurrenceColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getRecurrence()));

        categoryColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getCategory()));
    
    }
    
    @FXML
    private void handleNewTask() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/createTask.fxml"));
            Parent root = loader.load();

            // Get the controller from the pop-up

            Stage stage = new Stage();
            stage.setTitle("Create New Task");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Load tasks from the database via DAO.
            refreshTaskList();
         
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            System.out.println("No task selected for update.");
            return;
        }
        
        try {
            // Load the updateTask.fxml file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updateTask.fxml"));
            Parent root = loader.load();
            
            // Get the controller and pass the selected task to it.
            UpdateTaskController controller = loader.getController();
            controller.setTask(selectedTask);
            
            // Create a new Stage for the pop-up.
            Stage stage = new Stage();
            stage.setTitle("Update Task");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Load tasks from the database via DAO.
            refreshTaskList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    @FXML
    private void handleDeleteTask() {
        // Get the selected task from the TableView.
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            System.out.println("No task selected for deletion.");
            return;
        }
        
        // Show a confirmation dialog.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Task");
        alert.setHeaderText("Confirm Deletion");
        alert.setContentText("Are you sure you want to delete the task: " + selectedTask.getTitle() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Delete the task from the database using DAO.
            TaskDAO taskDAO = new TaskDAO();
            taskDAO.deleteTask(selectedTask);
            System.out.println("Task deleted: " + selectedTask.getTitle());
            
        }
        refreshTaskList();
    }

    private void populateCalendar(YearMonth yearMonth) {
        // Clear any existing content
        calendarGrid.getChildren().clear();
        
        // Optionally, add headers for days of the week.
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        for (int i = 0; i < dayNames.length; i++) {
            Label dayLabel = new Label(dayNames[i]);
            dayLabel.setPrefWidth(40);
            // Add header labels in the first row.
            calendarGrid.add(dayLabel, i, 0);
        }
        
        // Get the first day of the month
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        // Java's DayOfWeek uses Monday=1 ... Sunday=7.
        int dayOfWeekOfFirst = firstDayOfMonth.getDayOfWeek().getValue();
        int column = dayOfWeekOfFirst % 7; // Converts Sunday (7) to 0, Monday (1) to 1, etc.
        int row = 1; // Start on row 1 since row 0 has the headers.
        
        int daysInMonth = yearMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            Button dayButton = new Button(String.valueOf(day));
            dayButton.setPrefWidth(40);
            dayButton.setPrefHeight(40);
            
            // If this day matches the stored selection for the displayed month/year, highlight it.
            if (selectedDay != null && selectedYearMonth != null &&
                yearMonth.equals(selectedYearMonth) && day == selectedDay) {
                dayButton.getStyleClass().add("selected-day");
            }
            
            // Add event handler for day selection.
            dayButton.setOnAction(e -> {
                // When a day is selected, clear any filter selection.
                filterGroup.selectToggle(null);
                
                // Record the selection.
                selectedDay = Integer.valueOf(dayButton.getText());
                selectedYearMonth = yearMonth;
                
                // Update visual selection.
                clearCalendarSelection();
                dayButton.getStyleClass().add("selected-day");

                refreshTaskList();
                
                System.out.println("Selected day: " + dayButton.getText());
                
            });
            
            calendarGrid.add(dayButton, column, row);
            
            column++;
            if (column == 7) {
                column = 0;
                row++;
            }
        }
    }

    private void updateCalendar() {
        // Update the label with the current month and year.
        monthYearLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
        // Populate the calendar grid with the days for the current month.
        populateCalendar(currentYearMonth);
    }

    private void setupCalendarGrid() {
        // Clear any existing column constraints
        calendarGrid.getColumnConstraints().clear();

        // Create 7 columns (for the 7 days of the week)
        for (int i = 0; i < 7; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            // Set a minimum width that can accommodate double-digit day numbers
            colConstraints.setMinWidth(40);  
            // Optionally, set a preferred width too
            colConstraints.setPrefWidth(50);
            // Allow columns to grow if space permits
            colConstraints.setHgrow(Priority.ALWAYS);
            calendarGrid.getColumnConstraints().add(colConstraints);
        }
    }

    private void clearCalendarSelection() {
        // Remove the "selected-day" style from all children of the calendarGrid.
        calendarGrid.getChildren().stream()
            .filter(node -> node instanceof Button)
            .forEach(node -> node.getStyleClass().remove("selected-day"));
    }
    

    // Event Handlers for Month/Year navigation:
    @FXML
    private void handlePreviousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar();
    }
    
    @FXML
    private void handleNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar();
    }
    
    @FXML
    private void handlePreviousYear() {
        currentYearMonth = currentYearMonth.minusYears(1);
        updateCalendar();
    }
    
    @FXML
    private void handleNextYear() {
        currentYearMonth = currentYearMonth.plusYears(1);
        updateCalendar();
    }

    @FXML
    public void refreshTaskList() {
        int currentUserId = App.currentUser.getId();
        TaskDAO taskDAO = new TaskDAO();
        ObservableList<Task> allTasks = FXCollections.observableArrayList(taskDAO.getAllTasks(currentUserId));

        // If the "All" filter is selected, show all tasks regardless of calendar selection.
        if (filterGroup.getSelectedToggle() == allButton) {
            taskTable.setItems(allTasks);
        } else if (selectedDay != null && selectedYearMonth != null) {
            // If a calendar day is selected (and a filter other than All isn't selected), filter by that date.
            String selectedDate = selectedYearMonth.atDay(selectedDay).toString();
            ObservableList<Task> dateTasks = allTasks.filtered(task -> task.getDueDate().startsWith(selectedDate));
            taskTable.setItems(dateTasks);
        } else if (filterGroup.getSelectedToggle() == overdueButton) {
            ObservableList<Task> overdueTasks = allTasks.filtered(task -> {
                LocalDate due = LocalDate.parse(task.getDueDate().substring(0, 10));
                return due.isBefore(LocalDate.now());
            });
            taskTable.setItems(overdueTasks);
        } else if (filterGroup.getSelectedToggle() == completedButton) {
            ObservableList<Task> completedTasks = allTasks.filtered(task ->
                    "true".equalsIgnoreCase(task.getStatus()));
            taskTable.setItems(completedTasks);
        } else if (filterGroup.getSelectedToggle() == priorityButton) {
            ObservableList<Task> priorityTasks = allTasks.filtered(task ->
                    "true".equalsIgnoreCase(task.getPriority()));
            taskTable.setItems(priorityTasks);
        } else {
            // Fallback: show all tasks.
            taskTable.setItems(allTasks);
        }
    }

}
