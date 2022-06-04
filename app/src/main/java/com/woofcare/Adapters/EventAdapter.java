package com.woofcare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.woofcare.Objects.Event;
import com.woofcare.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.eventViewHolder>{

    private static final FirebaseDatabase WOOF_CARE_DB = FirebaseDatabase.getInstance();
    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbEvents;

    Context context;
    ArrayList<Event> arrEvents = new ArrayList<>();
    private EventAdapter.OnEventListener mOnEventListener;

    public EventAdapter(Context context, ArrayList<Event> arrEvents, EventAdapter.OnEventListener onEventListener) {
        this.context = context;
        this.arrEvents = arrEvents;
        this.mOnEventListener = onEventListener;
    }

    @NonNull
    @Override
    public EventAdapter.eventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_event, parent, false);
        return new EventAdapter.eventViewHolder(view, mOnEventListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.eventViewHolder holder, int position) {
        Event event = arrEvents.get(position);

        String title = event.getTitle();
        long timestamp = event.getTimestamp();
        String journal = event.getJournal();

        holder.tvTitle.setText(title);
        holder.tvJournal.setText(journal);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        holder.tvDate.setText(sdf.format(timestamp));
    }

    @Override
    public int getItemCount() {
        return arrEvents.size();
    }

    public class eventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTitle, tvDate, tvJournal;
        EventAdapter.OnEventListener onEventListener;

        public eventViewHolder(@NonNull View itemView, EventAdapter.OnEventListener onEventListener) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvJournal = itemView.findViewById(R.id.tvJournal);

            this.onEventListener = onEventListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onEventListener.onEventClick(getAdapterPosition());
        }
    }

    public interface OnEventListener{
        void onEventClick(int position);
    }
}