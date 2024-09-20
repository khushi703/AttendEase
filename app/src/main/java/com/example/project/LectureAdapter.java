package com.example.project;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.ViewHolder> {

    private ArrayList<Lecture> lectureList;

    public LectureAdapter(ArrayList<Lecture> lectureList) {
        this.lectureList = lectureList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lecture, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lecture lecture = lectureList.get(position);
        holder.subjectName.setText(lecture.getName());
        holder.lectureInfo.setText(lecture.getInfo());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userEmail = user.getEmail();
            if (userEmail != null) {
                String emailKey = userEmail.replace(".", ",");
                String date = lecture.getDate();
                String lectureName = lecture.getName();

                // Reference to the user's attendance
                DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference("Attendance")
                        .child(emailKey).child(date).child(lectureName);

                // Set the checkbox state based on Firebase data
                attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean isChecked = snapshot.getValue(Boolean.class);
                        holder.attendanceCheckBox.setChecked(isChecked != null && isChecked);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Failed to read checkbox state", error.toException());
                    }
                });

                // Add listener for checkbox changes
                holder.attendanceCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    // Store true or false in Firebase based on checkbox state
                    attendanceRef.setValue(isChecked);
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return lectureList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName;
        TextView lectureInfo;
        CheckBox attendanceCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subjectName);
            lectureInfo = itemView.findViewById(R.id.lectureInfo);
            attendanceCheckBox = itemView.findViewById(R.id.attendanceCheckBox);
        }
    }
}
