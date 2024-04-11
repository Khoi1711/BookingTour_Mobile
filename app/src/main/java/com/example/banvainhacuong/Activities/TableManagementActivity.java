package com.example.banvainhacuong.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.banvainhacuong.Adapters.TableAdapter;
import com.example.banvainhacuong.MainActivity;
import com.example.banvainhacuong.Models.FirebaseHelper;
import com.example.banvainhacuong.Models.Table;
import com.example.banvainhacuong.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class TableManagementActivity extends AppCompatActivity  implements TableAdapter.OnItemClickListener {
    private FirebaseHelper firebaseHelper;
    private TableAdapter adapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_management);

        firebaseHelper = new FirebaseHelper("tables", this);
        progressBar = findViewById(R.id.progressBarTable);
        recyclerView = findViewById(R.id.tableRecycleView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        FirebaseRecyclerOptions<Table> options = new FirebaseRecyclerOptions.Builder<Table>()
                .setQuery(firebaseHelper.myRef, Table.class)
                .build();

        adapter = new TableAdapter(options);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        // Ẩn ProgressBar
        registerDataObserver();

        FloatingActionButton btnBack = findViewById(R.id.btnBackTable);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TableManagementActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        FloatingActionButton btnAdd = findViewById(R.id.btnAddTable);
        setupButtonAddTable(btnAdd);

        FloatingActionButton btnDelete = findViewById(R.id.btnDeleteTable);
        setupButtonDeleteTable(btnDelete);
    }

    @Override
    public void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onItemClick(int position) {

    }

    // Hàm ẩn ProgressBar
    private void registerDataObserver() {
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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
    }

    private void setupButtonAddTable(FloatingActionButton btnAdd){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tabneId = firebaseHelper.myRef.push().getKey();
                firebaseHelper.myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long tableCount = dataSnapshot.getChildrenCount();
                        String newTableName = "Bàn " + (tableCount + 1);
                        Table newTable = new Table(tabneId, newTableName);
                        firebaseHelper.addItem(newTable, tabneId);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Xử lý lỗi
                        Toast.makeText(TableManagementActivity.this, "Đã xảy ra lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setupButtonDeleteTable(FloatingActionButton btnDelete){
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseHelper.myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long tableCount = dataSnapshot.getChildrenCount();
                        String lastTableId = null;
                        for (DataSnapshot tableSnapshot : dataSnapshot.getChildren()) {
                            Table table = tableSnapshot.getValue(Table.class);
                            if (table.getTableName().equals("Bàn " + tableCount)) {
                                lastTableId = table.getTableId();
                                break;
                            }
                        }
                        if (lastTableId != null) {
                            firebaseHelper.deleteItem(lastTableId, null);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Xử lý lỗi
                        Toast.makeText(TableManagementActivity.this, "Đã xảy ra lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}