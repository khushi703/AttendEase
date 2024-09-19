package com.example.project;

import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class calender extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView lectureRecyclerView;
    private LectureAdapter lectureAdapter;
    private ArrayList<Lecture> lectureList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calender);

        calendarView = findViewById(R.id.calendarView);
        lectureRecyclerView = findViewById(R.id.lectureRecyclerView);
        lectureRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        lectureList = new ArrayList<>();
        lectureAdapter = new LectureAdapter(lectureList);
        lectureRecyclerView.setAdapter(lectureAdapter);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = getFormattedDate(year, month, dayOfMonth);
            fetchSubjects(selectedDate);
        });
    }

    private void fetchSubjects(String date) {
        DatabaseReference subjectsRef = FirebaseDatabase.getInstance().getReference("Subjects").child(date);
        Log.d("FetchSubjects", "Fetching subjects for date: " + date);

        subjectsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lectureList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String key = childSnapshot.getKey();
                        String lectureInfo= (String) childSnapshot.getValue();
                        if (lectureInfo != null) {
                            lectureList.add(new Lecture(key, lectureInfo));
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

    private String getFormattedDate(int year, int month, int day) {
        month += 1;
        return year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
    }
}