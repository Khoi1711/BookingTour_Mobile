package com.example.banvainhacuong.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.banvainhacuong.Adapters.ProductAdapter;
import com.example.banvainhacuong.Dialogs.AddProductDialog;
import com.example.banvainhacuong.Dialogs.ProductDetailDialog;
import com.example.banvainhacuong.Fragments.ProductFragment;
import com.example.banvainhacuong.MainActivity;
import com.example.banvainhacuong.Models.FirebaseHelper;
import com.example.banvainhacuong.Models.Product;
import com.example.banvainhacuong.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ProductManagementActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener, ProductFragment.OnAdapterUpdatedListener{

    private FirebaseHelper firebaseHelper;
    private ProductAdapter adapter; // Khai báo adapter

    private ProductFragment productFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);

        productFragment = (ProductFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentProductManage);
        firebaseHelper = new FirebaseHelper("products", this);

        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(firebaseHelper.myRef, Product.class)
                .build();

        adapter = new ProductAdapter(options);
        adapter.startListening(); // Bắt đầu adapter

        FloatingActionButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductManagementActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        FloatingActionButton btnAdd = findViewById(R.id.btnAddProduct);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProductDialog dialog = new AddProductDialog();
                dialog.show(getSupportFragmentManager(), "ProductManageDialog");
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        try {
            if (position >= 0 && position < adapter.getItemCount()) {
                Product product = adapter.getItem(position);
                ProductDetailDialog dialog = new ProductDetailDialog(product);
                dialog.show(getSupportFragmentManager(), "ProductDetailDialog");
            } else {
                Toast.makeText(ProductManagementActivity.this, "Position: " + position
                                + "\n Adapter length: " + adapter.getItemCount(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            Toast.makeText(ProductManagementActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAdapterUpdated() {
        ProductAdapter productFragmentAdapter = productFragment.getAdapter();
        this.adapter = productFragmentAdapter;
        this.adapter.notifyDataSetChanged();
    }
}