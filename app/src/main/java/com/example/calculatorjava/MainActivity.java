package com.example.calculatorjava;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private TextView historyDisplay; // For showing history
    private String currentDisplay = "";
    private double firstNumber = 0;
    private String operator = "";
    private boolean isOperatorClicked = false;
    private boolean isChainedCalculation = false;

    // ArrayList to store the history of calculations
    private ArrayList<String> history = new ArrayList<>();
    private StringBuilder historyBuilder = new StringBuilder(); // For building history display text


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);
        historyDisplay = findViewById(R.id.history); // Initialize the history display

        // Set up button listeners
        setButtonListeners();
    }

    private void setButtonListeners() {


        findViewById(R.id.btn0).setOnClickListener(v -> appendNumber("0"));
        findViewById(R.id.btn1).setOnClickListener(v -> appendNumber("1"));
        findViewById(R.id.btn2).setOnClickListener(v -> appendNumber("2"));
        findViewById(R.id.btn3).setOnClickListener(v -> appendNumber("3"));
        findViewById(R.id.btn4).setOnClickListener(v -> appendNumber("4"));
        findViewById(R.id.btn5).setOnClickListener(v -> appendNumber("5"));
        findViewById(R.id.btn6).setOnClickListener(v -> appendNumber("6"));
        findViewById(R.id.btn7).setOnClickListener(v -> appendNumber("7"));
        findViewById(R.id.btn8).setOnClickListener(v -> appendNumber("8"));
        findViewById(R.id.btn9).setOnClickListener(v -> appendNumber("9"));

        // Example for operation buttons
        findViewById(R.id.btn_add).setOnClickListener(v -> setOperator("+"));
        findViewById(R.id.btn_subtract).setOnClickListener(v -> setOperator("-"));
        findViewById(R.id.btn_multiply).setOnClickListener(v -> setOperator("x"));
        findViewById(R.id.btn_divide).setOnClickListener(v -> setOperator("÷"));

        // Equals button
        findViewById(R.id.btn_equals).setOnClickListener(v -> calculateResult());

        // AC button for clearing everything
        findViewById(R.id.btn_ac).setOnClickListener(v -> clearAll());

        // +/- button for toggling the sign
        findViewById(R.id.btn_plusminus).setOnClickListener(v -> toggleSign());

        findViewById(R.id.btn_dot).setOnClickListener(v -> appendDot());

        // Backspace button for deleting the last character
        findViewById(R.id.btn_back).setOnClickListener(v -> backspace());
    }

    private void appendNumber(String number) {
        if (isOperatorClicked) {
            currentDisplay = ""; // Clear current display if an operator was clicked
            isOperatorClicked = false; // Reset the flag
        }
        currentDisplay += number; // Append new number
        display.setText(currentDisplay); // Update display
    }



    private void appendDot() {
        if (!currentDisplay.contains(".")) { // Check if a decimal point already exists
            currentDisplay += "."; // Append a decimal point
            display.setText(currentDisplay); // Update the display
        }
    }




    private void setOperator(String op) {
        if (!currentDisplay.isEmpty()) {
            // If this is the first operator, store the first number
            if (operator.isEmpty()) {
                firstNumber = Double.parseDouble(currentDisplay);
            } else {
                // If an operator was already clicked, calculate the intermediate result
                calculateIntermediateResult();
            }

            // Set the new operator and clear the current display for the next number
            operator = op;
            isOperatorClicked = true;
            currentDisplay = "";
            display.setText(removeTrailingZero(firstNumber) + " " + operator); // Show the first number and operator
        }
    }











    private void calculateResult() {
        if (!operator.isEmpty() && !currentDisplay.isEmpty()) {
            double secondNumber = Double.parseDouble(currentDisplay);  // Get the second number
            double result = 0;

            // Perform the operation based on the current operator
            switch (operator) {
                case "+":
                    result = firstNumber + secondNumber;
                    break;
                case "-":
                    result = firstNumber - secondNumber;
                    break;
                case "x":
                    result = firstNumber * secondNumber;
                    break;
                case "÷":
                    if (secondNumber != 0) {
                        result = firstNumber / secondNumber;
                    } else {
                        display.setText("Error");
                        return;
                    }
                    break;
            }

            // Correctly display the full calculation in history before updating firstNumber
            historyBuilder.append(removeTrailingZero(firstNumber))  // Use firstNumber, not result
                    .append(" ")  // Add a space
                    .append(operator)  // Use the current operator
                    .append(" ")  // Add a space
                    .append(removeTrailingZero(secondNumber))  // Second number
                    .append(" = ")
                    .append(removeTrailingZero(result));  // Result

            // Add to history
            addToHistory(historyBuilder.toString());
            historyBuilder.setLength(0); // Clear for the next calculation

            // Update the display with the result
            display.setText(removeTrailingZero(result));

            // Set the result as the new firstNumber for chaining calculations
            firstNumber = result;
            currentDisplay = String.valueOf(result);
            operator = "";  // Clear the operator for next input
            isChainedCalculation = true; // Set flag to allow chaining calculations
        }
    }





    private void calculateIntermediateResult() {
        if (!currentDisplay.isEmpty() && !operator.isEmpty()) {
            double secondNumber = Double.parseDouble(currentDisplay); // Get the second number
            double result = 0;

            // Perform the operation based on the current operator
            switch (operator) {
                case "+":
                    result = firstNumber + secondNumber;
                    break;
                case "-":
                    result = firstNumber - secondNumber;
                    break;
                case "x":
                    result = firstNumber * secondNumber;
                    break;
                case "÷":
                    if (secondNumber != 0) {
                        result = firstNumber / secondNumber;
                    } else {
                        display.setText("Error");
                        return;
                    }
                    break;
            }

            // Update history correctly for intermediate result, but avoid repeating the result twice
            if (isChainedCalculation) {
                // If we're chaining, just append the next operation and result
                historyBuilder.append(" ").append(operator).append(" ")
                        .append(removeTrailingZero(secondNumber)).append(" = ")
                        .append(removeTrailingZero(result));
            } else {
                // For first calculation, append full operation
                historyBuilder.append(removeTrailingZero(firstNumber))
                        .append(" ").append(operator).append(" ")
                        .append(removeTrailingZero(secondNumber)).append(" = ")
                        .append(removeTrailingZero(result));
            }

            // Update the history and clear builder for next input
            addToHistory(historyBuilder.toString());
            historyBuilder.setLength(0); // Clear for next round

            // Set the result as firstNumber for the next chain calculation
            firstNumber = result;
            currentDisplay = String.valueOf(result); // Store the result as the current display
            display.setText(removeTrailingZero(result)); // Show the result

            // Prepare for the next operator click
            operator = "";  // Clear the operator
            isChainedCalculation = true; // Mark that we are chaining calculations
        }
    }








    private void addToHistory(String calculation) {
        // Add calculation to the list and update the history display
        history.add(calculation);
        updateHistoryDisplay();
    }

    private void updateHistoryDisplay() {
        StringBuilder historyText = new StringBuilder();
        for (String record : history) {
            historyText.append(record).append("\n");
        }
        historyDisplay.setText(historyText.toString());
    }

    private void clearAll() {
        currentDisplay = "";
        firstNumber = 0;
        operator = "";
        isOperatorClicked = false;
        isChainedCalculation = false;
        display.setText("0");
        historyBuilder.setLength(0); // Clear history text builder
        history.clear(); // Clear the history list
        updateHistoryDisplay(); // Clear the history display
    }


    private void toggleSign() {
        if (!currentDisplay.isEmpty()) {
            double value = Double.parseDouble(currentDisplay);
            value = value * -1;
            currentDisplay = String.valueOf(value);
            display.setText(removeTrailingZero(value));
        }
    }


    private void backspace() {
        if (!currentDisplay.isEmpty()) {
            currentDisplay = currentDisplay.substring(0, currentDisplay.length() - 1);
            display.setText(currentDisplay.isEmpty() ? "0" : currentDisplay);
        }
    }

    // Helper method to remove trailing zeros from the result (e.g., 5.0 -> 5)
    private String removeTrailingZero(double number) {
        // Format number with commas
        NumberFormat numberFormat = NumberFormat.getInstance();
        String formattedNumber = numberFormat.format(number);

        // Check if the number is an integer and return appropriately
        if (number == (long) number) {
            return formattedNumber;  // Return formatted number as string
        } else {
            return String.format("%.2f", number);  // Keep two decimal places if it has decimals
        }
    }



}
