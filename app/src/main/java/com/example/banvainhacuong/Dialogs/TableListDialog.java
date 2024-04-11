package com.example.banvainhacuong.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.banvainhacuong.Adapters.ListTableAdapter;
import com.example.banvainhacuong.Models.FirebaseHelper;
import com.example.banvainhacuong.Models.Table;
import com.example.banvainhacuong.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class TableListDialog extends DialogFragment {
    private RecyclerView recyclerViewTables;
    private ListTableAdapter listTableAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_table_list, null);

        recyclerViewTables = view.findViewById(R.id.recycleViewListTable);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewTables.setLayoutManager(gridLayoutManager);

        FirebaseHelper firebaseHelper = new FirebaseHelper("tables", getContext());

        FirebaseRecyclerOptions<Table> options = new FirebaseRecyclerOptions.Builder<Table>()
                .setQuery(firebaseHelper.myRef, Table.class)
                .build();

        listTableAdapter = new ListTableAdapter(options);
        recyclerViewTables.setAdapter(listTableAdapter);

        builder.setView(view)
                .setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        listTableAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        listTableAdapter.stopListening();
    }
}

