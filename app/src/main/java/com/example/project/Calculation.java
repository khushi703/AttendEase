package com.example.project;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Calculation extends AppCompatActivity {

    private TextView todaysAttendanceTextView;
    private TextView overallAttendanceTextView;
    private TextView bunkFor80PercentTextView;
    private TextView bunkFor75PercentTextView;
    private TextView attendFor80PercentTextView;
    private TextView attendFor75PercentTextView;

    // Store selected date (default is today)
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculation);

        todaysAttendanceTextView = findViewById(R.id.todaysAttendanceTextView);
        overallAttendanceTextView = findViewById(R.id.overallAttendanceTextView);
        bunkFor80PercentTextView = findViewById(R.id.bunkFor80PercentTextView);
        bunkFor75PercentTextView = findViewById(R.id.bunkFor75PercentTextView);
//        attendFor80PercentTextView = findViewById(R.id.attendFor80PercentTextView);
//        attendFor75PercentTextView = findViewById(R.id.attendFor75PercentTextView);

        // Initialize with today's date
        selectedDate = getCurrentDate();

        // Fetch attendance data for the initial date (today)
        fetchAttendanceData(selectedDate);
    }

    // Method to set a new date dynamically
    public void setNewDate(String newDate) {
        this.selectedDate = newDate; // Update the selected date
        clearPreviousData(); // Clear previous data before fetching new data
        fetchAttendanceData(selectedDate); // Fetch attendance for the new date
    }

    // Method to clear previous data in the TextViews
    private void clearPreviousData() {
        todaysAttendanceTextView.setText(""); // Clear today's attendance
        overallAttendanceTextView.setText(""); // Clear overall attendance
        bunkFor80PercentTextView.setText(""); // Clear 80% bunk info
        bunkFor75PercentTextView.setText(""); // Clear 75% bunk info
        attendFor80PercentTextView.setText(""); // Clear 80% attend info
        attendFor75PercentTextView.setText(""); // Clear 75% attend info
    }

    private void fetchAttendanceData(String date) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userEmail = user.getEmail().replace(".", ",");
            DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference("Attendance").child(userEmail);

            attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int totalLectures = 0;
                    int attendedLectures = 0;
                    int lecturesOnSelectedDate = 0;
                    int attendedOnSelectedDate = 0;

                    // Iterate through each date in Firebase
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        String dateInDatabase = dateSnapshot.getKey();

                        // Check if the selected date matches the current date being iterated
                        boolean isSelectedDate = selectedDate.equals(dateInDatabase);

                        for (DataSnapshot lectureSnapshot : dateSnapshot.getChildren()) {
                            Boolean attended = lectureSnapshot.getValue(Boolean.class);

                            totalLectures++;
                            if (attended != null && attended) {
                                attendedLectures++;
                            }

                            if (isSelectedDate) {
                                lecturesOnSelectedDate++;
                                if (attended != null && attended) {
                                    attendedOnSelectedDate++;
                                }
                            }
                        }
                    }

                    // Calculate selected day's attendance percentage
                    double selectedDateAttendance = (lecturesOnSelectedDate > 0) ? (attendedOnSelectedDate * 100.0 / lecturesOnSelectedDate) : 0;
                    todaysAttendanceTextView.setText(selectedDate + "'s Attendance: " + String.format("%.2f", selectedDateAttendance) + "%");

                    // Calculate overall attendance percentage
                    double overallAttendance = (totalLectures > 0) ? (attendedLectures * 100.0 / totalLectures) : 0;
                    overallAttendanceTextView.setText("Overall Attendance: " + String.format("%.2f", overallAttendance) + "%");

//                    // Calculate lectures to attend and bunk for both 75% and 80%
//                    int lecturesToAttendFor75 = calculateAttendFor75(attendedLectures, totalLectures);
//                    int lecturesToAttendFor80 = calculateAttendFor80(attendedLectures, totalLectures);

                    int lecturesToBunkFor75 = calculateBunkFor75(attendedLectures, totalLectures);
                    int lecturesToBunkFor80 = calculateBunkFor80(attendedLectures, totalLectures);

//                    // Set TextViews
//                    attendFor75PercentTextView.setText("Lectures needed to attend for 75%: " + lecturesToAttendFor75);
//                    attendFor80PercentTextView.setText("Lectures needed to attend for 80%: " + lecturesToAttendFor80);
//
//                    bunkFor75PercentTextView.setText("Lectures you can bunk for 75%: " + lecturesToBunkFor75);
//                    bunkFor80PercentTextView.setText("Lectures you can bunk for 80%: " + lecturesToBunkFor80);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Failed to read attendance data", error.toException());
                }
            });
        }
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
    }

//    // Method to calculate how many lectures the user needs to attend to maintain 75% attendance
//    private int calculateAttendFor75(int attendedLectures, int totalLectures) {
//        int neededLectures = 0;
//        double currentAttendance = (totalLectures > 0) ? (attendedLectures * 100.0 / totalLectures) : 0;
//
//        while (currentAttendance < 75.0) {
//            totalLectures++;
//            attendedLectures++;
//            currentAttendance = (attendedLectures * 100.0 / totalLectures);
//            neededLectures++;
//        }
//        return neededLectures;
//    }
//
//    // Method to calculate how many lectures the user needs to attend to maintain 80% attendance
//    private int calculateAttendFor80(int attendedLectures, int totalLectures) {
//        int neededLectures = 0;
//        double currentAttendance = (totalLectures > 0) ? (attendedLectures * 100.0 / totalLectures) : 0;
//
//        while (currentAttendance < 80.0) {
//            totalLectures++;
//            attendedLectures++;
//            currentAttendance = (attendedLectures * 100.0 / totalLectures);
//            neededLectures++;
//        }
//        return neededLectures;
//    }

    // Method to calculate how many lectures the user can bunk to maintain 75% attendance
    private int calculateBunkFor75(int attendedLectures, int totalLectures) {
        int possibleBunks = 0;
        double currentAttendance = (totalLectures > 0) ? (attendedLectures * 100.0 / totalLectures) : 0;

        while (currentAttendance >= 75.0) {
            totalLectures++;
            currentAttendance = (attendedLectures * 100.0 / totalLectures);
            possibleBunks++;
        }

        // If no lectures can be bunked (bunks would result in < 75% attendance), return 0
        return Math.max(possibleBunks - 1, 0);
    }


    // Method to calculate how many lectures the user can bunk to maintain 80% attendance
    private int calculateBunkFor80(int attendedLectures, int totalLectures) {
        int possibleBunks = 0;
        double currentAttendance = (totalLectures > 0) ? (attendedLectures * 100.0 / totalLectures) : 0;

        while (currentAttendance >= 80.0) {
            totalLectures++;
            currentAttendance = (attendedLectures * 100.0 / totalLectures);
            possibleBunks++;
        }

        // If no lectures can be bunked (bunks would result in < 80% attendance), return 0
        return Math.max(possibleBunks - 1, 0);
    }

}