package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class calender extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView lectureRecyclerView;
    private LectureAdapter lectureAdapter;
    private ArrayList<Lecture> lectureList;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calender);

        calendarView = findViewById(R.id.calendarView);
        lectureRecyclerView = findViewById(R.id.lectureRecyclerView);
        btn = findViewById(R.id.button);
        lectureRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        lectureList = new ArrayList<>();
        lectureAdapter = new LectureAdapter(lectureList);
        lectureRecyclerView.setAdapter(lectureAdapter);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = getFormattedDate(year, month, dayOfMonth);
            fetchSubjects(selectedDate);
        });

        // Button to navigate to calculation page
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(calender.this, Calculation.class);
            startActivity(intent);
        });
    }

    private void fetchSubjects(String date) {
        DatabaseReference subjectsRef = FirebaseDatabase.getInstance().getReference("Subjects").child(date);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.getEmail() != null) {
            String userEmail = user.getEmail().replace(".", ",");
            DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference("Attendance").child(userEmail).child(date);

            Log.d("FetchSubjects", "Fetching subjects for date: " + date);

            subjectsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    lectureList.clear();
                    if (snapshot.exists()) {
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            String key = childSnapshot.getKey();
                            String lectureInfo = (String) childSnapshot.getValue();
                            if (lectureInfo != null) {
                                Lecture lecture = new Lecture(key, lectureInfo, date);
                                lectureList.add(lecture);

                                // Ensure attendance for this lecture is initialized in Firebase
                                attendanceRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot attendanceSnapshot) {
                                        if (!attendanceSnapshot.exists()) {
                                            // If attendance is not yet recorded, set it to false
                                            attendanceRef.child(key).setValue(false);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("Firebase", "Failed to set default attendance state", error.toException());
                                    }
                                });
                            }
                        }
                        lectureAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(calender.this, "No subjects found for this date", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(calender.this, "Failed to load subjects", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getFormattedDate(int year, int month, int day) {
        month += 1;
        return year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
    }
}