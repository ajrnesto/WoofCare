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
import com.woofcare.Objects.Memory;
import com.woofcare.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.memoryViewHolder>{

    private static final FirebaseDatabase WOOF_CARE_DB = FirebaseDatabase.getInstance();
    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbMemory;

    Context context;
    ArrayList<Memory> arrMemory = new ArrayList<>();
    private MemoryAdapter.OnMemoryListener mOnMemoryListener;

    public MemoryAdapter(Context context, ArrayList<Memory> arrMemory, MemoryAdapter.OnMemoryListener onMemoryListener) {
        this.context = context;
        this.arrMemory = arrMemory;
        this.mOnMemoryListener = onMemoryListener;
    }

    @NonNull
    @Override
    public MemoryAdapter.memoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_memory, parent, false);
        return new MemoryAdapter.memoryViewHolder(view, mOnMemoryListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoryAdapter.memoryViewHolder holder, int position) {
        Memory memory = arrMemory.get(position);

        String title = memory.getTitle();
        long timestamp = memory.getTimestamp();
        String journal = memory.getJournal();

        holder.tvTitle.setText(title);
        holder.tvJournal.setText(journal);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        holder.tvDate.setText(sdf.format(timestamp));
    }

    @Override
    public int getItemCount() {
        return arrMemory.size();
    }

    public class memoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTitle, tvDate, tvJournal;
        MemoryAdapter.OnMemoryListener onMemoryListener;

        public memoryViewHolder(@NonNull View itemView, MemoryAdapter.OnMemoryListener onMemoryListener) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvJournal = itemView.findViewById(R.id.tvJournal);

            this.onMemoryListener = onMemoryListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onMemoryListener.onMemoryClick(getAdapterPosition());
        }
    }

    public interface OnMemoryListener{
        void onMemoryClick(int position);
    }
}