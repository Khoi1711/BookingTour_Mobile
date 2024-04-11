package com.example.banvainhacuong.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.banvainhacuong.Models.Cart;
import com.example.banvainhacuong.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Cart> listCart;
    public CartAdapter(List<Cart> listCart) {
        this.listCart = listCart;
    }
    private CartChangeListener cartChangeListener;

    public interface CartChangeListener {
        void onCartChange();
    }

    public List<Cart> getListCart() {
        return listCart;
    }

    public CartAdapter(List<Cart> listCart, CartChangeListener cartChangeListener) {
        this.listCart = listCart;
        this.cartChangeListener = cartChangeListener;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);

        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cart = listCart.get(position);
        // Update your holder with cart object
        holder.productName.setText(cart.getProduct().getProductName());
        holder.amount.setText("x" + cart.getAmount());
        holder.toCast.setText(String.format("%.0f", cart.toCast()));
    }

    @Override
    public int getItemCount() {
        return listCart.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        public TextView productName, amount, toCast, deleteCartItem;

        public CartViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.lblCartProductName);
            amount = itemView.findViewById(R.id.lblCartProductAmount);
            toCast = itemView.findViewById(R.id.lblCartProductTotalPrice);
            deleteCartItem = itemView.findViewById(R.id.lblDeleteCartItem);
            deleteCartItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listCart.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    if (cartChangeListener != null) {
                        cartChangeListener.onCartChange();
                    }
                }
            });
        }
    }
}
