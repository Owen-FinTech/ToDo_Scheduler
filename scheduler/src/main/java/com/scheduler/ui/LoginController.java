package com.scheduler.ui;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.scheduler.App;
import com.scheduler.db.dao.UserDAO;
import com.scheduler.model.User;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    // Assuming you have a UserDAO with a method to get a user by username.
    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleRegisterNavigation() {
        try {
            App.setRoot("/register");  // This loads register.fxml
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }
        
        // Retrieve user from the database using DAO.
        User user = userDAO.getUserByUsername(username);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        if (user != null && encoder.matches(password, user.getPassword())) {
            // Successful login
            errorLabel.setText(""); // Clear any error messages
            try {
                // Switch to the primary screen, e.g., the dashboard.
                // App.setRoot() is assumed to change the scene.
                App.currentUser = user;
                App.setRoot("/main");
            } catch (Exception e) {
                errorLabel.setText("Error loading main screen.");
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid username or password.");
        }
    }
}
