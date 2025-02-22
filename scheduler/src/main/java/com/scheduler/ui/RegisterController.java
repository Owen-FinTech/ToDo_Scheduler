package com.scheduler.ui;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import com.scheduler.App;
import com.scheduler.db.dao.UserDAO;
import com.scheduler.model.User;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel; // Label to display error messages

    // This will store the new user if registration is successful.
    private User newUser;

    @FXML
    private void initialize() {

    }

    @FXML
    private void handleRegisterButtonAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Clear previous error message
        errorLabel.setText("");

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Please fill in all required fields.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }
        if (!password.matches("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).{6,}$")) {
            errorLabel.setText("Password must be at least 6 characters and contain letters, numbers, and special characters.");
            return;
        }

        UserDAO userDAO = new UserDAO();
        // Check if username already exists
        User existingUser = userDAO.getUserByUsername(username);
        if (existingUser != null) {
            errorLabel.setText("Username already exists. Please choose a different one.");
            return;
        }

        // Create and add the user
        newUser = new User();
        newUser.setUsername(username);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);
        newUser.setPassword(hashedPassword);

        userDAO.addUser(newUser);
        System.out.println("User registered: " + newUser.getUsername());

        // Return to the login screen
        try {
            App.setRoot("/login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public User getNewUser() {
        return newUser;
    }
}
