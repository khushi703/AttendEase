// LectureAdapter.java
package com.example.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.attendanceCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Store attendance status in Firebase here
        });
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
