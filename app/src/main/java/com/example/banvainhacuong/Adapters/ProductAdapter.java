package com.example.banvainhacuong.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.banvainhacuong.Models.Product;
import com.example.banvainhacuong.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


public class    ProductAdapter extends FirebaseRecyclerAdapter<Product, ProductAdapter.ProductViewHolder> {
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public ProductAdapter(@NonNull FirebaseRecyclerOptions<Product> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
        holder.lblProductName.setText(model.getProductName());
        holder.lblPrice.setText(String.valueOf(model.getProductPrice()) + "đ");
        holder.lblInventory.setText("Còn lại: " + String.valueOf(model.getInventory()));
        Glide.with(holder.imgProduct.getContext())
                .load(model.getProductImage())
                .into(holder.imgProduct);
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
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView lblProductName;
        TextView lblPrice;
        TextView lblInventory;

        public ProductViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            lblProductName = itemView.findViewById(R.id.lblProductName);
            lblPrice = itemView.findViewById(R.id.lblPrice);
            lblInventory = itemView.findViewById(R.id.lblInventory);
        }
    }
}
