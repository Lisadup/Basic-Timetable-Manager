package com.example.timetablemanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText taskInput, dateInput, timeInput;
    private Button addTaskButton;
    private ListView taskListView;

    private ArrayList<String> taskList;
    private ArrayAdapter<String> taskAdapter;

    private Calendar calendar;

    private int editingTaskIndex = -1;  // Track the index of the task being edited

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind UI components to their IDs
        taskInput = findViewById(R.id.taskInput);
        dateInput = findViewById(R.id.dateInput);
        timeInput = findViewById(R.id.timeInput);
        addTaskButton = findViewById(R.id.addTaskButton);
        taskListView = findViewById(R.id.taskListView);


        // Initialize the task list and adapter
        taskList = new ArrayList<>();
        taskAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskList);
        taskListView.setAdapter(taskAdapter);

        // Initialize the calendar instance
        calendar = Calendar.getInstance();

        // Set up Date Picker dialog for date input
        dateInput.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) ->
                            dateInput.setText(getString(R.string.date_format, dayOfMonth, monthOfYear + 1, year1)),
                    year, month, day);
            datePickerDialog.show();
        });

        // Set up Time Picker dialog for time input
        timeInput.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                    (view, hourOfDay, minute1) ->
                            timeInput.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1)),
                    hour, minute, true);
            timePickerDialog.show();
        });

        // Set onClickListener to add or update task with confirmation dialog
        addTaskButton.setOnClickListener(v -> {
            String task = taskInput.getText().toString();
            String date = dateInput.getText().toString();
            String time = timeInput.getText().toString();

            if (!task.isEmpty() && !date.isEmpty() && !time.isEmpty()) {
                String taskDetails = getString(R.string.task_details_format, task, date, time);
                if (editingTaskIndex == -1) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.add_task_title)
                            .setMessage(R.string.add_task_message)
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                taskList.add(taskDetails);
                                taskAdapter.notifyDataSetChanged();
                                clearInputFields();
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.update_task_title)
                            .setMessage(R.string.update_task_message)
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                taskList.set(editingTaskIndex, taskDetails);
                                taskAdapter.notifyDataSetChanged();
                                editingTaskIndex = -1;
                                addTaskButton.setText(R.string.add_task_button);
                                clearInputFields();
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                }
            } else {
                Toast.makeText(MainActivity.this, R.string.enter_all_details, Toast.LENGTH_SHORT).show();
            }
        });

        // Handle editing tasks on item click
        taskListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTask = taskList.get(position);
            String[] taskParts = selectedTask.split(" - Due: ");
            String taskName = taskParts[0];
            String[] dateTimeParts = taskParts[1].split(" at ");
            String taskDate = dateTimeParts[0];
            String taskTime = dateTimeParts[1];

            // Fill input fields with task details
            taskInput.setText(taskName);
            dateInput.setText(taskDate);
            timeInput.setText(taskTime);

            editingTaskIndex = position;  // Set the index of the task being edited
            addTaskButton.setText(R.string.update_task_button);  // Change button text to "Update Task"
        });

        // Handle deleting tasks on item long click with confirmation dialog
        taskListView.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.delete_task_title)
                    .setMessage(R.string.delete_task_message)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        taskList.remove(position);
                        taskAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        });
    }

    // Helper method to clear input fields
    private void clearInputFields() {
        taskInput.setText("");
        dateInput.setText("");
        timeInput.setText("");
    }
}
