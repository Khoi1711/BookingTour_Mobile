package com.example.banvainhacuong.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.banvainhacuong.Models.Bill;
import com.example.banvainhacuong.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class BillAdapter extends FirebaseRecyclerAdapter<Bill, BillAdapter.BillViewHolder> {

    public interface ItemClickListener {
        void onClick(Bill bill);
    }

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public BillAdapter(@NonNull FirebaseRecyclerOptions<Bill> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BillViewHolder holder, int position, @NonNull Bill model) {
        holder.lblTotalCost.setText(String.format("%.0f",model.getTotalCost()) + "đ");
        holder.lblBillId.setText(model.getBillId());

        Bill bill = getItem(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(bill);
                }
            }
        });
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bill_item, parent, false);
        return new BillViewHolder(view);
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView lblTotalCost,lblBillId;

        public BillViewHolder(View itemView) {
            super(itemView);
            lblTotalCost = itemView.findViewById(R.id.lblTotalCost);
            lblBillId = itemView.findViewById(R.id.lblBillId);
        }
    }
}

