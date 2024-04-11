package com.example.banvainhacuong.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.banvainhacuong.Models.Table;
import com.example.banvainhacuong.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class TableAdapter extends FirebaseRecyclerAdapter<Table, TableAdapter.TableViewHolder> {
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public TableAdapter(@NonNull FirebaseRecyclerOptions<Table> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TableViewHolder holder, int position, @NonNull Table model) {
        holder.lblTableName.setText(model.getTableName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_item, parent, false);
        return new TableViewHolder(view);
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView lblTableName;

        public TableViewHolder(View itemView) {
            super(itemView);
            lblTableName = itemView.findViewById(R.id.lblTableName);
        }
    }
}

