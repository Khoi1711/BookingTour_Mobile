package com.example.banvainhacuong.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.banvainhacuong.Adapters.BillAdapter;
import com.example.banvainhacuong.Dialogs.BillDetailDialog;
import com.example.banvainhacuong.MainActivity;
import com.example.banvainhacuong.Models.Bill;
import com.example.banvainhacuong.Models.Cart;
import com.example.banvainhacuong.Models.FirebaseHelper;
import com.example.banvainhacuong.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class BillManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBillDetail;
    private BillAdapter billAdapter;
    private ProgressBar progressBar;
    private FirebaseHelper firebaseHelper, firebaseHelperProductBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_management);

        firebaseHelper = new FirebaseHelper("bills", this);
        firebaseHelperProductBill = new FirebaseHelper("product_bills", this);

        progressBar = findViewById(R.id.progressBarBill);

        recyclerViewBillDetail = findViewById(R.id.recycleViewBill);
        recyclerViewBillDetail.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Bill> options = new FirebaseRecyclerOptions.Builder<Bill>()
                .setQuery(firebaseHelper.myRef, Bill.class)
                .build();

        billAdapter = new BillAdapter(options);
        recyclerViewBillDetail.setAdapter(billAdapter);

        billAdapter.setItemClickListener(new BillAdapter.ItemClickListener() {
            @Override
            public void onClick(Bill bill) {
                try {
                    final List<Cart> listCart = new ArrayList<>();
                    firebaseHelperProductBill.getProductBillListByBillId(listCart, bill.getBillId());

                    firebaseHelperProductBill.setLoadDataListener(new FirebaseHelper.LoadDataListener() {
                        @Override
                        public void onDataLoaded() {
                            BillDetailDialog billDetailDialog = new BillDetailDialog(listCart, bill);
                            billDetailDialog.show(getSupportFragmentManager(), "BillDetailDialog");
                        }
                    });
                } catch (Exception e){
                    Toast.makeText(BillManagementActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Ẩn ProgressBar
        registerDataObserver();

        FloatingActionButton btnBack = findViewById(R.id.btnBackBill);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BillManagementActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        billAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        billAdapter.stopListening();
    }

    // Hàm ẩn ProgressBar
    private void registerDataObserver() {
       try {
           billAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
               @Override
               public void onChanged() {
                   super.onChanged();
                   progressBar.setVisibility(View.GONE);
               }

               @Override
               public void onItemRangeInserted(int positionStart, int itemCount) {
                   super.onItemRangeInserted(positionStart, itemCount);
                   progressBar.setVisibility(View.GONE);
               }
           });
       } catch (Exception e){
           Toast.makeText(BillManagementActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
       }
    }
}