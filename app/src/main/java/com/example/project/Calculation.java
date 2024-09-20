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
    private TextView attendFor75PercentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculation);

        todaysAttendanceTextView = findViewById(R.id.todaysAttendanceTextView);
        overallAttendanceTextView = findViewById(R.id.overallAttendanceTextView);
        bunkFor80PercentTextView = findViewById(R.id.bunkFor80PercentTextView);
        attendFor75PercentTextView = findViewById(R.id.attendFor75PercentTextView);

        fetchAttendanceData();
    }
    private void fetchAttendanceData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userEmail = user.getEmail().replace(".", ",");
            DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference("Attendance").child(userEmail);

            attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int totalLectures = 0;
                    int attendedLectures = 0;
                    int lecturesToday = 0;
                    int attendedToday = 0;

                    String todayDate = getCurrentDate();

                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        String date = dateSnapshot.getKey();

                        // Check if the date is today to calculate today's attendance
                        boolean isToday = todayDate.equals(date);

                        for (DataSnapshot lectureSnapshot : dateSnapshot.getChildren()) {
                            Boolean attended = lectureSnapshot.getValue(Boolean.class);
                            totalLectures++;

                            if (attended != null && attended) {
                                attendedLectures++;
                                if (isToday) {
                                    attendedToday++;
                                }
                            }

                            if (isToday) {
                                lecturesToday++;
                            }
                        }
                    }

                    // Calculate today's attendance percentage
                    double todaysAttendance = (lecturesToday > 0) ? (attendedToday * 100.0 / lecturesToday) : 0;
                    todaysAttendanceTextView.setText("Today's Attendance: " + String.format("%.2f", todaysAttendance) + "%");

                    // Calculate overall attendance percentage
                    double overallAttendance = (totalLectures > 0) ? (attendedLectures * 100.0 / totalLectures) : 0;
                    overallAttendanceTextView.setText("Overall Attendance: " + String.format("%.2f", overallAttendance) + "%");


                    // Calculate lectures the user can bunk for 80% attendance
                    int lecturesToBunkFor80 = calculateBunkFor80(attendedLectures, totalLectures);
                    bunkFor80PercentTextView.setText("Lectures you can bunk for 80%: " + lecturesToBunkFor80);

                    // Calculate lectures the user needs to attend to reach 75% attendance
                    int lecturesToAttendFor75 = calculateAttendFor75(attendedLectures, totalLectures);
                    attendFor75PercentTextView.setText("Lectures you need to attend for 75%: " + lecturesToAttendFor75);
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

    // Method to calculate how many lectures the user can bunk to maintain 80% attendance
    private int calculateBunkFor80(int attendedLectures, int totalLectures) {
        int possibleBunks = 0;
        double currentAttendance = (totalLectures > 0) ? (attendedLectures * 100.0 / totalLectures) : 0;

        while (currentAttendance >= 80.0) {
            totalLectures++;
            currentAttendance = (attendedLectures * 100.0 / totalLectures);
            possibleBunks++;
        }
        return possibleBunks - 1;
    }

    // Method to calculate how many lectures the user needs to attend to reach 75% attendance
    private int calculateAttendFor75(int attendedLectures, int totalLectures) {
        int neededLectures = 0;
        double currentAttendance = (totalLectures > 0) ? (attendedLectures * 100.0 / totalLectures) : 0;

        while (currentAttendance < 75.0) {
            totalLectures++;
            attendedLectures++;
            currentAttendance = (attendedLectures * 100.0 / totalLectures);
            neededLectures++;
        }
        return neededLectures;
    }

}


