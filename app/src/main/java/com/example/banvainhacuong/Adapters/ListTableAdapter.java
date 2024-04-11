package com.example.banvainhacuong.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.banvainhacuong.Dialogs.CartDetailDialog;
import com.example.banvainhacuong.Models.Table;
import com.example.banvainhacuong.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ListTableAdapter extends FirebaseRecyclerAdapter<Table, ListTableAdapter.TableViewHolder> {

    public ListTableAdapter(@NonNull FirebaseRecyclerOptions<Table> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TableViewHolder holder, int position, @NonNull Table model) {
        // Bind the Table object to the ViewHolder
        holder.bind(model);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Table selectedTable = getItem(holder.getAdapterPosition());
                CartDetailDialog cartDetailDialog = (CartDetailDialog) ((FragmentActivity) v.getContext()).getSupportFragmentManager().findFragmentByTag("CartDetailDialog");
                if (cartDetailDialog != null) {
                    cartDetailDialog.setTable(selectedTable);
                }
            }
        });
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_table_item, parent, false);
        return new TableViewHolder(view);
    }

    static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView lblTableName;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            lblTableName = itemView.findViewById(R.id.lblListTableName);
        }

        public void bind(Table table) {
            lblTableName.setText(table.getTableName());
        }
    }
}

