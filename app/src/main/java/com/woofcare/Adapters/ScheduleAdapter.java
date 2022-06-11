package com.woofcare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.woofcare.Objects.Schedule;
import com.woofcare.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.scheduleViewHolder>{

    private static final FirebaseDatabase WOOF_CARE_DB = FirebaseDatabase.getInstance();
    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbSchedule, dbPet;

    Context context;
    ArrayList<Schedule> arrSchedule = new ArrayList<>();
    private ScheduleAdapter.OnScheduleListener mOnScheduleListener;
    boolean visiblePastSchedules;

    public ScheduleAdapter(Context context, ArrayList<Schedule> arrSchedule, ScheduleAdapter.OnScheduleListener onScheduleListener, boolean visiblePastSchedules) {
        this.context = context;
        this.arrSchedule = arrSchedule;
        this.mOnScheduleListener = onScheduleListener;
        this.visiblePastSchedules = visiblePastSchedules;
    }

    @NonNull
    @Override
    public ScheduleAdapter.scheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_schedule, parent, false);
        return new ScheduleAdapter.scheduleViewHolder(view, mOnScheduleListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleAdapter.scheduleViewHolder holder, int position) {
        Schedule schedule = arrSchedule.get(position);

        String petUid = schedule.getPetUid();
        String title = schedule.getTitle();
        String details = schedule.getDetails();
        long timestamp = schedule.getTimestamp();
        String category = schedule.getCategory();

        dbPet = WOOF_CARE_DB.getReference("user_"+USER.getUid()+"_pets/"+petUid);
        dbPet.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String petName = snapshot.child("name").getValue().toString();
                holder.tvPetName.setText(petName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.tvTitle.setText(title);
        holder.tvDetails.setText(details);
        loadScheduleTime(holder, timestamp);
        loadCategory(holder, category);

        if (timestamp < System.currentTimeMillis()) {
            holder.tvWeek.setTextColor(context.getResources().getColor(R.color.blue_cold_front));
            holder.tvDay.setTextColor(context.getResources().getColor(R.color.blue_cold_front));
            holder.tvMonth.setTextColor(context.getResources().getColor(R.color.blue_cold_front));
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.blue_cold_front));
            holder.tvDetails.setTextColor(context.getResources().getColor(R.color.blue_cold_front));
            holder.tvCategory.setTextColor(context.getResources().getColor(R.color.blue_cold_front));
            holder.tvPetName.setTextColor(context.getResources().getColor(R.color.blue_cold_front));
            holder.ivCategoryIcon.setColorFilter(context.getResources().getColor(R.color.blue_cold_front));
            holder.ivPawIcon.setColorFilter(context.getResources().getColor(R.color.blue_cold_front));
            if (visiblePastSchedules) {
                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            else {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }
        }
        else {
            holder.tvWeek.setTextColor(context.getResources().getColor(R.color.blue_oracle));
            holder.tvDay.setTextColor(context.getResources().getColor(R.color.blue_oracle));
            holder.tvMonth.setTextColor(context.getResources().getColor(R.color.blue_oracle));
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.blue_oracle));
            holder.tvDetails.setTextColor(context.getResources().getColor(R.color.blue_oracle));
            holder.tvCategory.setTextColor(context.getResources().getColor(R.color.blue_oracle));
            holder.tvPetName.setTextColor(context.getResources().getColor(R.color.blue_oracle));
            holder.ivCategoryIcon.setColorFilter(context.getResources().getColor(R.color.blue_oracle));
            holder.ivPawIcon.setColorFilter(context.getResources().getColor(R.color.blue_oracle));
        }
    }

    private void loadScheduleTime(scheduleViewHolder holder, long timestamp) {
        SimpleDateFormat sdfWeek = new SimpleDateFormat("EEE");
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MMM");

        holder.tvWeek.setText(sdfWeek.format(timestamp));
        holder.tvDay.setText(sdfDay.format(timestamp));
        holder.tvMonth.setText(sdfMonth.format(timestamp));
    }

    private void loadCategory(scheduleViewHolder holder, String category) {
        if (category.equalsIgnoreCase("Food")) {
            holder.ivCategoryIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.apple_whole_24));
        }
        else if (category.equalsIgnoreCase("Exercise")) {
            holder.ivCategoryIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.bolt_24));
        }
        else if (category.equalsIgnoreCase("Medication")) {
            holder.ivCategoryIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.medicine_24));
        }
        else if (category.equalsIgnoreCase("Vaccination")) {
            holder.ivCategoryIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.syringe_24));
        }
        holder.tvCategory.setText(category);
    }

    @Override
    public int getItemCount() {
        return arrSchedule.size();
    }

    public class scheduleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        AppCompatImageView ivCategoryIcon, ivPawIcon;
        TextView tvTitle, tvDetails, tvCategory, tvPetName, tvWeek, tvDay, tvMonth;
        ConstraintLayout constraintLayout;
        ScheduleAdapter.OnScheduleListener onScheduleListener;

        public scheduleViewHolder(@NonNull View itemView, ScheduleAdapter.OnScheduleListener onScheduleListener) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            ivPawIcon = itemView.findViewById(R.id.ivPawIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPetName = itemView.findViewById(R.id.tvPetName);
            tvWeek = itemView.findViewById(R.id.tvWeek);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvMonth = itemView.findViewById(R.id.tvMonth);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);

            this.onScheduleListener = onScheduleListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onScheduleListener.onScheduleClick(getAdapterPosition());
        }
    }

    public interface OnScheduleListener{
        void onScheduleClick(int position);
    }
}
