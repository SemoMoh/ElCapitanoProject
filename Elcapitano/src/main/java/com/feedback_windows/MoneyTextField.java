package com.feedback_windows;

import javafx.scene.control.TextField;

public class MoneyTextField extends TextField {

    public MoneyTextField() {
        setPromptText("Enter return amount");
    }

    public boolean isValid() {
        // Implement your validation logic here
        // For simplicity, assuming any non-empty value is valid
        return !getText().trim().isEmpty();
    }
}
